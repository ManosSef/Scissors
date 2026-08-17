package me.manossef.scissors;

public class SharedConstants {
    private static final String ENVIRONMENT = System.getenv("SCISSORS_ENVIRONMENT");

    static {
        if(!("staging".equalsIgnoreCase(ENVIRONMENT) || "production".equalsIgnoreCase(ENVIRONMENT)))
            throw new IllegalStateException("Invalid environment");
    }

    public static final boolean IS_STAGING = "staging".equalsIgnoreCase(ENVIRONMENT);

    public static final String TOKEN = System.getenv("SCISSORS_BOT_TOKEN");
    public static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    public static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");
    public static final String LOG_WEBHOOK_URL = System.getenv("SCISSORS_LOGGER_WEBHOOK");

    public static final long MY_USER_ID = 611151083141857286L;
    public static final String MY_MENTION = "<@" + MY_USER_ID + ">";

    public static final String COMMAND_PREFIX = "8<";

    public static final String FILE_DIRECTORY = "storage";
    public static final String CHECKED_ISSUES_FILE_NAME = "checked_issues.json";
    public static final String CONFIG_FILE_NAME = "config.json";
}