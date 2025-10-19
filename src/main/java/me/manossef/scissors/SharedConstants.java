package me.manossef.scissors;

public class SharedConstants {

    public static final String TOKEN = System.getenv("SCISSORS_BOT_TOKEN");
    public static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    public static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");

    public static final long MY_USER_ID = 611151083141857286L;

    public static final String COMMAND_PREFIX = "8<";

    public static final String ISSUETYPE_BUG_ID = "10009";
    public static final String ISSUETYPE_FEATURE_ID = "10048";
    public static final String ISSUETYPE_IMPROVEMENT_ID = "10049";

    public static final String PROJECT_SCIS_ID = "10039";

    public static final String CHECKED_ISSUES_FILE_NAME = "/storage/checked_issues.json";

}
