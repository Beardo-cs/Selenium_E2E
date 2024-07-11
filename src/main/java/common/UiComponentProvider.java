package common;

import org.openqa.selenium.WebDriver;
import page.BasePage;
import page.uicomponent.AlertComponent;


public class UiComponentProvider extends BasePage {
    public UiComponentProvider(WebDriver driver) {
        super(driver);
    }

    public AlertComponent getAlertComponent() {
        return new AlertComponent(driver);
    }
}
