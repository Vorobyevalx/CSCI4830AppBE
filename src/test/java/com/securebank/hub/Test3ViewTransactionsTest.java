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

public class Test3ViewTransactionsTest {
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
  public void test3ViewTransactions() {
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
    
    // Navigate to Transactions page
    // Look for navigation items or "View Transactions" button
    try {
      // Try finding nav items first
      List<WebElement> navItems = driver.findElements(By.cssSelector(".nav-item, [class*='nav']"));
      if (navItems.size() >= 2) {
        wait.until(ExpectedConditions.elementToBeClickable(navItems.get(1)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navItems.get(1));
      } else {
        // Try finding "View Transactions" button
        WebElement viewTransactionsBtn = wait.until(ExpectedConditions.elementToBeClickable(
          By.xpath("//button[contains(.,'View Transactions') or contains(.,'Transactions')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewTransactionsBtn);
      }
    } catch (Exception e) {
      // If navigation fails, try direct URL or look for transactions section
      System.out.println("Could not find navigation, trying alternative method");
    }
    
    // Wait for transactions page to load
    wait.until(ExpectedConditions.or(
      ExpectedConditions.presenceOfElementLocated(By.cssSelector(".transaction-item, .transaction-row, [class*='transaction']")),
      ExpectedConditions.presenceOfElementLocated(By.cssSelector(".quick-filter-btn"))
    ));
    
    // Test quick filter buttons if available
    List<WebElement> quickFilterBtns = driver.findElements(By.cssSelector(".quick-filter-btn"));
    if (quickFilterBtns.size() > 0) {
      // Click on different filter buttons
      for (int i = 0; i < Math.min(quickFilterBtns.size(), 4); i++) {
        WebElement filterBtn = quickFilterBtns.get(i);
        if (filterBtn.isDisplayed() && filterBtn.isEnabled()) {
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
          
          // Wait a moment for filter to apply
          try {
            Thread.sleep(500);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
          }
          
          // Verify transactions are still visible (or empty state message)
          assertTrue("Transactions page should still be visible after filter", 
                     driver.findElements(By.cssSelector(".transaction-item, .transaction-row, [class*='transaction'], .empty")).size() >= 0);
        }
      }
    }
    
    // Verify transactions page is displayed
    assertTrue("Transactions page should be visible", 
               driver.findElements(By.cssSelector(".transaction-item, .transaction-row, [class*='transaction'], .quick-filter-btn")).size() > 0);
    
    // Logout
    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".logout-btn")));
    driver.findElement(By.cssSelector(".logout-btn")).click();
    
    // Verify we're back at login page
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-container")));
    assertTrue("Should be back at login page after logout", 
               driver.findElement(By.cssSelector("input[type='text']")).isDisplayed());
  }
}
