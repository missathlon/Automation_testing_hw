package hw2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStandTutor {

    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MainPage mainPage;

    private static final String login = "Student-21";
    private static final String password = "a1d1708595";
    private static final String baseURL = "https://test-stand.gb.ru/login";


    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.get(baseURL);
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
    void groupStatusActiveOrInactiveTest() {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        mainPage.closeCreateGroupModalWindow();
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
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
                "src/test/java/screenshots/hw2_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }


    @AfterEach
    public void closeApp() {
        driver.quit();
    }
}