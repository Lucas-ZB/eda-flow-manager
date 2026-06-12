package br.com.lucas.pitanga.resource;

import br.com.lucas.pitanga.entity.CircuitProject;
import br.com.lucas.pitanga.client.AnalyticsClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @RestClient
    AnalyticsClient analyticsClient;

    // O Envelope: Recebe os metadados do projeto E a Netlist desenhada pelo Front-end
    public static class ProjectPayload {
        public String name;
        public String authorName;
        public String targetBoard;
        public AnalyticsClient.GraphRequest netlist;
    }

    @GET
    public List<CircuitProject> listAll() {
        return CircuitProject.listAll();
    }

    @POST
    @Transactional
    public Response create(ProjectPayload payload) {
        CircuitProject project = new CircuitProject();
        project.name = payload.name;
        project.authorName = payload.authorName;
        project.targetBoard = payload.targetBoard;

        try {
            // O Gerente apenas repassa o Grafo complexo para o Motor Matemático
            AnalyticsClient.StaResult result = analyticsClient.analyzeTiming(payload.netlist);

            project.criticalPathPs = result.criticalPathDelayPs;
            project.maxFreqGhz = result.maxFreqGhz;
            
            // Regra de Negócio: Baixamos a meta para 10 GHz para permitir testarmos circuitos maiores
            if (project.maxFreqGhz >= 10.0) {
                project.verificationStatus = "PASSED";
            } else {
                project.verificationStatus = "FAILED (Timing Violation)";
            }
        } catch (Exception e) {
            project.verificationStatus = "ERROR_CONNECTION";
            System.out.println("Erro: " + e.getMessage());
        }

        project.persist();
        return Response.status(Response.Status.CREATED).entity(project).build();
    }
}
