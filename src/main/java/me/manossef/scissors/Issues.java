package me.manossef.scissors;

import me.manossef.scissors.jira.objects.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Issues {
    private static final Logger LOGGER = LoggerFactory.getLogger(Issues.class);

    public static void createForException(Throwable exception) {
        createForException(exception, "", "");
    }

    public static void createForException(Throwable exception, String summaryPrefix, String description) {
        String summary = summaryPrefix + exception.getClass().getName();
        if(exception.getMessage() != null) summary += ": " + exception.getMessage();
        if(Environment.IS_STAGING && !Environment.DEBUG_ALWAYS_CREATE_ISSUES) {
            LOGGER.info("Not creating issue in staging, summary would be: {}", summary);
            return;
        }
        Issue issue = Scissors.JIRA_API.createIssue(
            summary,
            description + "\nStack trace:\n{noformat}" + getStackTrace(exception) + "{noformat}",
            Issue.Fields.Issuetype.BUG,
            Issue.Fields.Project.SCIS,
            Scissors.DISCORD_API.getSelfUser().getId(),
            null,
            null
        );
        if(issue.id() == null) {
            StringBuilder builder = new StringBuilder();
            for(String error : issue.errorMessages()) builder.append(error).append(", ");
            for(String error : issue.errors().values()) builder.append(error).append(", ");
            LOGGER.error("Failed to create issue: {}", builder.substring(0, builder.length() - 2));
        } else LOGGER.info("Created issue {}", issue.key());
    }

    public static String getStackTrace(Throwable exception) {
        StringBuilder stackTrace = new StringBuilder();
        for(StackTraceElement element : exception.getStackTrace())
            stackTrace.append("\t").append("at ").append(element.toString()).append("\n");
        return stackTrace.toString();
    }
}