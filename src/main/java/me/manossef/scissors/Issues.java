package me.manossef.scissors;

import me.manossef.scissors.jira.objects.Comment;
import me.manossef.scissors.jira.objects.Issue;
import me.manossef.scissors.jira.objects.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Issues {
    private static final Logger LOGGER = LoggerFactory.getLogger(Issues.class);

    public static void createForException(Throwable exception, String summaryPrefix, String description) {
        String summary = summaryPrefix + exception.getClass().getName();
        if(exception.getMessage() != null) summary += ": " + exception.getMessage();
        if(Environment.IS_STAGING && !Environment.DEBUG_ALWAYS_CREATE_ISSUES) {
            LOGGER.info("Not reporting exception in staging, summary would be: {}", summary);
            return;
        }
        String stackTrace = "{noformat}" + getStackTrace(exception) + "{noformat}";
        SearchResults searchResults = Scissors.JIRA_API.searchIssues("project = SCIS AND (resolution = Unresolved OR cf[10420] IS NOT EMPTY) AND summary ~ \"\\\"" + summary + "\\\"\" ORDER BY created ASC", "id,key,customfield_10419");
        if(searchResults == null) LOGGER.error("Failed to search issues; not logged in or Jira is down");
        else {
            Issue[] issues = searchResults.issues();
            for(Issue issue : issues) {
                String otherStackTrace = issue.fields().stackTrace();
                if(otherStackTrace == null) continue;
                if(otherStackTrace.equals(stackTrace)) {
                    Comment comment = Scissors.JIRA_API.addComment(issue.key(), description);
                    if(comment == null) LOGGER.error("Failed to comment on {}; not logged in or Jira is down", issue.key());
                    else if(comment.body() == null) {
                        StringBuilder builder = new StringBuilder();
                        for(String error : comment.errorMessages()) builder.append(error).append(", ");
                        for(String error : comment.errors().values()) builder.append(error).append(", ");
                        LOGGER.error("Failed to comment on {}: {}", issue.key(), builder.substring(0, builder.length() - 2));
                    } else {
                        LOGGER.info("Added comment on {}", issue.key());
                        return;
                    }
                }
            }
        }
        Issue issue = Scissors.JIRA_API.createIssue(
            summary,
            description,
            Issue.Fields.Issuetype.BUG,
            Issue.Fields.Project.SCIS,
            Scissors.DISCORD_API.getSelfUser().getId(),
            null,
            null,
            stackTrace
        );
        if(issue == null) LOGGER.error("Failed to create issue; not logged in or Jira is down");
        else if(issue.id() == null) {
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