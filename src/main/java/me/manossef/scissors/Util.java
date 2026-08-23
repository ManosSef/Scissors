package me.manossef.scissors;

import com.google.gson.JsonSyntaxException;
import me.manossef.scissors.config.InvalidConfigurationException;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static String getMessageLinkWithInfo(Message message) {
        return message.getJumpUrl() + " (channel: " + message.getChannelId() + ", message: " + message.getId() + ")";
    }

    public static String truncate(String message) {
        if(message.length() <= Message.MAX_CONTENT_LENGTH)
            return message;
        if(message.endsWith("```"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
        else if(message.endsWith("``"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 5) + "...``";
        else if(message.endsWith("`"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 4) + "...`";
        else
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "...";
    }

    public static <T> T getJsonFromFile(String fileName, Class<T> type) {
        String json = "";
        try {
            json = Files.readString(Path.of(SharedConstants.FILE_DIRECTORY, fileName));
            return Scissors.GSON.fromJson(json, type);
        } catch(IOException e) {
            LOGGER.error("Failed to read the {} file.", fileName);
            return null;
        } catch(JsonSyntaxException e) {
            LOGGER.error("Invalid JSON in {}:\n{}", fileName, json, e);
            return null;
        } catch(InvalidConfigurationException e) {
            LOGGER.error("Invalid configuration:\n{}", json, e);
            return null;
        }
    }

    public static void saveJsonToFile(String fileName, Object object) {
        try {
            Files.writeString(Path.of(SharedConstants.FILE_DIRECTORY, fileName), Scissors.GSON.toJson(object));
        } catch(IOException e) {
            LOGGER.error("Failed to save the {} file.", fileName);
        }
    }

    public static void loadWords(String fileName, List<String> list, String logError) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(Scissors.class.getResourceAsStream("/" + fileName))))) {
            list.clear();
            list.addAll(reader.lines().toList());
        } catch(IOException e) {
            LOGGER.error("Failed to load the list of words from the {} file.", fileName, e);
            DevGuild.logStatus(logError);
        }
    }

    public static void createIssueForException(Throwable exception) {
        createIssueForException(exception, "", "");
    }

    public static void createIssueForException(Throwable exception, String summaryPrefix, String description) {
        String summary = summaryPrefix + exception.getClass().getName();
        if(exception.getMessage() != null) summary += ": " + exception.getMessage();
        if(SharedConstants.IS_STAGING) {
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