package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.yaml.snakeyaml.Yaml;

import static common.constant.CommonConstants.EXPLICIT_WAIT_IN_SECONDS;

import java.io.InputStream;
import java.util.Map;

public class BasePage {
    public static WebDriver driver;
    public final WebDriverWait wait;
    private Map<String, Object> elements;

    public BasePage(WebDriver driver) {
        BasePage.driver = driver;
        wait = new WebDriverWait(driver, EXPLICIT_WAIT_IN_SECONDS);
        loadYamlFile(getClass().getSimpleName().toLowerCase() + ".yaml");
    }

    @SuppressWarnings("unchecked")
    private void loadYamlFile(String yamlFileName) {
        Yaml yaml = new Yaml();
        String yamlFilePath = "locators/" + yamlFileName;
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(yamlFilePath)) {
            elements = yaml.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public By getLocator(String key) {
        Map<String, Map<String, Map<String, String>>> elementsMap = (Map<String, Map<String, Map<String, String>>>) elements.get("elements");
        for (String elementKey : elementsMap.keySet()) {
            if (elementKey.equalsIgnoreCase(key)) {
                String locator = elementsMap.get(elementKey).get("en_US").get("web");
                return By.xpath(locator);
            }
        }
        return null;
    }

    public void waitUntilElementVisible(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitUntilElementClickable(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public void click(By by) {
        waitUntilElementClickable(by);
        driver.findElement(by).click();
    }

    public void sendKeys(By by, String text) {
        waitUntilElementVisible(by);
        driver.findElement(by).sendKeys(text);
    }

    public String getText(By by) {
        waitUntilElementVisible(by);
        return driver.findElement(by).getText();
    }

    public WebElement getElement(By by) {
        waitUntilElementVisible(by);
        return driver.findElement(by);
    }

    public void click(String key) {
        By by = getLocator(key);
        if (by != null) {
            click(by);
        } else {
            throw new IllegalArgumentException("Locator not found for key: " + key);
        }
    }

    public void sendKeys(String key, String text) {
        By by = getLocator(key);
        if (by != null) {
            sendKeys(by, text);
        } else {
            throw new IllegalArgumentException("Locator not found for key: " + key);
        }
    }

    public String getText(String key) {
        By by = getLocator(key);
        if (by != null) {
            return getText(by);
        } else {
            throw new IllegalArgumentException("Locator not found for key: " + key);
        }
    }

    public WebElement getElement(String key) {
        By by = getLocator(key);
        if (by != null) {
            return getElement(by);
        } else {
            throw new IllegalArgumentException("Locator not found for key: " + key);
        }
    }
}
