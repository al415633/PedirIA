package resources;

import data.Contact;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

@QuarkusTest
public class ContactResourceTest {
    @Test
    public void primerTest() {
        Contact obtenido = given()
                .when().get("contacts/retrieve/12")
                .body()
                .as(Contact.class);

        assertThat(obtenido, is(new Contact("uno", "dos", "12", null)));
    }
}
