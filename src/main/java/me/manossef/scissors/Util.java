package me.manossef.scissors;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.Message;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util {

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

    public static void writeValue(JsonWriter out, JsonElement value) throws IOException {

        if(value == null || value.isJsonNull())
            out.nullValue();
        else if(value.isJsonPrimitive()) {

            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if(primitive.isNumber())
                out.value(primitive.getAsNumber());
            else if(primitive.isBoolean())
                out.value(primitive.getAsBoolean());
            else
                out.value(primitive.getAsString());

        } else if(value.isJsonArray()) {

            out.beginArray();
            for(JsonElement element : value.getAsJsonArray())
                writeValue(out, element);
            out.endArray();

        } else {

            if(!value.isJsonObject())
                throw new IllegalArgumentException("Couldn't write " + value.getClass());
            out.beginObject();
            for(Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {

                out.name(entry.getKey());
                writeValue(out, entry.getValue());

            }
            out.endObject();

        }

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

            Scissors.LOGGER.error("Failed to read the {} file.", fileName);
            return null;

        }

    }

    public static void saveJsonToFile(String fileName, Object object) {

        try {

            new File(SharedConstants.FILE_DIRECTORY).mkdirs();
            JsonWriter writer = new JsonWriter(new FileWriter(SharedConstants.FILE_DIRECTORY + fileName));
            writer.setIndent("  ");
            writeValue(writer, Scissors.GSON.toJsonTree(object));
            writer.close();

        } catch(IOException e) {

            Scissors.LOGGER.error("Failed to save the {} file.", fileName);

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

            Scissors.LOGGER.error("Failed to load the list of words from the {} file.", fileName, e);
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
        if(issue.id() == null) {

            StringBuilder builder = new StringBuilder();
            for(String error : issue.errorMessages()) builder.append(error).append(", ");
            for(String error : issue.errors().values()) builder.append(error).append(", ");
            Scissors.LOGGER.error("Failed to create issue: {}", builder.substring(0, builder.length() - 2));

        }

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
