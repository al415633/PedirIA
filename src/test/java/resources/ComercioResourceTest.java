package resources;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ComercioResourceTest {


    @Test
    public void testLoginConCredencialesIncorrectas() {
        String loginJson = """
        {
            "correo": "correo@ejemplo.com",
            "password": "passwordIncorrecto"
        }
        """;

        given()
                .contentType("application/json") // Enviar JSON correctamente
                .body(loginJson)
                .when()
                .post("/comercio/login")
                .then()
                .statusCode(401) // Código correcto para credenciales incorrectas
                .body(containsString("Credenciales incorrectas"));
    }

    @Test
    public void testObtenerPerfilSinSesion() {
        given()
                .when()
                .get("/comercio/obtener")
                .then()
                .statusCode(401)
                .body(equalTo("No hay sesión activa"));
    }

}
