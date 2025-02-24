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

public class AddContactTest {
    private static WebDriver webDriver;
    private static final String localhost = "http://localhost:8080";

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

    @Given("I am in the contacts list page")
    public void i_am_in_the_contacts_list_page() {
        webDriver.navigate().to(localhost);
    }
    @When("II provide {string} for the name")
    public void ii_provide_for_the_name(String name) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"name\"]")).sendKeys(name);
        Thread.sleep(1000);
    }
    @When("I provide {string} for the surname")
    public void i_provide_for_the_surname(String surname) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"surname\"]")).sendKeys(surname);
        Thread.sleep(1000);
    }
    @When("I provide {string} for the nif")
    public void i_provide_for_the_nif(String nif) throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"nif\"]")).sendKeys(nif);
        Thread.sleep(1000);
    }
    @When("I click the New button")
    public void i_click_the_new_button() throws InterruptedException {
        webDriver.findElement(By.xpath("//*[@id=\"new\"]")).click();
        Thread.sleep(1000);
    }
    @Then("The person with nif {string} is created in the agenda")
    public void the_person_with_nif_is_created_in_the_agenda(String nif) {
        assertNotNull(webDriver.findElement(By.id(nif)));
    }
}
