package common;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import page.BasePage;
import page.LoginPage;
import page.NavigationBar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class PageProvider extends BasePage {
    public PageProvider(WebDriver driver) {
        super(driver);
    }



    public NavigationBar getNavigationBar() {
        return new NavigationBar(driver);
    }

    public LoginPage getLoginPage() {
        return new LoginPage(driver);
    }
}
