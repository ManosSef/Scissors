package me.manossef.scissors;

import com.google.gson.JsonSyntaxException;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static String getMessageLink(Message message) {

        String guildId = message.getGuildId();
        return "https://discord.com/channels/" + (guildId == null ? "@me" : guildId) + "/" + message.getChannelId() + "/" + message.getId();

    }

    public static String truncate(String message) {

        if(message.length() <= Message.MAX_CONTENT_LENGTH)
            return message;
        if(message.endsWith("```"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
        else if(message.endsWith("`"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 4) + "...`";
        else
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "...";

    }

    public static <T> T getJsonFromFile(String fileName, Class<T> type) {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(SharedConstants.FILE_DIRECTORY + fileName));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            reader.close();
            return Scissors.GSON.fromJson(builder.toString(), type);

        } catch(IOException | JsonSyntaxException e) {

            LOGGER.error("Failed to read the {} file.", fileName);
            return null;

        }

    }

    public static void saveJsonToFile(String fileName, Object object) {

        try {

            boolean ignored = new File(SharedConstants.FILE_DIRECTORY).mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(SharedConstants.FILE_DIRECTORY + fileName));
            writer.write(Scissors.GSON.toJson(object));
            writer.close();

        } catch(IOException e) {

            LOGGER.error("Failed to save the {} file.", fileName);

        }

    }

    public static void loadWords(String fileName, List<String> list, String logError) {

        try {

            list.clear();
            Reader fileReader = SharedConstants.IS_STAGING ? new FileReader("staging_resources/" + fileName) : new InputStreamReader(Objects.requireNonNull(Scissors.class.getResourceAsStream("/" + fileName)));
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while((line = reader.readLine()) != null) list.add(line.toLowerCase());
            reader.close();

        } catch(IOException e) {

            LOGGER.error("Failed to load the list of words from the {} file.", fileName, e);
            DevGuild.logStatus(logError);

        }

    }

    public static void createIssueForException(Throwable exception) {

        createIssueForException(exception, "", "");

    }

    public static void createIssueForException(Throwable exception, String summaryPrefix, String description) {

        Issue issue = Scissors.JIRA_API.createIssue(
            summaryPrefix + exception.getClass().getName() + ": " + exception.getMessage(),
            description + "\nStack trace:\n{noformat}" + getStackTrace(exception) + "{noformat}",
            Scissors.JIRA_API.getIssuetype(SharedConstants.ISSUETYPE_BUG_ID),
            Scissors.JIRA_API.getProject(SharedConstants.PROJECT_SCIS_ID),
            Scissors.DISCORD_API.getSelfUser().getId()
        );
        if(SharedConstants.IS_STAGING) {

            LOGGER.info("Issue that would be created: {}", issue);
            return;

        }
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

    public static boolean isLong(String input) {

        try {

            Long.parseLong(input);
            return true;

        } catch(NumberFormatException e) {

            return false;

        }

    }

}
