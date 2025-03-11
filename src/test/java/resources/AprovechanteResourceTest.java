package resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class AprovechanteResourceTest {

    @Test
    public void testObtenerPerfilSinSesion() {
        given()
                .when()
                .get("/aprovechante/obtener")
                .then()
                .statusCode(401)
                .body(equalTo("No hay sesión activa"));
    }


    @Test
    public void testEliminarAprovechanteNoExistente() {
        given()
                .when()
                .delete("/aprovechante/delete/correoNoExistente@ejemplo.com")
                .then()
                .statusCode(404); // Código 404 si el aprovechante no existe
    }
}