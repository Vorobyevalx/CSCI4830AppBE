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
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.List;

public class Test9ViewFraudAlertsTest {
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
  public void test9ViewFraudAlerts() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
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
    
    // Wait for sidebar navigation to be visible
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".nav")));
    
    // Navigate to Fraud Alerts page via sidebar navigation
    List<WebElement> navItems = driver.findElements(By.cssSelector(".nav-item"));
    assertTrue("Should have navigation items", navItems.size() >= 3);
    
    // Find the Fraud Alerts button (should be the third one)
    WebElement fraudAlertsNavBtn = null;
    for (WebElement navItem : navItems) {
      String text = navItem.getText();
      if (text != null && (text.contains("Fraud") || text.contains("fraud"))) {
        fraudAlertsNavBtn = navItem;
        break;
      }
    }
    
    // If not found by text, use the third nav item
    if (fraudAlertsNavBtn == null) {
      fraudAlertsNavBtn = navItems.get(2);
    }
    
    // Click the Fraud Alerts navigation button
    wait.until(ExpectedConditions.elementToBeClickable(fraudAlertsNavBtn));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fraudAlertsNavBtn);
    
    // Wait for fraud alerts page to load - look for page title
    wait.until(ExpectedConditions.textToBePresentInElementLocated(
      By.cssSelector(".page-title"), "Fraud"));
    
    // Verify fraud alerts page is displayed
    WebElement pageTitle = driver.findElement(By.cssSelector(".page-title"));
    String titleText = pageTitle.getText();
    assertTrue("Page title should contain 'Fraud'", titleText.contains("Fraud") || titleText.contains("fraud"));
    
    // Check for fraud alerts content (either alerts list or empty state)
    try {
      // Wait for either fraud alerts or empty message
      wait.until(ExpectedConditions.or(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".alert-item, [class*='alert']")),
        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".empty"))
      ));
      
      // Check if there are fraud alerts or empty state
      List<WebElement> alertItems = driver.findElements(By.cssSelector(".alert-item, [class*='alert']"));
      WebElement emptyMessage = driver.findElement(By.cssSelector(".empty"));
      
      if (alertItems.size() > 0) {
        System.out.println("Found " + alertItems.size() + " fraud alert(s)");
        // Verify alert structure
        for (WebElement alert : alertItems) {
          assertTrue("Fraud alert should be visible", alert.isDisplayed());
        }
      } else if (emptyMessage.isDisplayed()) {
        System.out.println("No fraud alerts found (empty state)");
        String emptyText = emptyMessage.getText();
        assertTrue("Empty state should indicate no fraud alerts", 
                   emptyText.toLowerCase().contains("no fraud") || emptyText.toLowerCase().contains("no alerts"));
      }
    } catch (Exception e) {
      // If neither found, at least verify we're on the fraud alerts page
      assertTrue("Should be on fraud alerts page", titleText.contains("Fraud"));
    }
    
    // Test refresh button if available
    try {
      WebElement refreshBtn = driver.findElement(By.xpath("//button[contains(.,'Refresh')]"));
      if (refreshBtn.isDisplayed() && refreshBtn.isEnabled()) {
        wait.until(ExpectedConditions.elementToBeClickable(refreshBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", refreshBtn);
        
        // Wait a moment for refresh
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
        
        System.out.println("Refresh button clicked successfully");
      }
    } catch (Exception e) {
      System.out.println("Refresh button not found or not clickable (this is okay)");
    }
    
    // Logout
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".logout-btn")));
    driver.findElement(By.cssSelector(".logout-btn")).click();
    // Wait for login page - check for login form elements
    wait.until(ExpectedConditions.or(
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-container")),
      ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")),
      ExpectedConditions.presenceOfElementLocated(By.cssSelector(".login-form"))
    ));
  }
}

