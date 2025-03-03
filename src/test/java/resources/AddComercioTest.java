package resources;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AddComercioTest {
    private static WebDriver webDriver;
    private static final String localhost = "http://localhost:8080/comercioRegistro.html"; // URL de registro

    @BeforeAll
    public static void setUp() {
        ChromeDriverService service = new ChromeDriverService.Builder().build();
        webDriver = new ChromeDriver(service);
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    @AfterAll
    public static void tearDown() {
        webDriver.quit();
    }

    @Given("I am in the comercio registration page")
    public void i_am_in_the_comercio_registration_page() {
        webDriver.navigate().to(localhost);
    }

    @When("I provide {string} for the email")
    public void i_provide_for_the_email(String email) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"email\"]")).sendKeys(email);
        Thread.sleep(1000);
    }

    @When("I provide {string} for the password")
    public void i_provide_for_the_password(String password) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(password);
        Thread.sleep(1000);
    }

    @When("I provide {string} for the commerce name")
    public void i_provide_for_the_commerce_name(String nombreComercio) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"nombreComercio\"]")).sendKeys(nombreComercio);
        Thread.sleep(1000);
    }

    @When("I select {string} as commerce type")
    public void i_select_as_commerce_type(String tipoComercio) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"tipoComercio\"]")).sendKeys(tipoComercio);
        Thread.sleep(1000);
    }

    @When("I select {string} as stock purchase day")
    public void i_select_as_stock_purchase_day(String dia) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"dia\"]")).sendKeys(dia);
        Thread.sleep(1000);
    }

    @When("I click the Register button")
    public void i_click_the_register_button() throws InterruptedException {
        webDriver.findElement(By.xpath("//button[contains(text(), 'Registrarse')]")).click();
        Thread.sleep(2000);
    }

    @Then("The commerce with email {string} is successfully registered")
    public void the_commerce_with_email_is_successfully_registered(String email) {
        // Verificar que la redirección se ha realizado correctamente
        assertEquals("http://localhost:8080/registroCorrecto.html", webDriver.getCurrentUrl());
    }

    @Then("The system shows an error message")
    public void the_system_shows_an_error_message() {
        // Verificar que se ha mostrado una alerta o redirigido a error
        assertEquals("http://localhost:8080/registroError.html", webDriver.getCurrentUrl());
    }
}
