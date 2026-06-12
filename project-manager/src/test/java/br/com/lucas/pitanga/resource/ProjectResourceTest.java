package br.com.lucas.pitanga.resource;

import br.com.lucas.pitanga.client.AnalyticsClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class ProjectResourceTest {

    // 1. A Mágica do Mockito: Substituímos o cliente real por um Dublê!
    @InjectMock
    @RestClient
    AnalyticsClient analyticsClientMock;

    @Test
    public void testCriacaoDeProjetoComSucessoNoTiming() {
        
        // 2. Treinando o Dublê (Configuração do Mockito)
        // Criamos uma resposta falsa de sucesso (FMax = 25 GHz)
        AnalyticsClient.StaResult mockResult = new AnalyticsClient.StaResult();
        mockResult.criticalPathDelayPs = 40;
        mockResult.maxFreqGhz = 25.0;

        // Dizemos ao Mockito: "Quando o Gerente tentar chamar o analyzeTiming com QUALQUER grafo, devolva o mockResult"
        Mockito.when(analyticsClientMock.analyzeTiming(Mockito.any())).thenReturn(mockResult);

        // 3. O Payload que simula o Front-end enviando os dados
        String jsonPayload = """
            {
                "name": "Chip_Teste_Mockito",
                "authorName": "Engenheiro QA",
                "targetBoard": "SIMPLE_AND",
                "netlist": {
                    "nodes": [],
                    "edges": []
                }
            }
            """;

        // 4. A Execução do Teste (REST Assured)
        given()
            .header("Content-Type", "application/json")
            .body(jsonPayload)
        .when()
            .post("/api/projects")
        .then()
            .statusCode(201) // O Gerente deve conseguir salvar no banco H2
            .body("id", notNullValue())
            .body("name", is("Chip_Teste_Mockito"))
            .body("maxFreqGhz", is(25.0f)) // Confirma que o Gerente usou o valor do Mock!
            .body("verificationStatus", containsString("PASSED")); // Passou da meta de 10GHz
    }

    @Test
    public void testCriacaoDeProjetoComViolacaoDeTiming() {
        
        // Treinando o Dublê para simular uma falha física (FMax = 5 GHz)
        AnalyticsClient.StaResult mockResultLento = new AnalyticsClient.StaResult();
        mockResultLento.criticalPathDelayPs = 200;
        mockResultLento.maxFreqGhz = 5.0;

        Mockito.when(analyticsClientMock.analyzeTiming(Mockito.any())).thenReturn(mockResultLento);

        String jsonPayload = """
            {
                "name": "Chip_Lento_Mockito",
                "authorName": "Engenheiro QA",
                "targetBoard": "RING_OSC",
                "netlist": { "nodes": [], "edges": [] }
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(jsonPayload)
        .when()
            .post("/api/projects")
        .then()
            .statusCode(201)
            .body("maxFreqGhz", is(5.0f))
            // Confirma que o Gerente aplicou a regra de negócio correta para chips lentos
            .body("verificationStatus", containsString("FAILED")); 
    }
}
