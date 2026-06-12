package br.com.lucas.pitanga.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TransistorResourceTest {

    @Test
    public void testTransistorEmSaturacao() {
        String jsonPayload = """
            {
                "vdd": 1.2,
                "widthNm": 360.0,
                "cLoadFf": 50.0
            }
            """;

        // DADO QUE eu tenho os parâmetros clássicos de 180nm
        given()
            .header("Content-Type", "application/json")
            .body(jsonPayload)
        // QUANDO eu peço a análise física
        .when()
            .post("/api/transistor")
        // ENTÃO eu espero 160uA de corrente e 187.5ps de atraso
        .then()
            .statusCode(200)
            .body("saturationCurrentUa", is(160.0f))
            .body("propagationDelayPs", is(187.5f))
            .body("operationRegion", is("SATURAÇÃO"));
    }

    @Test
    public void testTransistorEmCorte() {
        String jsonPayload = """
            {
                "vdd": 0.3, 
                "widthNm": 360.0,
                "cLoadFf": 50.0
            }
            """;

        // DADO QUE o Vdd (0.3V) está abaixo da Tensão de Limiar (0.4V)
        given()
            .header("Content-Type", "application/json")
            .body(jsonPayload)
        // QUANDO a análise roda
        .when()
            .post("/api/transistor")
        // ENTÃO a corrente deve ser zero e o estado "CORTE"
        .then()
            .statusCode(200)
            .body("saturationCurrentUa", is(0.0f))
            .body("operationRegion", is("CORTE (DESLIGADO)"));
    }
}
