package br.com.lucas.pitanga.client;

/**
 * CLIENTE REST: COMUNICAÇÃO ENTRE MICROSSERVIÇOS
 * -------------------------------------------------------------------------
 * Interface responsável por estabelecer a ponte de comunicação HTTP entre o Web Service 1 (Project Manager) e o Web Service 2 (Analytics Engine).

 * Utiliza a extensão REST Client do Quarkus para realizar chamadas 
 
 * Garante o desacoplamento do sistema: o Gerente não precisa saber como a física do semicondutor é calculada, 
 * apenas delega a carga computacional para o Motor e aguarda o retorno.
 * 
 */

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@Path("/api")
@RegisterRestClient(configKey="analytics-api")
public interface AnalyticsClient {

    @POST
    @Path("/sta")
    StaResult analyzeTiming(GraphRequest request);

    @POST
    @Path("/transistor")
    TransistorResponse analyzePhysics(TransistorRequest request);

    // DTOs do STA
    class Node { public String id; public String type; public int delayPs; public Node(String id, String type, int delayPs) { this.id = id; this.type = type; this.delayPs = delayPs; } }
    class Edge { public String from; public String to; public Edge(String from, String to) { this.from = from; this.to = to; } }
    class GraphRequest { public List<Node> nodes; public List<Edge> edges; }
    class StaResult { public int criticalPathDelayPs; public List<String> criticalPathNodes; public double maxFreqGhz; }

    // DTOs da Física do Transistor
    class TransistorRequest { public double vdd; public double widthNm; public double cLoadFf; }
    class TransistorResponse { public double saturationCurrentUa; public double propagationDelayPs; public String operationRegion; }
}
