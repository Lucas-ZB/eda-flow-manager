package br.com.lucas.pitanga.resource;

import br.com.lucas.pitanga.client.AnalyticsClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/physics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PhysicsResource {

    @RestClient
    AnalyticsClient analyticsClient;

    @POST
    public AnalyticsClient.TransistorResponse calculate(AnalyticsClient.TransistorRequest request) {
        // O Gerente recebe do Front-end e repassa para o Motor de Física
        return analyticsClient.analyzePhysics(request);
    }
}
