package br.com.lucas.pitanga.resource;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;

import br.com.lucas.pitanga.client.AnalyticsClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class ProjectResourceTest {

    // Mock do cliente REST para isolar este teste do outro microsserviço
    @InjectMock
    @RestClient
    AnalyticsClient analyticsClientMock;

    @Test
    @DisplayName("Deve APROVAR o projeto quando a frequência for MAIOR que 10 GHz")
    public void testProjetoAprovado() {

        // Mock deve retornar 15.0 GHz

        AnalyticsClient.StaResult mockResult = new AnalyticsClient.StaResult();
        mockResult.criticalPathDelayPs = 66; // 1000 / 66 = ~15 GHz
        mockResult.maxFreqGhz = 15.0;
        
        Mockito.when(analyticsClientMock.analyzeTiming(any())).thenReturn(mockResult);

        String payloadJson = """
            {
                "name": "ALU 32-bit (Test)",
                "authorName": "Lucas Basso",
                "targetBoard": "FPGA",
                "netlist": { "nodes": [], "edges": [] }
            }
            """;

        // AÇÃO (When) e VALIDAÇÃO (Then)
        given()
          .contentType("application/json")
          .body(payloadJson)
        .when()
          .post("/api/projects")
        .then()
          .statusCode(201) // 201 CREATED (Salvo no banco com sucesso)
          .body("verificationStatus", is("PASSED"))
          .body("maxFreqGhz", is(15.0f));
    }

    @Test
    @DisplayName("Deve REPROVAR o projeto quando a frequência for MENOR que 10 GHz")

    public void testProjetoReprovado() {

        //Mock deve  retornar apenas 5.0 GHz (Chip Lento)
        
        AnalyticsClient.StaResult mockResult = new AnalyticsClient.StaResult();
        mockResult.criticalPathDelayPs = 200; // 1000 / 200 = 5 GHz
        mockResult.maxFreqGhz = 5.0;
        
        Mockito.when(analyticsClientMock.analyzeTiming(any())).thenReturn(mockResult);

        String payloadJson = """
            {
                "name": "Chip Lento (Test)",
                "authorName": "Lucas Basso",
                "targetBoard": "ASIC",
                "netlist": { "nodes": [], "edges": [] }
            }
            """;

        // AÇÃO (When) e VALIDAÇÃO (Then)
        given()
          .contentType("application/json")
          .body(payloadJson)
        .when()
          .post("/api/projects")
        .then()
          .statusCode(201)
          .body("verificationStatus", is("FAILED (Timing Violation)"))
          .body("maxFreqGhz", is(5.0f));
    }
}