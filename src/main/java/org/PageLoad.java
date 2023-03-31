package org;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PageLoad {

    //    @Step("Waiting for page to load")
    public static void waitForPageLoaded(RemoteWebDriver driver) {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));//We use a constructor here for wait since we need to pass some arguments to it.
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

    public static String getCurrentPageUrl(RemoteWebDriver driver) throws Exception {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl;
    }

    public static WebElement waitForElementWithActions(String description, RemoteWebDriver driver, String elementValue_string, String type, WebElement elementValue, int timeToWaitInSeconds) throws Exception {
        Thread.sleep(100);
        int time1 = LocalDateTime.now().getSecond();
        WebElement element = null;
        for (int counter = 0; counter <= (10); counter++) {
            try {
                if (elementValue == null){
                    Actions actions = new Actions(driver);
                    switch (type) {
                        case "xpath":
                            element = driver.findElement(By.xpath(elementValue_string));
                            break;
                        case "cssSelector":
                            element = driver.findElement(By.cssSelector(elementValue_string));
                            break;
                        case "id":
                            element = driver.findElement(By.id(elementValue_string));
                            break;
                        case "linkText":
                            element = driver.findElement(By.linkText(elementValue_string));
                            break;
                        case "className":
                            element = driver.findElement(By.className(elementValue_string));
                            break;
                        case "tagName":
                            element = driver.findElement(By.tagName(elementValue_string));
                            break;
                    }
                    actions.moveToElement(element).perform();
                    element.getLocation();
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2-time1);
//                    System.out.println(element + " found after " + diff + "s.");
                    return element;
                }
                else {
                    Actions actions = new Actions(driver);
                    actions.moveToElement(elementValue).perform();
                    elementValue.getLocation();
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2 - time1);
//                    System.out.println(elementValue + " found after " + diff + "s.");
                    return elementValue;
                }
            } catch (Exception e) {
                if (counter == timeToWaitInSeconds) {
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2-time1);
                    if (elementValue == null) {
                        Assert.assertTrue(false, description + " not found after " + diff+"s. \n" + e);
                    }else{
                        Assert.assertTrue(false, description + " not found after " + diff + "s. \n" + e);
                    }
                }
                Thread.sleep(1000);
            }
        }
        return element;
    }


    public static List<WebElement> waitForElementWithActions_list(String description,RemoteWebDriver driver, String elementValue_string, String type, List<WebElement> elementValue_list, int timeToWaitInSeconds) throws Exception {
        Thread.sleep(100);
        int time1 = LocalDateTime.now().getSecond();
        List<WebElement> element = null;
        for (int counter = 0; counter <= (10); counter++) {
            try {
                if (elementValue_list == null){
                    Actions actions = new Actions(driver);
                    switch (type) {
                        case "xpath":
                            element = driver.findElements(By.xpath(elementValue_string));
                            break;
                        case "cssSelector":
                            element = driver.findElements(By.cssSelector(elementValue_string));
                            break;
                        case "id":
                            element = driver.findElements(By.id(elementValue_string));
                            break;
                        case "linkText":
                            element = driver.findElements(By.linkText(elementValue_string));
                            break;
                        case "className":
                            element = driver.findElements(By.className(elementValue_string));
                            break;
                    }
                    actions.moveToElement(element.get(0)).perform();
                    element.get(0).getLocation();
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2-time1);
//                    System.out.println(element.get(0) + " found after " + diff + "s.");
                    return element;
                }
                else {
                    Actions actions = new Actions(driver);
                    actions.moveToElement(elementValue_list.get(0)).perform();
                    elementValue_list.get(0).getLocation();
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2 - time1);
//                    System.out.println(elementValue_list.get(0) + " found after " + diff + "s.");
                    return elementValue_list;
                }
            } catch (Exception e) {
                if (counter == timeToWaitInSeconds) {
                    int time2 = LocalDateTime.now().getSecond();
                    int diff = Math.abs(time2-time1);
                    if (elementValue_list.get(0) == null) {
                        Assert.assertTrue(false, description + " not found after " + diff + "s. \n" + e);
                    }else{
                        Assert.assertTrue(false, description + " not found after " + diff + "s. \n" + e);
                    }
                }
                Thread.sleep(1000);
            }
        }
        return element;
    }
}