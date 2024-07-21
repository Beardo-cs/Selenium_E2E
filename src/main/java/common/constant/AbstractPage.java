//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package common.constant;

import com.qapitol.sauron.configuration.Config;
import com.qapitol.sauron.configuration.Config.ConfigProperty;
import com.qapitol.sauron.platform.html.support.HtmlElementUtils;
import com.qapitol.sauron.platform.utilities.FileAssistant;
import com.qapitol.sauron.platform.web.Page;
import com.qapitol.sauron.platform.web.PageFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

public abstract class AbstractPage {
    public static final String DEFAULT_LOCATOR_DIR = "locators";
    private String locatorDir;
    private String locatorFileName;
    private String locatorFilePath;
    private Locale locale;
    private boolean pageLoaded;
    private Page page;

    public AbstractPage() {
    }

    public void load() throws IOException {
        String filePath = this.getLocatorFilePath();
        InputStream locatorFile = FileAssistant.loadFile(filePath);
        this.page = PageFactory.getPage(locatorFile);
    }

    public String getLocator(String... key) {
        String locator = this.page.getLocator(key[0], Config.getConfigProperty(ConfigProperty.SITE_LOCALE), Config.getConfigProperty(ConfigProperty.TARGET_CLIENT));
        if (key.length > 1) {
            MessageFormat format = new MessageFormat(locator);
            locator = format.format(Arrays.copyOfRange(key, 1, key.length));
        }

        return this.formatIgnoreCase(locator);
    }

    public String formatIgnoreCase(String locator) {
        if (locator != null && locator.contains("containsIgnoreCase")) {
            String[] locatorSplt = locator.split("containsIgnoreCase");
            boolean first = true;
            String formated = "";
            String[] var5 = locatorSplt;
            int var6 = locatorSplt.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String str = var5[var7];
                if (first) {
                    first = false;
                    formated = str;
                } else {
                    formated = formated + "contains(translate(" + str.substring(str.indexOf("(") + 1, str.indexOf(",")) + ", 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')" + str.substring(str.indexOf(","), str.indexOf(")")).toLowerCase() + str.substring(str.indexOf(")"));
                }
            }

            return formated;
        } else {
            return locator;
        }
    }

    public String getLocator(String key, String client) {
        return this.page.getLocator(key, Config.getConfigProperty(ConfigProperty.SITE_LOCALE), client);
    }

    public boolean isElementPresent(String key) {
        return HtmlElementUtils.isElementPresent(this.getLocator(key));
    }

    public boolean isElementPresent(String key, String client) {
        return HtmlElementUtils.isElementPresent(this.getLocator(key, client));
    }

    public RemoteWebElement locateElement(String key) {
        return HtmlElementUtils.locateElement(this.getLocator(key));
    }

    public RemoteWebElement locateElement(String key, String client) {
        return HtmlElementUtils.locateElement(this.getLocator(key, client));
    }

    public List<WebElement> locateElements(String key) {
        return HtmlElementUtils.locateElements(this.getLocator(key));
    }

    public List<WebElement> locateElements(String key, String client) {
        return HtmlElementUtils.locateElements(this.getLocator(key, client));
    }

    protected String getLocatorFilePath() {
        return null != this.locatorFilePath ? this.locatorFilePath : this.getLocatorDir() + File.separator + this.getLocatorFileName();
    }

    protected String getLocatorDir() {
        return null != this.locatorDir ? this.locatorDir : "locators";
    }

    protected String getLocatorFileName() {
        return null == this.locatorFileName ? this.getClass().getSimpleName().toLowerCase() + ".yaml" : this.locatorFileName;
    }

    public void setLocatorFileName(String locatorFileName) {
        this.locatorFileName = locatorFileName;
    }

    public void setLocatorFilePath(String locatorFilePath) {
        this.locatorFilePath = locatorFilePath;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isPageLoaded() {
        return this.pageLoaded;
    }

    public void setPageLoaded(boolean pageLoaded) {
        this.pageLoaded = pageLoaded;
    }

    public Page getPage() {
        return this.page;
    }
}
