package hw4_2;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic(value = "Testing Tutor page on the GB test bench https://test-stand.gb.ru/login")
@Feature(value = "Homework4 - Running tests via Docker Selenoid")

public class TestStandTutorPage4_2 {

    private WebDriver driver;
    private hw4_2.LoginPage loginPage;
    private hw4_2.MainPage mainPage;

    private static final String login = "Student-21";
    private static final String fullName = "21 Student";
    private static final String password = "a1d1708595";
    private static final String birthDateInput = "01.01.2012";
    private static final String birthDateDisplay = "02.02.2012";
    private static final String baseURL = "https://test-stand.gb.ru/login";


    @BeforeAll
    public static void openSelenoidContainer() {
        Configuration.browser = "chrome";
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.screenshots = true;
        Map<String, Object> map = new HashMap<>();
        map.put("enableVnc", true);
        map.put("enableLog", true);
        Configuration.browserCapabilities.setCapability("selenoid:options", map);
    }

    @BeforeEach
    public void setUp() {
        Selenide.open(baseURL);
        driver = WebDriverRunner.getWebDriver();
    }

    @Test
    @Description("Create a group")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing the creation of a group and making sure the name of the created group is displayed")
    public void groupAddingTest()  {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        hw4_2.MainPage mainPage = Selenide.page(hw4_2.MainPage.class);
        mainPage.createGroup(groupTestName);
    }

    @Test
    @Description("Changing group status")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing group status changes and checking group status display")
    void groupStatusActiveOrInactiveTest() {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.createGroup(groupTestName);
        mainPage.closeCreateGroupModalWindow();
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
    }


    @Test
    @Description("Authorization at the stand")
    @Severity(SeverityLevel.CRITICAL)
    @Story(value = "Authorization at the stand without entering a login and password. "
            + "Checking the display of error message and 401 code")
    void authorizationWithoutEnteringLoginAndPasswordShouldReturnTest() {
        LoginPage loginPage = Selenide.page(LoginPage.class);
        loginPage.clickLoginButton();
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
        getScreen();
    }


    @Test
    @Description("Changing student status")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing student status changes and checking student status display")
    void studentStatusActiveOrInactiveTest() {
        successfulAuthorization();
        String groupName = "New Test Group " + System.currentTimeMillis();
        MainPage mainPage = Selenide.page(MainPage.class);
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
    }


    @Test
    @Description("Full username in profile")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing the display of the user's full name in the profile")
    void valueFullNameInProfilePageTest() {
        successfulAuthorization();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.clickProfileButton();
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        assertEquals(fullName, profilePage.getFullNameInAdditionalInfo());
        assertEquals(fullName, profilePage.getFullNameUnderAvatar());
        getScreen();
    }

    @Test
    @Description("Uploading a new avatar")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing the upload of a new avatar and checking the display of the file in the upload field")
    void uploadNewAvatarTest() {
        successfulAuthorization();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.clickProfileButton();
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        profilePage.editProfileButton();
        assertEquals("", profilePage.getAvatarValue());
        profilePage.uploadNewAvatar(new File("src/test/java/resources/selenideAvatar.jpg"));
        assertEquals("selenideAvatar.jpg", profilePage.getAvatarValue());
    }

    @Test
    @Description("Changing a user's date of birth in their profile")
    @Severity(SeverityLevel.NORMAL)
    @Story(value = "Testing changing the user's date of birth in the profile,"
            + " and checking the display of the date of birth in the profile")
    void setDateOfBirthTest() {
        successfulAuthorization();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.clickProfileButton();
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        profilePage.editProfileButton();
        profilePage.setBirthDate(birthDateInput);
        getScreen();
        profilePage.saveFormEditProfileButton();
        profilePage.closeFormEditProfileButton();
        profilePage.getBirthDateInfo(birthDateDisplay);
    }

    private void successfulAuthorization() {
        LoginPage loginPage = Selenide.page(LoginPage.class);
        loginPage.login(login, password);
        MainPage mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(login));
    }

    private void getScreen() {
        Selenide.screenshot("hw4_2_" + System.currentTimeMillis());
    }


    @AfterEach
    public void closeApp() {
        WebDriverRunner.closeWebDriver();
    }
}