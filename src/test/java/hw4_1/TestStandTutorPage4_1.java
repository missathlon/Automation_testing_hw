package hw4_1;

import org.junit.jupiter.api.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStandTutorPage4_1 {

    private static WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MainPage mainPage;

    private static final String login = "Student-21";
    private static final String password = "a1d1708595";
    private static final String baseURL = "https://test-stand.gb.ru/login";
    private static final String selenoidURL = "http://localhost:4444/wd/hub";


    @BeforeAll
    public static void openSelenoidContainer() throws MalformedURLException {
        ChromeOptions browser = new ChromeOptions();
        browser.setCapability("browserVersion", "128.0");
        Map<String, Object> map = new HashMap<>();
        map.put("enableVnc", true);
        map.put("enableLog", true);
        browser.setCapability("selenoid:options", map);
        driver = new RemoteWebDriver(new URL(selenoidURL), browser);
    }


    @BeforeEach
    public void setUp() {
        driver.get(baseURL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver, wait);
    }


    @Test
    public void groupAddingTest() {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
    }

    @Test
    void groupStatusActiveOrInactiveTest() throws IOException {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        mainPage.closeCreateGroupModalWindow();
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        getScreen();
    }


    @Test
    void authorizationWithoutEnteringLoginAndPasswordShouldReturnTest() throws IOException {
        loginPage.clickLoginButton();
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
        getScreen();
    }


    @Test
    void studentStatusActiveOrInactiveTest() throws IOException {
        successfulAuthorization();
        String groupName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupName);
        mainPage.closeCreateGroupModalWindow();
        int studentQuantity = 2;
        mainPage.clickOnCreatingNewLoginsStudentsByTitle(groupName);
        mainPage.enteringTheNumberOfNewLoginsStudents(studentQuantity);
        mainPage.clickSaveNumberNewLoginsStudents();
        mainPage.clickCloseNewLoginsStudentsForm();
        mainPage.waitForChangeNumberOfLoginsStudents(groupName, studentQuantity);
        mainPage.clickOnStudentsIdentitiesByTitle(groupName);
        int studentIndex = 0;
        String studentUsername = mainPage.getStudentUsernameByIndex(studentIndex);
        assertEquals("active", mainPage.getStatusOfStudentByUsername(studentUsername));
        mainPage.clickTrashIconOnStudentByUsername(studentUsername);
        assertEquals("block", mainPage.getStatusOfStudentByUsername(studentUsername));
        mainPage.clickRestoreFromTrashIconOnStudentByUsername(studentUsername);
        assertEquals("active", mainPage.getStatusOfStudentByUsername(studentUsername));
        getScreen();
    }


    private void successfulAuthorization() {
        loginPage.login(login, password);
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(login));
    }

    private void getScreen() throws IOException {
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of(
                "src/test/java/screenshots/hw4_1_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }


    @AfterAll
    public static void closeApp() {
        driver.quit();
    }
}