package common.constant;


public enum NavigationBarOption {
    HOME("Home"),
    CONTACT("Contact"),
    ABOUT_US("About us"),
    CART("Cart"),
    LOG_IN("Log in"),
    SIGN_UP("Sign up");

    private final String navBarItemName;

    NavigationBarOption(String navBarItemName) {
        this.navBarItemName = navBarItemName;
    }

    public String getName() {
        return navBarItemName;
    }
}
