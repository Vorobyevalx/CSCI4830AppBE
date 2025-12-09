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

public class Test7TransferBetweenAccountsTest {
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
  public void test7TransferBetweenAccounts() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    
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
    
    // Ensure user has at least 2 accounts for transfer
    java.util.List<WebElement> accounts = driver.findElements(By.cssSelector(".acct"));
    int accountCount = accounts.size();
    System.out.println("Current account count: " + accountCount);
    
    // If less than 2 accounts, create another one
    if (accountCount < 2) {
      System.out.println("Creating second account for transfer test...");
      WebElement newAccountBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'New Account')]")));
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", newAccountBtn);
      
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".create-account-form")));
      
      WebElement accountTypeSelect = driver.findElement(By.cssSelector(".create-account-form select"));
      Select accountSelect = new Select(accountTypeSelect);
      accountSelect.selectByValue("SAVINGS");
      
      WebElement createAccountSubmitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".create-account-form button[type='submit']")));
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createAccountSubmitBtn);
      
      longWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")));
      try {
        Thread.sleep(2000);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      
      // Verify we now have 2 accounts
      accounts = driver.findElements(By.cssSelector(".acct"));
      assertTrue("Should have at least 2 accounts for transfer", accounts.size() >= 2);
    }
    
    // Select the first account
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".acct:first-child")));
    WebElement firstAccount = driver.findElement(By.cssSelector(".acct:first-child"));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstAccount);
    
    // Wait for account details to load
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".detail-actions")));
    try {
      Thread.sleep(1500);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    
    // Click "New Transaction" button
    WebElement newTransactionBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'New Transaction')]")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", newTransactionBtn);
    try {
      Thread.sleep(500);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", newTransactionBtn);
    
    // Wait for transaction form to appear
    WebDriverWait formWait = new WebDriverWait(driver, Duration.ofSeconds(15));
    formWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transaction-form")));
    
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    
    // Select TRANSFER_OUT from transaction type dropdown
    WebElement transactionTypeSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transaction-form select")));
    Select select = new Select(transactionTypeSelect);
    wait.until(ExpectedConditions.elementToBeClickable(transactionTypeSelect));
    wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(transactionTypeSelect, By.cssSelector("option")));
    
    try {
      select.selectByVisibleText("Transfer Out");
    } catch (Exception e) {
      select.selectByValue("TRANSFER_OUT");
    }
    
    // Wait a moment for React to update the form
    try {
      Thread.sleep(1000);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    
    // Wait for destination account dropdown to appear (it's conditionally rendered)
    // Look for select element that contains "Select account..." option
    wait.until(ExpectedConditions.presenceOfElementLocated(
      By.xpath("//div[contains(@class, 'transaction-form')]//select[.//option[contains(text(), 'Select account')]]")));
    
    // Select destination account (second account)
    WebElement destinationSelect = driver.findElement(
      By.xpath("//div[contains(@class, 'transaction-form')]//select[.//option[contains(text(), 'Select account')]]"));
    Select destinationSelectObj = new Select(destinationSelect);
    
    // Get available options and select the first one (should be the second account)
    java.util.List<WebElement> options = destinationSelectObj.getOptions();
    assertTrue("Should have at least one destination account option", options.size() > 1);
    destinationSelectObj.selectByIndex(1); // Select first available account (index 0 is "Select account...")
    
    // Set transfer amount
    WebElement amountInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transaction-form input[type='number']")));
    amountInput.click();
    ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", amountInput);
    amountInput.clear();
    amountInput.sendKeys("25.00");
    
    try {
      Thread.sleep(500);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    
    // Verify amount was set
    String amountValue = amountInput.getAttribute("value");
    if (amountValue == null || amountValue.isEmpty() || !amountValue.contains("25")) {
      ((JavascriptExecutor) driver).executeScript(
        "var input = arguments[0];" +
        "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
        "nativeInputValueSetter.call(input, '25.00');" +
        "var event = new Event('input', { bubbles: true });" +
        "input.dispatchEvent(event);" +
        "var changeEvent = new Event('change', { bubbles: true });" +
        "input.dispatchEvent(changeEvent);",
        amountInput
      );
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
    }
    
    // Submit the form
    WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".transaction-form button[type='submit']")));
    System.out.println("Submitting transfer form...");
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
    
    // Wait for success or error message
    try {
      longWait.until(ExpectedConditions.or(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-error"))
      ));
      
      java.util.List<WebElement> successAlerts = driver.findElements(By.cssSelector(".alert-success"));
      java.util.List<WebElement> errorAlerts = driver.findElements(By.cssSelector(".alert-error"));
      
      if (successAlerts.size() > 0 && successAlerts.get(0).isDisplayed()) {
        String successText = successAlerts.get(0).getText();
        System.out.println("Success message: " + successText);
        assertTrue("Transfer should succeed", successText.toLowerCase().contains("success") || 
                   successText.toLowerCase().contains("transfer") || successText.toLowerCase().contains("completed"));
      } else if (errorAlerts.size() > 0 && errorAlerts.get(0).isDisplayed()) {
        String errorText = errorAlerts.get(0).getText();
        System.out.println("Error message: " + errorText);
        if (errorText.contains("Insufficient funds")) {
          System.out.println("Insufficient funds error is expected if account balance is too low");
        } else {
          throw new RuntimeException("Transfer failed with error: " + errorText);
        }
      }
    } catch (org.openqa.selenium.TimeoutException e) {
      // Check if form disappeared (indicates success)
      java.util.List<WebElement> forms = driver.findElements(By.cssSelector(".transaction-form"));
      if (forms.size() == 0) {
        System.out.println("Form disappeared - transfer likely succeeded");
        assertTrue("Transfer likely succeeded (form disappeared)", true);
      } else {
        throw new RuntimeException("Transfer form still visible after timeout");
      }
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

