package selenium;

import com.example.rehab.config.SeleniumConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;

public class SeleniumObject {

    public SeleniumConfig getConfig() {
        return config;
    }

    private SeleniumConfig config;
    private String url = "http:localhost:8080";

    public SeleniumObject() throws MalformedURLException {
        config = new SeleniumConfig();
        config.getDriver().get(url);
    }

    public void closeWindow() {
        this.config.getDriver().close();
    }

    public String getTitle() {
        return this.config.getDriver().getTitle();
    }

    public void getAboutPage() {
        WebElement element = this.config.getDriver()
                .findElement(By.partialLinkText("About"));
        element.click();
    }

    public void getWorkingWithPatientsPage() {
        WebElement element = this.config.getDriver()
                .findElement(By.partialLinkText("Working with patients"));
        element.click();

    }

    public void getBackFromErrorPage() {
        WebElement element = this.config.getDriver()
                .findElement(By.name("back"));
        element.click();
    }

    public void getEventsPage() {
        WebElement element = this.config.getDriver()
                .findElement(By.partialLinkText("Events"));
        element.click();
    }

    public void logOut() {
        WebElement element = this.config.getDriver()
                .findElement(By.name("logout"));
        element.click();
    }

}
