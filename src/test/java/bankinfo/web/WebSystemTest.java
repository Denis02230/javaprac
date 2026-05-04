package bankinfo.web;

import bankinfo.dao.TestDbHelper;
import bankinfo.test.EmbeddedPostgresHolder;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class WebSystemTest {

    private static Tomcat tomcat;
    private static String baseUrl;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void startWebApplication() throws Exception {
        EmbeddedPostgresHolder.start();
        TestDbHelper.recreateAndFillDatabase();

        int port = pickRandomPort();
        tomcat = new Tomcat();

        Path baseDir = Path.of("build", "tomcat-system-tests");
        Files.createDirectories(baseDir);

        tomcat.setBaseDir(baseDir.toAbsolutePath().toString());
        tomcat.setPort(port);
        tomcat.getConnector();

        File webAppDir = Path.of("src", "main", "webapp").toFile();
        Context context = tomcat.addWebapp("", webAppDir.getAbsolutePath());
        context.setParentClassLoader(WebSystemTest.class.getClassLoader());

        tomcat.start();
        baseUrl = "http://127.0.0.1:" + port;
    }

    @AfterClass(alwaysRun = true)
    public void stopWebApplication() throws Exception {
        try {
            if (tomcat != null) {
                tomcat.stop();
                tomcat.destroy();
            }
        } finally {
            tomcat = null;
            EmbeddedPostgresHolder.stop();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpTestDataAndDriver() {
        TestDbHelper.recreateAndFillDatabase();

        HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(true);
        htmlUnitDriver.setJavascriptEnabled(true);
        driver = htmlUnitDriver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void shouldNavigateMainSectionsViaMenuLinks() {
        open("/");
        waitForHeading("Main Navigation");

        clickNavLink("Branches");
        waitForHeading("Branch List");

        clickNavLink("Clients");
        waitForHeading("Client List");

        clickNavLink("Accounts");
        waitForHeading("Account List");

        clickNavLink("Operations");
        waitForHeading("Operations Journal");

        clickNavLink("Interest");
        waitForHeading("Interest Run");
    }

    @Test
    public void branchFlow_shouldCreateAndThenDeleteBranchFromUi() {
        String branchName = "UI E2E Branch";

        open("/branches");
        waitForHeading("Branch List");
        clickLink("Add branch");

        waitForHeading("Branch Form");
        typeByName("name", branchName);
        typeByName("address", "Moscow, UI st, 101");
        clickButton("Save");

        waitForHeading("Branch Card");
        assertBodyContains("Success: Branch saved");
        assertBodyContains("Name: " + branchName);

        clickButton("Delete");
        waitForHeading("Branch List");
        assertBodyContains("Success: Branch deleted");

        typeByName("q", branchName);
        clickButton("Search");
        waitForHeading("Branch List");
        assertBodyNotContains(branchName);
    }

    @Test
    public void branchFlow_shouldShowValidationError_whenNameIsEmpty() {
        open("/branches/form");
        waitForHeading("Branch Form");

        typeByName("name", "");
        typeByName("address", "Moscow, Empty Name st, 1");
        clickButton("Save");

        waitForHeading("Branch Form");
        assertBodyContains("Error: Branch name is required");
    }

    @Test
    public void clientFlow_shouldCreateClientAndBeVisibleInList() {
        String clientName = "UI E2E Client";

        open("/clients");
        waitForHeading("Client List");
        clickLink("Add client");

        waitForHeading("Client Form");
        selectByName("clientType", "PERSON");
        typeByName("displayName", clientName);
        clickButton("Save");

        waitForHeading("Client Card");
        assertBodyContains("Success: Client saved");
        assertBodyContains("Name: " + clientName);

        clickNavLink("Clients");
        waitForHeading("Client List");
        typeByName("q", clientName);
        clickButton("Filter");

        assertBodyContains(clientName);
    }

    @Test
    public void clientFlow_shouldShowValidationError_whenDisplayNameIsEmpty() {
        open("/clients/form");
        waitForHeading("Client Form");

        typeByName("displayName", "");
        clickButton("Save");

        waitForHeading("Client Form");
        assertBodyContains("Error: Display name is required");
    }

    @Test
    public void accountFlow_shouldOpenPostCreditAndCloseAccountThroughUi() {
        open("/accounts");
        waitForHeading("Account List");
        clickLink("Open account");

        waitForHeading("Open Account");
        selectByName("clientId", "2");
        selectByName("branchId", "2");
        selectByName("accountTypeId", "2");
        clickButton("Create");

        waitForHeading("Account Card");
        assertBodyContains("Success: Account opened");
        String accountNumber = extractValueAfterPrefix("Number:");
        assertFalse(accountNumber.isBlank());

        clickLink("Credit");
        waitForHeading("Account Operation");
        typeByName("amount", "100");
        typeByName("description", "UI credit");
        clickButton("Post operation");

        waitForHeading("Account Card");
        assertBodyContains("Success: Transaction posted");
        assertBodyContains("Balance: 100.00");

        clickLink("Debit");
        waitForHeading("Account Operation");
        typeByName("amount", "100");
        typeByName("description", "UI debit");
        clickButton("Post operation");

        waitForHeading("Account Card");
        assertBodyContains("Success: Transaction posted");
        assertBodyContains("Balance: 0.00");

        clickLink("Close account");
        waitForHeading("Close Account");
        clickButton("Confirm close");

        waitForHeading("Account Card");
        assertBodyContains("Success: Account closed");
        assertBodyContains("Status: CLOSED");

        clickNavLink("Accounts");
        waitForHeading("Account List");
        typeByName("q", accountNumber);
        clickButton("Filter");
        assertBodyContains(accountNumber);
        assertBodyContains("CLOSED");
    }

    @Test
    public void accountFlow_shouldShowValidationError_onWrongCloseSubmission() {
        open("/accounts/1/close");
        waitForHeading("Close Account");

        clickButton("Confirm close");

        waitForHeading("Close Account");
        assertBodyContains("Error: Account can be closed only with zero balance");
    }

    @Test
    public void transactionFlow_shouldShowValidationError_onBadInputAmount() {
        open("/accounts/1/tx?type=DEBIT");
        waitForHeading("Account Operation");

        typeByName("amount", "-1");
        typeByName("description", "bad amount");
        clickButton("Post operation");

        waitForHeading("Account Operation");
        assertBodyContains("Error: Amount must be greater than zero");
    }

    @Test
    public void interestFlow_shouldShowReportAndRejectBadTimestamp() {
        open("/interest");
        waitForHeading("Interest Run");

        typeByName("runAtIso", "2026-03-01T00:00:00+00:00");
        clickButton("Run interest");

        waitForHeading("Interest Run");
        assertBodyContains("Run report");
        assertBodyContains("Created operations:");
        assertBodyContains("Processed accounts");

        open("/interest");
        waitForHeading("Interest Run");
        typeByName("runAtIso", "bad-time");
        clickButton("Run interest");

        waitForHeading("Interest Run");
        assertBodyContains("Error: Invalid run timestamp format. Expected ISO offset date-time");
    }

    @Test
    public void branchFlow_shouldSearchEditAndRejectDeleteWithOpenAccounts() {
        open("/branches");
        waitForHeading("Branch List");
        typeByName("q", "Central");
        clickButton("Search");
        assertBodyContains("Central Branch");

        clickRowActionLink("Central Branch", "Open");
        waitForHeading("Branch Card");
        assertBodyContains("Name: Central Branch");

        clickLink("Edit");
        waitForHeading("Branch Form");
        typeByName("name", "Central Branch Updated");
        typeByName("address", "Moscow, Updated Street, 1");
        clickButton("Save");

        waitForHeading("Branch Card");
        assertBodyContains("Success: Branch saved");
        assertBodyContains("Name: Central Branch Updated");

        clickButton("Delete");
        waitForHeading("Branch Card");
        assertBodyContains("Error: Cannot delete branch with open accounts");
    }

    @Test
    public void clientFlow_shouldFilterOpenEditAndRejectDeleteWithAccounts() {
        open("/clients");
        waitForHeading("Client List");
        typeByName("q", "Alpha");
        selectByName("type", "ORG");
        clickButton("Filter");

        assertBodyContains("Alpha LLC");
        clickRowActionLink("Alpha LLC", "Open");
        waitForHeading("Client Card");
        assertBodyContains("Name: Alpha LLC");
        assertBodyContains("Arman Hakobyan");
        assertBodyContains("ACC-0004");

        clickLink("Edit");
        waitForHeading("Client Form");
        typeByName("displayName", "Alpha LLC Updated");
        clickButton("Save");

        waitForHeading("Client Card");
        assertBodyContains("Success: Client saved");
        assertBodyContains("Name: Alpha LLC Updated");

        clickButton("Delete");
        waitForHeading("Client Card");
        assertBodyContains("Error: Cannot delete client with existing accounts");
    }

    @Test
    public void clientFlow_shouldDeleteClientWithoutAccounts() {
        String clientName = "Client Without Accounts";

        open("/clients/form");
        waitForHeading("Client Form");
        selectByName("clientType", "PERSON");
        typeByName("displayName", clientName);
        clickButton("Save");

        waitForHeading("Client Card");
        assertBodyContains("Name: " + clientName);
        clickButton("Delete");

        waitForHeading("Client List");
        assertBodyContains("Success: Client deleted");
        typeByName("q", clientName);
        clickButton("Filter");
        assertBodyNotContains(clientName);
    }

    @Test
    public void clientFlow_shouldOpenAccountCardFromClientCard() {
        open("/clients");
        waitForHeading("Client List");
        typeByName("q", "Anna");
        clickButton("Filter");
        clickRowActionLink("Anna Sargsyan", "Open");

        waitForHeading("Client Card");
        clickRowActionLink("ACC-0002", "Open");
        waitForHeading("Account Card");
        assertBodyContains("Number: ACC-0002");
    }

    @Test
    public void accountList_shouldFilterAndOpenAccountCard() {
        open("/accounts");
        waitForHeading("Account List");
        selectByName("status", "CLOSED");
        selectByName("clientId", "2");
        selectByName("branchId", "2");
        selectByName("accountTypeId", "2");
        clickButton("Filter");

        assertBodyContains("ACC-0008");
        assertBodyNotContains("ACC-0007");
        clickRowActionLink("ACC-0008", "Open");
        waitForHeading("Account Card");
        assertBodyContains("Number: ACC-0008");
    }

    @Test
    public void accountCard_shouldFilterOperationsByPeriod() {
        open("/accounts/1");
        waitForHeading("Account Card");

        typeByName("from", "2026-02-07T00:00:00+00:00");
        typeByName("to", "2026-02-07T23:59:59+00:00");
        clickButton("Apply");

        waitForHeading("Account Card");
        assertBodyContains("Card payment");
        assertBodyNotContains("Salary");
    }

    @Test
    public void debitFlow_shouldRejectOperationBlockedByAccountTypeRules() {
        open("/accounts/5/tx?type=DEBIT");
        waitForHeading("Account Operation");
        typeByName("amount", "100");
        typeByName("description", "blocked debit");
        clickButton("Post operation");

        waitForHeading("Account Operation");
        assertBodyContains("Error: Debit operation is not allowed for account type Deposit To Other");
    }

    @Test
    public void operationsFlow_shouldFilterByPeriodAndOpenAccount() {
        open("/operations");
        waitForHeading("Operations Journal");

        typeByName("from", "2026-02-06T10:00:30+00:00");
        typeByName("to", "2026-02-06T10:01:30+00:00");
        selectByName("type", "INTEREST");
        typeByName("accountId", "4");
        typeByName("clientId", "4");
        typeByName("branchId", "1");
        typeByName("accountTypeId", "2");
        clickButton("Filter");

        waitForHeading("Operations Journal");
        assertBodyContains("INTEREST");
        assertBodyContains("ACC-0004");
        clickRowActionLink("ACC-0004", "Open account");
        waitForHeading("Account Card");
        assertBodyContains("Number: ACC-0004");
    }

    @Test
    public void accountTypeFlow_shouldSearchOpenEditAndSaveRules() {
        open("/account-types");
        waitForHeading("Account Types");
        typeByName("q", "Savings");
        clickButton("Search");

        assertBodyContains("Savings Basic");
        clickRowActionLink("Savings Basic", "Open");
        waitForHeading("Account Type Card");
        assertBodyContains("Interest interval: MONTHLY");

        clickLink("Edit");
        waitForHeading("Account Type Form");
        typeByName("name", "Savings Basic Updated");
        typeByName("interestRate", "0.0600");
        selectByName("interestInterval", "QUARTERLY");
        clickButton("Save");

        waitForHeading("Account Type Card");
        assertBodyContains("Success: Account type saved");
        assertBodyContains("Name: Savings Basic Updated");
        assertBodyContains("Interest interval: QUARTERLY");
    }

    @Test
    public void accountTypeFlow_shouldShowValidationErrorForEmptyName() {
        open("/account-types/1/edit");
        waitForHeading("Account Type Form");
        typeByName("name", "");
        clickButton("Save");

        waitForHeading("Account Type Form");
        assertBodyContains("Error: Account type name is required");
    }

    private void open(String path) {
        driver.get(baseUrl + path);
    }

    private void waitForHeading(String headingText) {
        waitUntil(d -> {
            List<WebElement> headings = d.findElements(By.tagName("h1"));
            for (WebElement heading : headings) {
                if (headingText.equals(heading.getText().trim())) {
                    return true;
                }
            }
            return false;
        }, "Expected H1 not found: " + headingText);
    }

    private void clickNavLink(String linkText) {
        WebElement nav = waitUntilElement(By.tagName("nav"), "Navigation menu not found");
        for (WebElement link : nav.findElements(By.tagName("a"))) {
            if (linkText.equals(link.getText().trim())) {
                link.click();
                return;
            }
        }
        throw new AssertionError("Nav link not found: " + linkText);
    }

    private void clickLink(String linkText) {
        WebElement link = waitUntilElement(By.linkText(linkText), "Link not found: " + linkText);
        link.click();
    }

    private void clickRowActionLink(String rowText, String linkText) {
        By by = By.xpath(
                "//tr[td[contains(normalize-space(.), " + xpathLiteral(rowText) + ")]]//a[normalize-space()="
                        + xpathLiteral(linkText) + "]"
        );
        WebElement link = waitUntilElement(by, "Row link not found: row='" + rowText + "', link='" + linkText + "'");
        link.click();
    }

    private void clickButton(String buttonText) {
        for (WebElement button : driver.findElements(By.tagName("button"))) {
            if (buttonText.equals(button.getText().trim())) {
                button.click();
                return;
            }
        }
        throw new AssertionError("Button not found: " + buttonText);
    }

    private void typeByName(String fieldName, String value) {
        WebElement input = waitUntilElement(By.name(fieldName), "Field not found: " + fieldName);
        input.clear();
        if (value != null) {
            input.sendKeys(value);
        }
    }

    private void selectByName(String fieldName, String optionValue) {
        WebElement selectElement = waitUntilElement(By.name(fieldName), "Select not found: " + fieldName);
        new Select(selectElement).selectByValue(optionValue);
    }

    private void assertBodyContains(String expected) {
        String bodyText = waitUntilBodyText(expected);
        assertTrue(bodyText.contains(expected), "Expected page to contain: " + expected + "\nActual:\n" + bodyText);
    }

    private void assertBodyNotContains(String expected) {
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertFalse(bodyText.contains(expected), "Expected page not to contain: " + expected + "\nActual:\n" + bodyText);
    }

    private String extractValueAfterPrefix(String prefix) {
        String bodyText = driver.findElement(By.tagName("body")).getText();
        for (String line : bodyText.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
                return trimmed.substring(prefix.length()).trim();
            }
        }
        return "";
    }

    private String waitUntilBodyText(String expected) {
        waitUntil(d -> d.findElement(By.tagName("body")).getText().contains(expected),
                "Body text did not contain: " + expected);
        return driver.findElement(By.tagName("body")).getText();
    }

    private WebElement waitUntilElement(By by, String errorMessage) {
        waitUntil(d -> {
            try {
                WebElement element = d.findElement(by);
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException e) {
                return null;
            }
        }, errorMessage);

        return driver.findElement(by);
    }

    private <T> T waitUntil(ExpectedCondition<T> condition, String errorMessage) {
        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            throw new AssertionError(errorMessage, e);
        }
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        String[] parts = value.split("'");
        StringBuilder builder = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(", \"'\", ");
            }
            builder.append("'").append(parts[i]).append("'");
        }
        builder.append(")");
        return builder.toString();
    }

    private int pickRandomPort() {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to pick random port", e);
        }
    }
}
