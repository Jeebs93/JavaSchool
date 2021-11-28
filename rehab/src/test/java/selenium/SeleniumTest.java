package selenium;


import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {
    private static SeleniumObject seleniumObject;

    @BeforeAll
    public static void setUp() throws MalformedURLException {
        seleniumObject = new SeleniumObject();
    }

    @AfterAll
    public static void tearDown() {
        seleniumObject.closeWindow();
    }


    @Test
    void testAboutPage() {
        seleniumObject.getAboutPage();
        String actualTitle = seleniumObject.getTitle();
        assertEquals("About",actualTitle);
    }

    @Test
    void testWorkingWithPatientsPageWithDoctor() {
        seleniumObject.getWorkingWithPatientsPage();
        WebElement username = seleniumObject.getConfig().getDriver().findElement(By.name("username"));
        WebElement password = seleniumObject.getConfig().getDriver().findElement(By.name("password"));
        username.sendKeys("doctor");
        password.sendKeys("doctor123");
        seleniumObject.getConfig().getDriver().findElement(By.name("login")).click();
        assertEquals("Patients", seleniumObject.getTitle());
        seleniumObject.getConfig().getDriver().findElement(By.partialLinkText("Add")).click();
        assertEquals("Add patient", seleniumObject.getTitle());
        seleniumObject.getConfig().getDriver().findElement(By.name("add-patient")).click();
        assertTrue(seleniumObject.getConfig().getDriver()
                .getPageSource()
                .contains("Please, enter name of patient"));
        assertTrue(seleniumObject.getConfig().getDriver()
                .getPageSource()
                .contains("Please, enter insurance number"));
        assertTrue(seleniumObject.getConfig().getDriver()
                .getPageSource()
                .contains("Please, enter diagnosis"));
        seleniumObject.logOut();
    }

    @Test
    void testWorkingWithPatientsPageWithNurse() {
        seleniumObject.getWorkingWithPatientsPage();
        WebElement username = seleniumObject.getConfig().getDriver().findElement(By.name("username"));
        WebElement password = seleniumObject.getConfig().getDriver().findElement(By.name("password"));
        username.sendKeys("nurse");
        password.sendKeys("nurse123");
        seleniumObject.getConfig().getDriver().findElement(By.name("login")).click();
        assertEquals("Patients", seleniumObject.getTitle());
        seleniumObject.getConfig().getDriver().findElement(By.partialLinkText("Add")).click();
        assertEquals("Access denied", seleniumObject.getTitle());
        seleniumObject.getBackFromErrorPage();
        seleniumObject.getConfig().getDriver().findElement(By.partialLinkText("Discharged patients")).click();
        seleniumObject.getConfig().getDriver().findElement(By.partialLinkText("Return to treatment")).click();
        seleniumObject.getConfig().getDriver().switchTo().alert().accept();
        assertEquals("Access denied", seleniumObject.getTitle());
        seleniumObject.getBackFromErrorPage();
        seleniumObject.logOut();

    }

    @Test
    void testMyPatientsAndPatientDetailsWithDoctor() {
        seleniumObject.getWorkingWithPatientsPage();
        WebElement username = seleniumObject.getConfig().getDriver().findElement(By.name("username"));
        WebElement password = seleniumObject.getConfig().getDriver().findElement(By.name("password"));
        username.sendKeys("doctor");
        password.sendKeys("doctor123");
        seleniumObject.getConfig().getDriver().findElement(By.name("login")).click();
        seleniumObject.getConfig().getDriver().findElement(By.name("my-patients")).click();
        seleniumObject.getConfig().getDriver().findElement(By.name("details")).click();
        assertTrue(seleniumObject.getConfig().getDriver()
                .getPageSource()
                .contains("Appointments"));
        assertNotNull(seleniumObject.getConfig().getDriver().findElement(By.name("add-procedure")));
        seleniumObject.logOut();
    }

    @Test
    void testEventsWithDoctor() {
        seleniumObject.getEventsPage();
        WebElement username = seleniumObject.getConfig().getDriver().findElement(By.name("username"));
        WebElement password = seleniumObject.getConfig().getDriver().findElement(By.name("password"));
        username.sendKeys("doctor");
        password.sendKeys("doctor123");
        seleniumObject.getConfig().getDriver().findElement(By.name("login")).click();
        assertNotNull(seleniumObject.getConfig().getDriver().findElement(By.name("patient-details")));
        seleniumObject.logOut();
    }

    @Test
    void testEventsWithNurse() {
        seleniumObject.getEventsPage();
        WebElement username = seleniumObject.getConfig().getDriver().findElement(By.name("username"));
        WebElement password = seleniumObject.getConfig().getDriver().findElement(By.name("password"));
        username.sendKeys("nurse");
        password.sendKeys("nurse123");
        seleniumObject.getConfig().getDriver().findElement(By.name("login")).click();
        assertNotNull(seleniumObject.getConfig().getDriver().findElement(By.name("complete")));
        assertNotNull(seleniumObject.getConfig().getDriver().findElement(By.name("cancel")));
        seleniumObject.logOut();
    }
}
