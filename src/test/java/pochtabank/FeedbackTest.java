package pochtabank;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
                {"Клементьев Вадим Владимирович", "(977) 567-67-67", "testcase@mail.ru", "Хьюстон, у вас проблемы", true, "с заполнением всех полей валидными данными"},
                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, true, "с заполнением только обязательных полей валидными данными"},
                {"Клементьев Вадим Владимирович", "9775676767", "testcase@mail.ru", null, false, "Проверка обязательности поля \"Даю согласие на обработку  персональных данных\""},
                {null, null, null, null, true, "Отправка \"Формы обратной связи\" с пустыми полями"},
                {null, "9775676767", "testcase@mail.ru", null, true, "Проверка реакции системы при вводе невалидных данных в поле \"ФИО\""},
                {"Клементьев Вадим Владимирович", "9775676767", "", null, true, "Проверка реакции системы при вводе невалидных данных в поле \"E-mail\""},
        };
    }

    @BeforeTest
    private void setupTest() {
        System.setProperty("webdriver.chrome.driver", "E:\\Projects\\Autotest\\ChromeDriver\\chromedriver.exe");
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        options.addArguments("--remote-debugging-port=22785");
        options.addArguments("start-maximized");
        options.addArguments("--disable-notifications");
        chrome = new ChromeDriver(options);
        chrome.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(chrome, Duration.ofSeconds(15));
        testIndex = 0;
        System.out.println("setupTest");
    }

    @Parameters({"sms"})
    @Test(dataProvider = "sms")
    @Description(value = "Проверка работы \"Формы обратной связи\". {condition}")
    public void testNoSms(String fio, String phone, String mail, String comment, Boolean checkAgreement, String condition) {

        System.out.println("Test " + (testIndex + 1) + " - " + condition);

        Actions act = new Actions(chrome);
        if (fio != null && fio.length() > 0) {
            wait.until(ExpectedConditions.visibilityOf(elFio));
            act.moveToElement(elFio).sendKeys(fio).build().perform();
            elFio.sendKeys(fio);
//            String script = String.format("$('input[name=\"fio\"]').val('%s').change()", fio);
//            System.out.println(script);
//            ((JavascriptExecutor) chrome).executeScript(script);
            // elFio = chrome.findElement(By.name("fio"));
            elPhone = chrome.findElement(By.name("phone"));
            System.out.println("FIO - " + elFio.getAttribute("value"));
        }
        if (phone != null && phone.length() > 0) {
            try {
//                wait.until(ExpectedConditions.visibilityOf(elPhone));
                wait.until(ExpectedConditions.elementToBeClickable(elPhone));
                act.moveToElement(elPhone).click().build().perform();
                Thread.sleep(100);
                elPhone.clear();
                act.moveToElement(elMail).sendKeys(phone).build().perform();
                elPhone.sendKeys(phone);

//                for (char num : phone.toCharArray()) {
//                    Thread.sleep(50);
//                    elPhone.sendKeys(Character.toString(num));
//                }
//                String script = String.format("$('input[name=\"phone\"]').focus().val('%s').blur()", phone);
//                System.out.println(script);
//                ((JavascriptExecutor) chrome).executeScript(script);
                System.out.println("Phone - " + elPhone.getAttribute("value"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ElementClickInterceptedException ie) {
                System.out.println(ie.getLocalizedMessage());
            }
        }
        if (mail != null && mail.length() > 0) {
            System.out.println("Start fill email");
//            wait.until(ExpectedConditions.visibilityOf(elMail));
//            wait.until(ExpectedConditions.elementToBeClickable(elMail));
            act.moveToElement(elMail).sendKeys(mail).build().perform();
            elMail.sendKeys(mail);
//            String script = String.format("$('input[name=\"email\"]').click().val('%s')", mail);
//            System.out.println(script);
//            ((JavascriptExecutor) chrome).executeScript(script);
            elMail = chrome.findElement(By.name("email"));
            System.out.println("Email - " + elMail.getAttribute("value"));
        }
        if (comment != null && comment.length() > 0) {
//            wait.until(ExpectedConditions.visibilityOf(elComment));
            elComment.sendKeys(comment);
//            String script = String.format("$('input[name=\"message\"]').click().val('%s')", comment);
//            System.out.println(script);
//            ((JavascriptExecutor) chrome).executeScript(script);
            elMail = chrome.findElement(By.name("message"));
            System.out.println("Comment - " + elComment.getAttribute("value"));
        }
        try {
            if (checkAgreement) {
                Thread.sleep(100);
//                wait.until(ExpectedConditions.visibilityOf(elAgreement));
                wait.until(ExpectedConditions.elementToBeClickable(elAgreement));
                act.moveToElement(elAgreement).click().build().perform();
//                String script = "$('span.style_checkmark___GZe2').click()";
//                System.out.println(script);
//                ((JavascriptExecutor) chrome).executeScript(script);
                elAgreement = chrome.findElement(By.cssSelector("span.style_checkmark___GZe2"));
                System.out.println("check agreement - "  + elAgreement.isSelected());
            }
            Thread.sleep(100);
//            wait.until(ExpectedConditions.visibilityOf(submitBtn));
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            act.moveToElement(submitBtn).click().build().perform();
//            String script = "$('form.style_form__8TDpF button[type=\"submit\"]').click()";
//            System.out.println(script);
//            ((JavascriptExecutor) chrome).executeScript(script);
            System.out.println("click submit");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ElementClickInterceptedException ie) {
            System.out.println(ie.getLocalizedMessage());
        }
    }

    @BeforeMethod
    @Step()
    private void openDriver() {
        try {
            chrome.get(URL);
            //String window = chrome.getWindowHandle();
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
        System.out.println("beforeMethod");
    }

    @AfterMethod
    @Step()
    private void checkResult() {
        try {
            Thread.sleep(100);
            List<WebElement> errors = chrome.findElements(By.cssSelector("div[class^='style_error_']"));
            if (errors.size() == 0) {
                wait.until(ExpectedConditions.jsReturnsValue("return document.querySelector('div.style_modalWrapper__UG622')"));
                Assert.assertTrue(testIndex < 3, dbData()[testIndex][5].toString());
                System.out.println("Test pass");
            } else if(testIndex > 1){
                checkErrors(errors);
            }else{
                System.out.println("Test fail");
            }
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            System.out.println("Test fail");
        }
        testIndex++;
        System.out.println("afterMethod");

    }

    private void checkErrors(List<WebElement> errors) {
        System.out.println("checkErrors - case " + testIndex);
        System.out.println("Errors list size - " + errors.size());
        switch (testIndex) {
            case 3:
                Assert.assertEquals(errors.size(), 3, dbData()[testIndex][5].toString());
                break;
            case 2:
            case 4:
                Assert.assertEquals(errors.size(), 1, dbData()[testIndex][5].toString());
                Assert.assertEquals(errors.get(0).getText(), "Поле обязательно для заполнения");
                break;
            case 5:
                Assert.assertEquals(errors.size(), 1, dbData()[testIndex][5].toString());
                Assert.assertEquals(errors.get(0).getText(), "Заполните поле E-mail");
                break;
        }
        System.out.println("Test pass");
    }

    @AfterTest
    private void closeDriver() {
        if (chrome != null)
            chrome.quit();
        System.out.println("afterTest");
    }
}
