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

public class Test2ViewAccountDetailsTest {
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
  public void test2ViewAccountDetails() {
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
    
    // Wait for accounts to load
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".acct")));
    List<WebElement> accounts = driver.findElements(By.cssSelector(".acct"));
    assertTrue("Should have at least one account", accounts.size() > 0);
    
    // Click on first account
    WebElement firstAccount = accounts.get(0);
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstAccount);
    
    // Wait for account details to appear
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".detail-title")));
    WebElement detailTitle = driver.findElement(By.cssSelector(".detail-title"));
    assertTrue("Account detail title should be visible", detailTitle.isDisplayed());
    
    // Verify account details are shown
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".detail-balance")));
    WebElement balance = driver.findElement(By.cssSelector(".detail-balance"));
    assertTrue("Account balance should be visible", balance.isDisplayed());
    
    // Click on other accounts if available
    accounts = driver.findElements(By.cssSelector(".acct"));
    for (int i = 1; i < Math.min(accounts.size(), 4); i++) {
      WebElement account = accounts.get(i);
      ((JavascriptExecutor) driver).executeScript("arguments[0].click();", account);
      
      // Wait for details to update
      try {
        Thread.sleep(500);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
      }
      
      // Verify details are still visible
      assertTrue("Account details should be visible after selecting account " + (i + 1), 
                 driver.findElement(By.cssSelector(".detail-title")).isDisplayed());
    }
    
    // Logout
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".logout-btn")));
    driver.findElement(By.cssSelector(".logout-btn")).click();
    
    // Verify we're back at login page
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-container")));
    assertTrue("Should be back at login page after logout", 
               driver.findElement(By.cssSelector("input[type='text']")).isDisplayed());
  }
}
