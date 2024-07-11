package page;

import common.constant.NavigationBarOption;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class NavigationBar extends BasePage {
    public NavigationBar(WebDriver driver) {
        super(driver);
    }

    public WebElement getNavigationOptionElement(NavigationBarOption navigationBarOption) {
        return getElement(
                By.xpath(
                        "//a[@class='nav-link'][contains(.,'" + navigationBarOption.getName() + "')]"
                )
        );
    }

    public void clickOnNavOption(NavigationBarOption navigationBarOption) {
        getNavigationOptionElement(navigationBarOption).click();
    }

    public String getGreetingMessage() {
        return getText(By.id("nameofuser"));
    }
}
