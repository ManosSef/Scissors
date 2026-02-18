package me.manossef.scissors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Settings;
import me.manossef.scissors.jira.JiraAPI;
import me.manossef.scissors.jira.JiraCheckLoop;
import me.manossef.scissors.listeners.CommandListener;
import me.manossef.scissors.listeners.MessageListeners;
import me.manossef.scissors.listeners.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Scissors {

    public static final JDA DISCORD_API = JDABuilder.createDefault(SharedConstants.TOKEN)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES)
        .addEventListeners(new Startup(), new CommandListener(), new MessageListeners())
        .build();
    public static final JiraAPI JIRA_API = new JiraAPI("https://manossef.atlassian.net/rest/api/2/");
    public static final Gson GSON = new Gson();
    public static final Random RANDOM = new Random();

    private static Configuration config;

    public static void main(String[] args) {

        try {

            DISCORD_API.awaitReady();
            config = getConfigFromFile();
            if(config == null)
                config = new Configuration(new Settings(), new HashMap<>(), new HashMap<>());
            saveConfiguration();
            JiraCheckLoop.CheckedIssues checkedIssues = getCheckedIssues();
            if(checkedIssues == null)
                checkedIssues = new JiraCheckLoop.CheckedIssues(new ArrayList<>(), new ArrayList<>());
            if(SharedConstants.IS_STAGING) System.out.println("Retrieved previous checked issues: " + checkedIssues);
            Thread jiraCheckLoop = new Thread(new JiraCheckLoop(checkedIssues));
            jiraCheckLoop.start();

        } catch(InterruptedException e) {

            System.err.println("The thread was interrupted!");

        }

    }

    public static Configuration getConfiguration() {

        return config;

    }

    private static Configuration getConfigFromFile() {

        return getJsonFromFile(SharedConstants.CONFIG_FILE_NAME, Configuration.class);

    }

    private static JiraCheckLoop.CheckedIssues getCheckedIssues() {

        return getJsonFromFile(SharedConstants.CHECKED_ISSUES_FILE_NAME, JiraCheckLoop.CheckedIssues.class);

    }

    private static <T> T getJsonFromFile(String fileName, Class<T> type) {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(SharedConstants.FILE_DIRECTORY + fileName));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            reader.close();
            return GSON.fromJson(builder.toString(), type);

        } catch(IOException | JsonSyntaxException e) {

            System.err.println("Failed to read the " + fileName + " file.");
            return null;

        }

    }

    public static void saveConfiguration() {

        saveJsonToFile(SharedConstants.CONFIG_FILE_NAME, config);

    }

    public static void saveCheckedIssues(JiraCheckLoop.CheckedIssues checkedIssues) {

        saveJsonToFile(SharedConstants.CHECKED_ISSUES_FILE_NAME, checkedIssues);

    }

    private static void saveJsonToFile(String fileName, Object object) {

        try {

            new File(SharedConstants.FILE_DIRECTORY).mkdirs();
            JsonWriter writer = new JsonWriter(new FileWriter(SharedConstants.FILE_DIRECTORY + fileName));
            writer.setIndent("  ");
            writeValue(writer, GSON.toJsonTree(object));
            writer.close();

        } catch(IOException e) {

            System.err.println("Failed to save the " + fileName + " file.");

        }

    }

    private static void writeValue(JsonWriter out, JsonElement value) throws IOException {

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

}