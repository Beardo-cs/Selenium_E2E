package common.constant;

import static util.Reader.getEnvironmentConfig;

public class ApplicationConstants {
    public static final String APPLICATION_URL = getEnvironmentConfig("application_url");
    public static final String LOGIN_USERNAME = getEnvironmentConfig("username");
    public static final String LOGIN_PASSWORD = getEnvironmentConfig("password");
}
