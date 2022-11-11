package pochtabank;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class FeedbackTest {

    private static final String URL = "https://www.pochtabank.ru/feedback";
    WebDriver chrome;

    WebElement elFio, elMail, elPhone, elAgreement, elComment;
    WebElement submitBtn;
    WebDriverWait wait;

    int testIndex;

    @DataProvider(name = "sms")
    public static Object[][] dbData() {
        return new Object[][]{
                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", "Хьюстон, у вас проблемы", true},
                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, true},
                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, false},
                {null, "9775676767", "testcase@mail.ru", null, true},
                {"Клементьев Вадим Владимирович", null, "testcase@mail.ru", null, true},
                {"Клементьев Вадим Владимирович", "9775676767", null, null, true},
//                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, true},
//                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, true},
        };
    }

    @BeforeTest
    private void setupTest() {
        System.setProperty("webdriver.chrome.driver", "E:\\Projects\\Autotest\\ChromeDriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        //options.addArguments("start-maximized");
        chrome = new ChromeDriver(options);
        chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(chrome, Duration.ofSeconds(10));
        testIndex = 0;
    }

    @Parameters({"sms"})
    @Test(dataProvider = "sms")
    public void testNoSms(String fio, String phone, String mail, String comment, Boolean checkAgreement) {
        testIndex++;
        System.out.println("Test " + testIndex);

//        try {
//            Alert alert = wait.until(alertIsPresent());
//            alert.dismiss();
//        }catch (TimeoutException tex){}

        Actions act = new Actions(chrome);
        if (fio != null && fio.length() > 0)
            elFio.sendKeys(fio);
        if (phone != null && phone.length() > 0)
            try {
                //act.moveToElement(elPhone).click().build().perform();
                wait.until(ExpectedConditions.elementToBeClickable(elPhone)).click();
                Thread.sleep(100);
                for (char num : phone.toCharArray()) {
                    Thread.sleep(50);
                    elPhone.sendKeys(Character.toString(num));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        if (mail != null && mail.length() > 0)
            elMail.sendKeys(mail);
        if (comment != null && comment.length() > 0)
            elComment.sendKeys(comment);
        if (!elAgreement.isSelected() && checkAgreement) {
            act.moveToElement(elAgreement).click().build().perform();
//            elAgreement.click();
        }
        try {
            Thread.sleep(100);
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();
            act.moveToElement(submitBtn).click().build().perform();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeMethod
    private void openDriver() {
        try {
            chrome.get(URL);
            Thread.sleep(2000);
            elFio = chrome.findElement(By.name("fio"));
            elPhone = chrome.findElement(By.name("phone"));
            elMail = chrome.findElement(By.name("email"));
            elAgreement = chrome.findElement(By.cssSelector("span.style_checkmark___GZe2"));//(By.name("accept"));
            elComment = chrome.findElement(By.name("message"));
            submitBtn = chrome.findElement(By.cssSelector("form.style_form__8TDpF button[type='submit']"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterMethod
    private void checkResult() {
        try {
            Object obj = wait.until(ExpectedConditions.jsReturnsValue("return document.querySelector('div.style_modalWrapper__UG622') != null"));
            Assert.assertTrue(testIndex < 3 && (boolean) obj);
            if ((boolean) obj)
                System.out.println("Test pass");
            else
                System.out.println("Test fail");
        } catch (TimeoutException ex) {
            List<WebElement> errors = chrome.findElements(By.cssSelector("div[class^='style_error_']"));

            switch (testIndex) {
                case 3:
                case 4:
                case 5:
                    Assert.assertEquals(errors.get(0).getText(), "Поле обязательно для заполнения");
                    break;
                case 6:
                    Assert.assertEquals(errors.get(0).getText(), "Заполните поле E-mail");
                    break;
            }
            if (errors.size() == 1)
                System.out.println("Test pass");
            else
                System.out.println("Test fail");
        }

    }

    @AfterTest
    private void closeDriver() {
        chrome.close();
    }
}
