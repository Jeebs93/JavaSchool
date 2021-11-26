package com.example.rehab.config;

import lombok.Data;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

@Data
public class SeleniumConfig {

   private WebDriver driver;

    public SeleniumConfig() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.setBinary("/Program Files (x86)/Google/Chrome/Application");
        driver = new RemoteWebDriver(new
                java.net.URL("http://localhost:9515"), new ChromeOptions());
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    static {
        System.setProperty("webdriver.gecko.driver", findFile("geckodriver"));
    }

    private static String findFile(String filename) {
        String paths[] = {"", "bin/", "target/classes"};
        for (String path : paths) {
            if (new File(path + filename).exists())
                return path + filename;
        }
        return "";
    }

}
