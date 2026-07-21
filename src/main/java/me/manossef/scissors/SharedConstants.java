package me.manossef.scissors;

public class SharedConstants {
    public static final boolean IS_STAGING = "staging".equalsIgnoreCase(System.getenv("SCISSORS_ENVIRONMENT"));

    public static final String TOKEN = System.getenv("SCISSORS_BOT_TOKEN");
    public static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    public static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");
    public static final String LOG_WEBHOOK_URL = System.getenv("SCISSORS_LOGGER_WEBHOOK");

    public static final long MY_USER_ID = 611151083141857286L;

    public static final String COMMAND_PREFIX = "8<";

    public static final String ISSUETYPE_BUG_ID = "10009";
    public static final String ISSUETYPE_FEATURE_ID = "10048";
    public static final String ISSUETYPE_IMPROVEMENT_ID = "10049";
    public static final String ISSUETYPE_TASK_ID = "10147";

    public static final String PROJECT_SCIS_ID = "10039";

    public static final String FILE_DIRECTORY = IS_STAGING ? "storage/" : "/storage/";
    public static final String CHECKED_ISSUES_FILE_NAME = "checked_issues.json";
    public static final String CONFIG_FILE_NAME = "config.json";
}