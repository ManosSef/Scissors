package me.manossef.scissors;

public class SharedConstants {

    public static final String TOKEN = System.getenv("SCISSORS_BOT_TOKEN");
    public static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    public static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");

    public static final long MY_USER_ID = 611151083141857286L;
    public static final long DEV_GUILD_ID = 1428446740855656542L;
    public static final long STATUS_LOGS_CHANNEL_ID = 1428451434373845114L;
    public static final long COMMAND_LOGS_CHANNEL_ID = 1428462041441501275L;

    public static final String COMMAND_PREFIX = ">";

    public static final String ISSUETYPE_BUG_ID = "10009";
    public static final String ISSUETYPE_FEATURE_ID = "10048";
    public static final String ISSUETYPE_IMPROVEMENT_ID = "10049";

    public static final String PROJECT_SCIS_ID = "10039";

}
