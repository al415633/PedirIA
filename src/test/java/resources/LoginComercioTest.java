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

public class LoginComercioTest {
    private static WebDriver webDriver;
    private static final String localhost = "http://localhost:8080/comercioLogin.html"; // URL de login

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

    @Given("I am in the comercio login page")
    public void i_am_in_the_comercio_login_page() {
        webDriver.navigate().to(localhost);
    }

    @When("I enter {string} as email")
    public void i_enter_as_email(String email) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"email\"]")).sendKeys(email);
        Thread.sleep(1000);
    }

    @When("I enter {string} as password")
    public void i_enter_as_password(String password) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(password);
        Thread.sleep(1000);
    }

    @When("I click the Login button")
    public void i_click_the_login_button() throws InterruptedException {
        webDriver.findElement(By.xpath("//button[contains(text(), 'Iniciar Sesión')]")).click();
        Thread.sleep(2000);
    }

    @Then("The commerce is logged in successfully and redirected")
    public void the_commerce_is_logged_in_successfully_and_redirected() {
        // Verificar que la redirección se ha realizado correctamente
        assertEquals("http://localhost:8080/dashboard.html", webDriver.getCurrentUrl());
    }

    @Then("The system shows an invalid credentials error")
    public void the_system_shows_an_invalid_credentials_error() {
        // Verificar que se ha mostrado un mensaje de error
        assertTrue(webDriver.findElement(By.id("errorMessage")).isDisplayed());
    }
}
