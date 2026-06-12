package br.com.lucas.pitanga.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class StaResourceTest {

    @Test
    public void testCaminhoCriticoPortaAnd() {
        // Grafo: IN (5ps) -> AND (25ps) -> OUT (5ps)
        String jsonPayload = """
            {
              "nodes": [
                { "id": "IN", "type": "PAD", "delayPs": 5 },
                { "id": "U1", "type": "AND", "delayPs": 25 },
                { "id": "OUT", "type": "PAD", "delayPs": 5 }
              ],
              "edges": [
                { "from": "IN", "to": "U1" },
                { "from": "U1", "to": "OUT" }
              ]
            }
            """;

        // DADO QUE eu envio uma netlist de 35 picossegundos no total
        given()
            .header("Content-Type", "application/json")
            .body(jsonPayload)
        // QUANDO o motor STA analisa os caminhos
        .when()
            .post("/api/sta")
        // ENTÃO o caminho crítico deve ser 35ps e a FMax 28.57 GHz
        .then()
            .statusCode(200)
            .body("criticalPathDelayPs", is(35))
            .body("maxFreqGhz", is(28.57f));
    }
}
