package common.constant;


import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.event.KeyEvent.*;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public abstract class AbstractProjectPage extends AbstractPage {

  private static final int WAIT_TIME = 5; // in seconds

  public AbstractProjectPage() {
    try {
      loadLocators();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected static boolean isMobilePage() {
    String pagedetail = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return pagedetail.equalsIgnoreCase("mweb");
  }

  public static String getTargetClient() {
    return Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
  }

  public static String getBrowser() {
    return Config.getConfigProperty(ConfigProperty.BROWSER);
  }

  /** returning element if clickable */
  public static WebElement waitUntilElementIsClickable(
      String elementLocator, Class<? extends Throwable> ignoring) {
    return WebDriverWaitUtils.waitUntilElementIsClickable(elementLocator);
  }

  // To hide the keyboard
  @Step("Hide Soft keyboard")
  public static void hideKeyboard() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.hideKeyboard();
  }

  // Capturing screenshot
  @Step("Capture Screenshot")
  public static File takeScreenShot() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    String destinationPath;
    DateFormat dateFormat;
    destinationPath = "src/test/resources/screenshot/";
    File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
    new File(destinationPath).mkdirs();
    // Set file name with current date time.
    String destinationFile = dateFormat.format(new Date()) + ".png";
    try {
      // Copy paste file at destination folder location
      FileUtils.copyFile(screenshotFile, new File(destinationPath + "/" + destinationFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return screenshotFile;
  }

  public static void setClipboardData(String string) {
    // StringSelection is a class that can be used for copy and paste operations.
    StringSelection stringSelection = new StringSelection(string);
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
  }

  protected void loadLocators() throws IOException {
    String className = this.getClass().getSimpleName().toLowerCase();
    setLocatorFileName(className + ".yaml");
    load();
  }

  protected boolean isWebPage() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("web");
  }

  protected boolean isAndroid() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("android");
  }

  protected boolean isIOS() {
    String targetClient = Config.getConfigProperty(ConfigProperty.TARGET_CLIENT);
    return targetClient.equalsIgnoreCase("ios");
  }

  protected boolean isBrowserStack() {
    String browserStackEnabled = Config.getConfigProperty(ConfigProperty.SELENIUM_USE_BROWSERSTACK);
    return browserStackEnabled.equalsIgnoreCase("true");
  }

  public void lowWait() {
    WebUtil.sleep(2000);
  }
  public void mediumWait(){
    WebUtil.sleep(3000);
  }

  public void highWait() {
    WebUtil.sleep(5000);
  }

  public void clickUsingActions(String element) {
    if (isBrowserStack()) {
      waitAndCheckIsElementIsPresent(element);
      MobileElement ele = (MobileElement) locateElement(element);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions.tap(new TapOptions().withElement(new ElementOption().withElement(ele))).perform();
    } else {
      waitAndCheckIsElementIsPresent(element);
      Actions act = new Actions(Grid.driver());
      act.moveToElement(locateElement(element)).click().perform();
    }
  }

  public void scrollSliderActions(String element) {
    Actions act = new Actions(Grid.driver());
    act.dragAndDropBy(locateElement(element), 50, 216).click().build().perform();
  }

  public WebElement waitUntilElementIsPresent(String key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  public WebElement waitUntilElementIsPresent(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  public boolean waitAndCheckIsElementIsPresent(String key) {
    try {
      WebElement element = waitUntilElementIsPresent(key);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckIsElementIsPresent(String... key) {
    try {
      WebElement element = waitUntilElementIsPresent(key);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckIsSelected(String key) {
    return locateElement(key).isSelected();
  }

  public void pageRefresh() {
    Grid.driver().navigate().refresh();
  }

  public void scrollToElement(String key, String direction, int highpxl) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.verticalScroll(locateElement(key));
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void scrollToElement(String direction, int highpxl, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 15) {
        WebUtil.verticalScroll(waitAndGetElement(key));
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public String mouseOverAndReturnCursorString(String key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    // Check that the cusrsor does not change to pointer
    return ele.getCssValue("cursor");
  }

  public String mouseOverAndReturnCursorString(String... key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    // Check that the cusrsor does not change to pointer
    return ele.getCssValue("cursor");
  }

  public int getWidthOfScreen() {
    return Grid.driver().manage().window().getSize().getWidth();
  }

  public int getHeightOfScreen() {
    return Grid.driver().manage().window().getSize().getHeight();
  }

  public boolean waitAndCheckIsElementPresent(String... key) {
    return waitAndCheckIsElementPresent(WAIT_TIME, key);
  }

  public boolean waitAndCheckIsElementPresent(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsPresent(
              getLocator(key), WebDriverException.class, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckIsElementClickable(long waitTimeInSec, String key) {
    try {
      WebElement element = WebDriverWaitUtils.waitUntilElementIsClickable(key, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckIsElementClickable(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsClickable(getLocator(key), waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * Wait and and check is element is selected
   *
   * @param key
   */
  @Step("Check element {key} is selected")
  public boolean waitAndCheckIsElementSelected(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .isSelected();
  }

  @Step("Clear {key} Text Box")
  public void clearTextBox(String... key) {
    if (isWebPage() || isMobilePage()) {
      waitAndGetElement(key).clear(); // Changed this bcoz ctrl+a is not working for chrome
    } else if (isAndroid()) {
      WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
          .clear();
    } else {
      waitAndGetElement(key).clear();
    }
  }

  
  public String getURL() {
    return Grid.driver().getCurrentUrl();
  }

  public String getTitle() {
    return Grid.driver().getTitle();
  }

  public void clickOnBackBrowser() {
    if (isWebPage()) {
      Grid.driver().navigate().back();
      WebUtil.sleep();
    } else if (isAndroid()) {
      ((SauronAppiumAndroidDriver) Grid.driver()).pressKey(new KeyEvent().withKey(AndroidKey.BACK));
    }
  }

  public void navigateToHomePage() {
    if (isWebPage()) {
      Grid.driver().navigate().to("https://udaan.com");
      WebUtil.sleep();
    } else if (isAndroid()) {
      // ((SauronAppiumAndroidDriver) Grid.driver()).pressKey(new
      // KeyEvent().withKey(AndroidKey.BACK));
    }
  }

  public int waitAndGetSizeOfAnElements(String key) {
    try {
      WebUtil.sleep();
      List<WebElement> elements = locateElements(key);
      return elements.size();
    } catch (Exception ex) {
      //
    }
    return 0;
  }

  public int waitAndGetSizeOfAnElements(String... key) {
    try {
      WebUtil.sleep();
      List<WebElement> elements = waitAndGetElements(key);
      return elements.size();
    } catch (Exception ex) {
      //
    }
    return 0;
  }
  // create the random number
  public int getRandomNumber() {
    int randomNumber = 0;
    try {
      Random rand = new Random();
      randomNumber = rand.nextInt(999);
    } catch (NumberFormatException | NullPointerException exception) {
      log.info("not able to fetch the random number");
    }
    return randomNumber;
  }

  public ArrayList<Double> sortWithPrice(String key) {
    waitAndCheckIsElementIsPresent(key);
    String productPrice = "";
    String mrpText = "";
    ArrayList<Double> obtainedList = new ArrayList<>();
    List<WebElement> elementList = locateElements(key);
    if (elementList.size() > 1) {
      if (isWebPage() || isAndroid()) {
        for (WebElement we : elementList) {
          productPrice = "";
          if (we.getText().contains(".")) {
            mrpText = we.getText().split("[.]")[0];
          } else if (we.getText().contains("|")) {
            mrpText = we.getText().split("[|]")[0];
          }
          if (!(mrpText.isEmpty())) {
            for (int i = 0; i < mrpText.length(); i++) {
              if (Character.isDigit(mrpText.charAt(i))) {
                productPrice = productPrice + mrpText.charAt(i);
              }
            }
          }
          obtainedList.add(Double.parseDouble(productPrice));
        }
      }
    } else {
      log.info("Not able to get the price text from the listing");
      return null;
    }
    return obtainedList;
  }

  public boolean waitAndCheckWithTextOfAnElements(String key, String textToCompare) {
    boolean isTureOrFalse = false;
    try {
      List<WebElement> elements = locateElements(key);
      for (WebElement element : elements) {
        if (element.getText().toLowerCase().contains(textToCompare.toLowerCase())) {
          isTureOrFalse = true;
        } else {
          isTureOrFalse = false;
          break;
        }
      }
    } catch (Exception ex) {
      isTureOrFalse = false;
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }

  public boolean waitAndCheckWithTextOfAnElements(String textToCompare, String... key) {
    boolean isTureOrFalse = false;
    try {
      List<WebElement> elements = waitAndGetElements(key);
      for (WebElement element : elements) {
        if (element.getText().toLowerCase().contains(textToCompare.toLowerCase())) {
          isTureOrFalse = true;
        } else {
          break;
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }

  public ArrayList<Integer> filterWithPrice(String key) {
    ArrayList<Integer> obtainedList = new ArrayList<>();
    List<WebElement> elementList = locateElements(key);
    for (WebElement we : elementList) {
      String priceText = we.getText();
      String price = priceText.split("\\.", 2)[0].replaceAll("₹", "").trim();
      double priceValue2 = Double.parseDouble(price);
      int realvalue = (int) priceValue2;
      obtainedList.add(realvalue);
      // Collections.sort(obtainedList);
    }
    return obtainedList;
  }

  /**
   * click on element
   *
   * @param key
   */
  @Step("Click on {key}")
  public void clickOn(String key) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class).click();
  }

  /**
   * click on element
   *
   * @param key
   */
  @Step("Click on {key}")
  public void clickOn(String... key) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class).click();
  }

  /**
   * Wait for WebElement and click on it
   *
   * @param key
   */
  @Step("Click on {key}")
  public void waitAndClickOn(String key) {
    waitUntilElementIsClickable(getLocator(key), WebDriverException.class).click();
  }

  /**
   * Wait for WebElement and click on it
   *
   * @param key
   */
  @Step("Click on {key}")
  public void waitAndClickOn(String... key) {
    waitUntilElementIsClickable(getLocator(key), WebDriverException.class).click();
  }

  /**
   * Get element text
   *
   * @param key
   */
  @Step("Get text of {key}")
  public String waitAndGetText(String key) {
    waitAndCheckIsElementIsPresent(key);
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getText();
  }

  /**
   * Get element text
   *
   * @param key
   */
  @Step("Get text of {key}")
  public String waitAndGetText(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getText();
  }

  /**
   * Get element text
   *
   * @param key
   */
  @Step("Get text of {key}")
  public String waitAndGetTextByAttribute(String key, String attributeName) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getAttribute(attributeName);
  }

  /**
   * Get element text
   *
   * @param key
   */
  @Step("Get text of {key}")
  public String waitAndGetAttribute(String attribute, String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .getAttribute(attribute);
  }

  /**
   * Get element text
   *
   * @param key
   */
  @Step("Get text of {key}")
  public String getText(String key) {
    return locateElement(key).getText();
  }

  /**
   * Set element text and press Enter
   *
   * @param key
   */
  @Step("Set element text {text} for {key}")
  public void setTextAndClickEnter(String key, String text) {

    if (isAndroid()) {
      waitUntilElementIsPresent(key);
      if (waitAndCheckIsElementIsPresent(key)) {
        waitAndClickOn(key);
      }
      AndroidDriver driver = (AndroidDriver) Grid.driver();
      KeyEvent keyEvent = new KeyEvent();
      waitUntilElementIsPresent(key).clear();
      driver.getKeyboard().sendKeys(text);
      driver.pressKey(keyEvent.withKey(AndroidKey.ENTER));

    } else {
      waitAndClickOn(key);
      waitUntilElementIsPresent(key).clear();
      waitAndSetText(key, text);
      waitAndClickOn(key);
      Actions actions = new Actions(Grid.driver());
      actions.sendKeys(Keys.ENTER).build().perform();
    }
  }

  @Step("Set text {text} into {key} for Android")
  public void waitAndSetTextForAndroidAndWeb(String key, String text) {
    if (isAndroid()) {
      // waitAndClickOn(key);
      AndroidDriver driver = (AndroidDriver) Grid.driver();

      driver.getKeyboard().sendKeys(text);
    } else if (isWebPage()) {
      waitAndSetText(key, text);
    }
  }

  public void hideKeyboardAndroid() {
    if (isAndroid()) {
      ((SauronAppiumAndroidDriver) Grid.driver()).hideKeyboard();
    }
  }

  /**
   * Set element text
   *
   * @param key
   */
  @Step("Set text {text} into {key} ")
  public void waitAndSetText(String key, String text) {
    WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .sendKeys(text);
  }

  /**
   * Wait and and check is element is displayed
   *
   * @param key
   */
  @Step("Check element {key} is displayed")
  public boolean waitAndCheckIsElementDisplayed(String key) {
    return WebDriverWaitUtils.waitUntilElementIsVisible(getLocator(key), WebDriverException.class)
        .isDisplayed();
  }

  public boolean waitAndCheckIsElementDisplayed(long waitTimeInSec, String... key) {
    try {
      WebElement element =
          WebDriverWaitUtils.waitUntilElementIsVisible(
              getLocator(key), WebDriverException.class, waitTimeInSec);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  /**
   * Wait and and check is element is displayed
   *
   * @param key
   */
  @Step("Check element {key} is displayed")
  public boolean waitAndCheckIsElementDisplayed(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class)
        .isDisplayed();
  }

  public WebElement waitAndGetElement(String... key) {
    return WebDriverWaitUtils.waitUntilElementIsPresent(getLocator(key), WebDriverException.class);
  }

  public List<WebElement> waitAndGetElements(String... key) {
    return HtmlElementUtils.locateElements(getLocator(key));
  }

  /** Open Notification Drawer and Fetch the OTP */
  public void openNotificationBar() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.openNotifications();
  }

  public void otpTimeOutWait() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.MINUTES);
  }

  // Load elements using PageFactory
  @Step("Uses PageFactory to load elements")
  public void loadElements() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    PageFactory.initElements(new AppiumFieldDecorator(driver), this);
  }

  // get specific item from a list
  @Step("Fetch List Of Elements Present")
  public AndroidElement getListElement(String elementName, List<AndroidElement> listElement) {
    for (int i = 0; i < listElement.size(); i++) {
      if (listElement.get(i).getText().contains(elementName)) {
        return listElement.get(i);
      }
    }
    return null;
  }

  // To click hardware back button
  @Step("Press hard back button")
  public void clickBackButton() {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.pressKey(new KeyEvent(AndroidKey.BACK));
  }

  // Loop to fetch OTP from notification
  private String OTPloop(int size, List<AndroidElement> element) {
    for (int i = 0; i < size; i++) {
      if (element.get(i).getText().contains("is your Udaan Login OTP.")) {
        return element.get(i).getText();
      }
    }
    return "";
  }

  // To extract OTP using Regex
  private String extractOTP(String OTP) {
    Pattern p = Pattern.compile("\\d+");
    Matcher m = p.matcher(OTP);
    while (m.find()) {
      if (m.group().length() == 4) {
        return m.group();
      }
    }
    return "";
  }

  // To retrieve OTP from the notifications
  @Step
  public String getOTP() throws InterruptedException {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    String OTP = new String();
    try {
      openNotificationBar();
      List<AndroidElement> messageText =
          driver.findElements(By.className("android.widget.TextView"));
      int size = messageText.size();
      for (int i = 0; i <= 3; i++) {
        if (OTP.length() == 0) {
          OTP = OTPloop(size, messageText);
        } else {
          break;
        }
      }
      OTP = extractOTP(OTP);
      clickBackButton();

    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return OTP;
  }

  @Step
  public void sendOTP(String realOtp) {
    if (realOtp != null) {
      Actions actions = new Actions(Grid.driver());
      actions.sendKeys(realOtp).build().perform();
    }
  }

  public void clickWithIndex(int index, String... key) {
    List<WebElement> elements;
    if (key.length > 1) {
      elements = waitAndGetElements(key);
    } else {
      elements = locateElements(key[0]);
    }
    WebElement element = elements.get(index);
    WebUtil.sleep(2000);
    element.click();
  }

  public void clickUsingActionsWithIndex(String key, int mobileIndex, int webIndex) {
    Actions act = new Actions(Grid.driver());
    if (isBrowserStack()) {
      List<WebElement> elements = locateElements(key);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions
          .tap(
              new TapOptions()
                  .withElement(new ElementOption().withElement(elements.get(mobileIndex))))
          .perform();
    } else {
      List<WebElement> elements = locateElements(key);
      act.moveToElement(elements.get(webIndex)).click().build().perform();
    }
  }

  public boolean waitAndCheckElementWithIndex(String key, int index) {
    waitAndCheckIsElementIsPresent(key);
    List<WebElement> elements = locateElements(key);
    try {
      WebElement element = elements.get(index);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public boolean waitAndCheckElementWithIndex(int index, String... key) {
    List<WebElement> elements = waitAndGetElements(key);
    try {
      WebElement element = elements.get(index);
      return element != null;
    } catch (Exception ex) {
      //
    }
    return false;
  }

  public WebElement findElementsAndReturnOnIndex(int index, String key) {
    List<WebElement> elements = locateElements(key);
    return elements.get(index);
  }

  public WebElement findElementsAndReturnOnIndex(int index, String... key) {
    List<WebElement> elements = waitAndGetElements(key);
    return elements.get(index);
  }

  public void lazyPageDownScroll(String key) {
    WebUtil.sleep();
    WebElement element = locateElement(key);
    if (element != null) {
      Actions actions = new Actions(Grid.driver());
      actions.click(element).sendKeys(element, Keys.PAGE_DOWN).build().perform();
    }
  }

  public void clickUsingActions(String... key) {
    if (isBrowserStack()) {
      waitAndCheckIsElementIsPresent(key);
      MobileElement ele = (MobileElement) waitAndGetElement(key);
      TouchAction actions = new TouchAction((PerformsTouchActions) Grid.driver());
      actions.tap(new TapOptions().withElement(new ElementOption().withElement(ele))).perform();
    } else {
      waitAndCheckIsElementIsPresent(key);
      Actions act = new Actions(Grid.driver());
      act.moveToElement(waitAndGetElement(key)).click().perform();
    }
  }

  public void scrollUsingActions(String key, String direction, int highpxl) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        Actions actions = new Actions(Grid.driver());
        // Scroll Down using Actions class
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
        WebUtil.sleep(2000);
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void scrollUsingActions(String direction, int highpxl, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        Actions actions = new Actions(Grid.driver());
        // Scroll Down using Actions class
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
        WebUtil.sleep(2000);
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 20) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void scrollUsingActionsWithSleep(String direction, int highpxl, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 50) {
        Actions actions = new Actions(Grid.driver());
        // Scroll Down using Actions class
        actions.sendKeys(Keys.PAGE_DOWN).build().perform();
        WebUtil.sleep(5000);
        retryCount++;
      }
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < 50) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void customScroll(String key, String direction, int highpxl, int noOfScrolls) {
    int retryCount = 0;
    if (isWebPage()) {
      WebUtil.verticalScroll(waitUntilElementIsPresent(key));
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < noOfScrolls) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public void customScroll(String direction, int highpxl, int noOfScrolls, String... key) {
    int retryCount = 0;
    if (isWebPage()) {
      waitAndCheckIsElementIsPresent(key);
      WebUtil.verticalScroll(waitAndGetElement(key));
    } else if (isAndroid()) {
      while (waitAndGetSizeOfAnElements(key) == 0 && retryCount < noOfScrolls) {
        WebUtil.mobileVerticalScroll(direction, highpxl);
        retryCount++;
      }
    }
  }

  public int getElementYAxisLocation(String key) {
    if (isAndroid()) {
      MobileElement ele = (MobileElement) locateElement(key);
      return ele.getLocation().y;
    } else {
      return locateElement(key).getLocation().y;
    }
  }

  public int getElementYAxisLocation(String... key) {
    if (isAndroid()) {
      MobileElement ele = (MobileElement) waitAndGetElement(key);
      return ele.getLocation().y;
    } else {
      return waitAndGetElement(key).getLocation().y;
    }
  }

  public String getTextColour(String key) {
    WebElement ele = waitAndGetElement(key);
    Actions builder = new Actions(Grid.driver());
    builder.moveToElement(ele).build().perform();
    return ele.getCssValue("color");
  }

  public String getBackgroundColour(String key) {
    WebElement ele = waitUntilElementIsPresent(key);
    return ele.getCssValue("background-color");
  }

  public String returnElementAttributeValue(String key, String cssType) {
    WebElement ele = waitAndGetElement(key);
    return ele.getAttribute(cssType.toLowerCase());
  }

  public boolean checkKeyboardShown() {
    return ((SauronAppiumAndroidDriver) Grid.driver()).isKeyboardShown();
  }

  public void handleAlert(String action) {
    WebDriverWait wait = new WebDriverWait(Grid.driver(), 5 /*timeout in seconds*/);
    Alert alert = wait.until(ExpectedConditions.alertIsPresent());
    wait.until(ExpectedConditions.alertIsPresent());
    if (action.equalsIgnoreCase("accept")) {
      alert.accept(); // Close Alert popup
    } else if (action.equalsIgnoreCase("close")) {
      alert.dismiss(); // Close Alert popup
    }
  }

  // File upload by Robot Class//
  public void uploadFileWithRobot(String imagePath) {
    StringSelection stringSelection = new StringSelection(imagePath);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    robot.delay(250);
    robot.keyPress(VK_ENTER);
    robot.keyRelease(VK_ENTER);
    robot.keyPress(VK_CONTROL);
    robot.keyPress(VK_V);
    robot.keyRelease(VK_V);
    robot.keyRelease(VK_CONTROL);
    robot.keyPress(VK_ENTER);
    robot.delay(150);
    robot.keyRelease(VK_ENTER);
  }

  // Place a file onto the device in a particular place
  public void placeFileOnToDevice(String remotePath, File pathName) throws IOException {
    AndroidDriver driver = (AndroidDriver) Grid.driver();
    driver.pushFile(
        remotePath, new File(String.valueOf(pathName))); // example "/data/local/tmp/foo.bar", new
    // File("/Users/qapitol/files/foo.bar"
  }

  public void closeWindow() {
    Robot robot = null;
    try {
      robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    robot.delay(250);
    robot.keyPress(VK_CANCEL);
  }

  public void uploadImage(String fileName) throws IOException {
    File classpathRoot = new File(System.getProperty("user.dir"));
    File assetDir = new File(classpathRoot, "../resources");
    File imagePath = new File(assetDir.getCanonicalPath(), fileName);
  }
  // checking for digits in a string
  public boolean checkForDigits(String text) {
    boolean isTureOrFalse = false;
    try {
      if (text == null) {
        isTureOrFalse = false;
      }
      for (int i = 0; i < text.length(); i++) {
        if (Character.isDigit(text.charAt(i))) {
          isTureOrFalse = true;
        } else {
          isTureOrFalse = false;
          break;
        }
      }
    } catch (Exception ex) {
      isTureOrFalse = false;
      ex.printStackTrace();
    }
    return isTureOrFalse;
  }
  // get the digits from the String
  public int getDigitFromText(String text) {
    if (!text.isEmpty()) {
      try {
        String digits = "";
        for (int i = 0; i < text.length(); i++) {
          if (Character.isDigit(text.charAt(i))) {
            digits = digits + text.charAt(i);
          }
        }
        return Integer.parseInt(digits);
      } catch (NumberFormatException | NullPointerException exception) {
        return 0;
      }
    } else {
      return 0;
    }
  }

  public boolean checkKeyboardIsEnabled() {
    return ((SauronAppiumAndroidDriver) Grid.driver()).isKeyboardShown();
  }

  public boolean sortingHighToLowAndAsserting(String key) {
    int startingIndex = 0;
    boolean sortResult = false;
    waitAndCheckIsElementIsPresent(key);
    if (null != sortWithPrice(key) && sortWithPrice(key).size() > 1) {
      waitAndCheckIsElementIsPresent(key);
      List<Double> obtainedListPricingList = sortWithPrice(key);
      ListIterator<Double> priceValuesCollection = obtainedListPricingList.listIterator();
      while (priceValuesCollection.hasNext()) {
        if (obtainedListPricingList.get(startingIndex)
            > obtainedListPricingList.get(startingIndex + 1)) {
          log.info("Sorting is working fine");
          if (startingIndex < obtainedListPricingList.size()) {
            startingIndex++;
          }
          sortResult = true;
        } else {
          log.info("Sorting is not working fine");
          break;
        }
      }
    }
    return sortResult;
  }

  public void performEsc() {
    if (isAndroid()) {
      Actions actions = new Actions(Grid.driver());
      actions.sendKeys(Keys.ESCAPE).build().perform();
    }
  }

  public void changeDriverContextToWebView() {
    AppiumDriver<WebElement> driver = (AppiumDriver<WebElement>) Grid.driver();
    Set<String> contextHandles = driver.getContextHandles();
    contextHandles.stream().forEach(contex -> System.out.println(contex));

    // Set<String> contextHandles = Grid.driver().getContextHandles();
    for (String name : contextHandles) {
      if (name.equals("WEBVIEW")) driver.context(name);
    }
  }
}
