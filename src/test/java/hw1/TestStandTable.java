package hw1;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;


public class TestStandTable {

    private final static String login = "Student-21";
    private final static String password = "a1d1708595";
    private final static String baseURL = "https://test-stand.gb.ru/login";


    static WebDriver driver;
    static WebDriverWait wait;

    @BeforeAll
    static void initElements() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.get(baseURL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    @Test
    void groupAddingTest() throws  IOException {
        authorization();
        String groupName = "Hihihhi" + System.currentTimeMillis();
        WebElement createGroup = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@id='create-btn']")));
        createGroup.click();
        By fieldGroupName = By.xpath("//*[@type='text']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(fieldGroupName)).sendKeys(groupName);
        WebElement buttonSaveGroup = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form div.submit button")));
        buttonSaveGroup.click();

        String tableTitleXpath = "//td[contains(text(), '%s')]";
        WebElement expectedTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(String.format(tableTitleXpath, groupName))));
        Assertions.assertTrue(expectedTitle.isDisplayed());
        getScreen();
    }


    private void authorization() {
        WebElement loginWeb = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@type='text']")));
        WebElement passwordWeb = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[@type='password']")));
        WebElement buttonLogin = driver.findElement(By.xpath("//*[@class='mdc-button__label']"));
        loginWeb.sendKeys(login);
        passwordWeb.sendKeys(password);
        buttonLogin.click();
        wait.until(ExpectedConditions.invisibilityOf(buttonLogin));
        List<WebElement> searchElement = driver.findElements(By.partialLinkText("Hello"));
        Assertions.assertEquals(1, searchElement.size());
    }

    private void getScreen() throws IOException {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of(
                "src/test/java/screenshots/hw1_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }


    @AfterAll
    static void closeApp() {
        driver.quit();
    }
}