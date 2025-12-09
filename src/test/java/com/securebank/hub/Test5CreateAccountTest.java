package com.securebank.hub;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class Test5CreateAccountTest {
  private WebDriver driver;
  JavascriptExecutor js;
  
  @Before
  public void setUp() {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1080");
    driver = new ChromeDriver(options);
    js = (JavascriptExecutor) driver;
  }
  
  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
  
  @Test
  public void test5CreateAccount() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    
    // Navigate to application
    driver.get("http://34.42.31.237:8080/");
    driver.manage().window().setSize(new Dimension(968, 877));
    
    // Wait for login page and login
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")));
    WebElement usernameInput = driver.findElement(By.cssSelector("input[type='text']"));
    usernameInput.click();
    usernameInput.clear();
    usernameInput.sendKeys("testuser");
    
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
    WebElement passwordInput = driver.findElement(By.cssSelector("input[type='password']"));
    passwordInput.click();
    passwordInput.clear();
    passwordInput.sendKeys("password123");
    
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
    driver.findElement(By.cssSelector("button[type='submit']")).click();
    
    // Wait for dashboard to load
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));
    
    // Get initial account count
    int initialAccountCount = driver.findElements(By.cssSelector(".acct")).size();
    System.out.println("Initial account count: " + initialAccountCount);
    
    // Click "New Account" button
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'New Account')]")));
    WebElement newAccountBtn = driver.findElement(By.xpath("//button[contains(.,'New Account')]"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", newAccountBtn);
    
    // Wait for account creation form to appear (it uses .transaction-form class)
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'transaction-form')]//h4[contains(.,'Create New Account')]")));
    
    // Select account type (SAVINGS)
    WebElement accountTypeSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
      By.xpath("//div[contains(@class, 'transaction-form')]//select")));
    Select select = new Select(accountTypeSelect);
    select.selectByValue("SAVINGS");
    
    // Leave account number blank to auto-generate
    // (form will auto-generate it)
    
    // Submit the form
    WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
      By.xpath("//div[contains(@class, 'transaction-form')]//button[@type='submit']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
    
    // Wait for success message or account to appear in list
    try {
      // Wait for either success message or new account in list
      longWait.until(ExpectedConditions.or(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
        ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".acct"), initialAccountCount)
      ));
      
      // Check for success message
      java.util.List<WebElement> successAlerts = driver.findElements(By.cssSelector(".alert-success"));
      if (successAlerts.size() > 0 && successAlerts.get(0).isDisplayed()) {
        String successText = successAlerts.get(0).getText();
        System.out.println("Success message: " + successText);
        assertTrue("Account creation should succeed", successText.toLowerCase().contains("success") || 
                   successText.toLowerCase().contains("created"));
      }
      
      // Wait for account list to refresh (form closes and list updates)
      try {
        Thread.sleep(2000); // Give time for React to update
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      
      // Wait for account count to increase (with retry)
      int newAccountCount = initialAccountCount;
      for (int i = 0; i < 5; i++) {
        newAccountCount = driver.findElements(By.cssSelector(".acct")).size();
        if (newAccountCount > initialAccountCount) {
          break;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      }
      System.out.println("New account count: " + newAccountCount);
      assertTrue("Account count should increase (initial: " + initialAccountCount + ", new: " + newAccountCount + ")", 
                 newAccountCount > initialAccountCount);
      
    } catch (Exception e) {
      // Check for error message
      java.util.List<WebElement> errorAlerts = driver.findElements(By.cssSelector(".alert-error"));
      if (errorAlerts.size() > 0 && errorAlerts.get(0).isDisplayed()) {
        String errorText = errorAlerts.get(0).getText();
        System.out.println("Error message: " + errorText);
        throw new RuntimeException("Account creation failed: " + errorText);
      }
      throw e;
    }
    
    // Logout (optional - skip if logout doesn't work reliably in headless mode)
    try {
      wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".logout-btn")));
      driver.findElement(By.cssSelector(".logout-btn")).click();
      // Wait for login page - React state change, so wait a bit longer
      try {
        Thread.sleep(2000);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      // Try to find login elements, but don't fail if not found (logout is not critical to test)
      try {
        wait.until(ExpectedConditions.or(
          ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-container")),
          ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']"))
        ));
      } catch (Exception e) {
        System.out.println("Logout verification skipped (not critical)");
      }
    } catch (Exception e) {
      System.out.println("Logout skipped (not critical to test functionality)");
    }
  }
}

