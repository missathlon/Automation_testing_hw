package hw3;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStandTutorPage {


    private WebDriver driver;
    private LoginPage loginPage;
    private MainPage mainPage;

    private static final String login = "Student-21";
    private static final String password = "a1d1708595";
    private static final String fullName = "21 Student";
    private static final String baseURL = "https://test-stand.gb.ru/login";


    @BeforeEach
    public void setUp() {
        Selenide.open(baseURL);
        driver = WebDriverRunner.getWebDriver();
    }

    @Test
    public void groupAddingTest()  {
        successfulAuthorization();
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.createGroup(groupTestName);
    }

    @Test
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
    void authorizationWithoutEnteringLoginAndPasswordShouldReturnTest()  {
        LoginPage loginPage = Selenide.page(LoginPage.class);
        loginPage.clickLoginButton();
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
        getScreen();
    }

    @Test
    void studentStatusActiveOrInactiveTest()  {
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
        getScreen();
    }

    @Test
    void valueFullNameInProfilePageTest() {
        successfulAuthorization();
        MainPage mainPage = Selenide.page(MainPage.class);
        mainPage.clickProfileButton();
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        assertEquals(fullName, profilePage.getFullNameInAdditionalInfo());
        assertEquals(fullName, profilePage.getFullNameUnderAvatar());
    }


    private void successfulAuthorization() {
        LoginPage loginPage = Selenide.page(LoginPage.class);
        loginPage.login(login, password);
        MainPage mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(login));
    }

    private void getScreen()  {
        Selenide.screenshot("hw3_" + System.currentTimeMillis());
    }


    @AfterEach
    public void closeApp() {
        WebDriverRunner.closeWebDriver();
    }
}