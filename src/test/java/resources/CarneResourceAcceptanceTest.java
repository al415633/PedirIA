package resources;

import data.carniceria.Carne;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.carne.CarneResource;
import services.ProductoDAO;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.List;

@QuarkusTest
@TestHTTPEndpoint(CarneResource.class)
public class CarneResourceAcceptanceTest {

    @Inject
    ProductoDAO<Carne> daoProducto;

    private Carne carne;

    @BeforeEach
    @Transactional
    public void setup() {
        carne = new Carne();
        carne.setNombre("Pechuga de Pollo");
        carne.setUnidad("kg");
        carne.setIdNegocio(1);
        daoProducto.create(carne);
    }

    @Test
    public void testGetAllCarne() {
        given()
          .when().get()
          .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].nombre", notNullValue());
    }

    @Test
    public void testGetCarneById() {
        given()
          .when().get("/" + carne.getId())
          .then()
            .statusCode(200)
            .body("nombre", equalTo("Pechuga de Pollo"))
            .body("unidad", equalTo("kg"));
    }

    @Test
    public void testCreateCarne() {
        Carne nueva = new Carne();
        nueva.setNombre("Costilla");
        nueva.setUnidad("kg");

        given()
          .contentType("application/json")
          .cookie("usuario", "al415647@uji.es")
          .body(nueva)
          .when().post()
          .then()
            .statusCode(201)
            .body("nombre", equalTo("Costilla"))
            .body("unidad", equalTo("kg"));
    }

    @Test
    public void testDeleteCarne() {
        given()
          .when().delete("/" + carne.getId())
          .then()
            .statusCode(204);
    }

    @Test
    public void testValidarCarne() {
        given()
          .cookie("usuario", "al415647@uji.es")
          .queryParam("nombre", "Pechuga de Pollo")
          .queryParam("unidad", "Kg")
          .when().get("/validar")
          .then()
            .statusCode(200)
            .body("existe", is(true));
    }

    @Test
    public void testMisProductosSinSesion() {
        given()
          .when().get("/mis-productos")
          .then()
            .statusCode(401)
            .body(is("No hay sesión activa"));
    }

    @Test
    public void testMisProductosConSesion() {
        given()
          .cookie("usuario", "al415647@uji.es")
          .when().get("/mis-productos")
          .then()
            .statusCode(200)
            .body("[0].nombre", notNullValue());
    }
}
