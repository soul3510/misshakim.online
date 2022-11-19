package org;



import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TestBase {
    public static WebDriver driver;





    public void afterMethod() throws Exception {
    }

    public void afterSuite() throws IOException {
    }
}