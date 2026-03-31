package me.manossef.scissors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.manossef.scissors.config.*;
import me.manossef.scissors.jira.JiraAPI;
import me.manossef.scissors.jira.JiraCheckLoop;
import me.manossef.scissors.listeners.CommandListener;
import me.manossef.scissors.listeners.MessageListeners;
import me.manossef.scissors.listeners.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Scissors {

    public static final JDA DISCORD_API = JDABuilder.createDefault(SharedConstants.TOKEN)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES)
        .addEventListeners(new Startup(), new CommandListener(), new MessageListeners())
        .build();
    public static final JiraAPI JIRA_API = new JiraAPI("https://manossef.atlassian.net/rest/api/2/");
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Option.class, new OptionAdapter()).registerTypeAdapterFactory(new OptionValueAdapterFactory()).create();
    public static final Random RANDOM = new Random();
    public static final Logger LOGGER = LoggerFactory.getLogger(Scissors.class);

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
            if(SharedConstants.IS_STAGING) LOGGER.info("Retrieved previous checked issues: {}", checkedIssues);
            Thread jiraCheckLoop = new Thread(new JiraCheckLoop(checkedIssues));
            jiraCheckLoop.start();

        } catch(InterruptedException e) {

            LOGGER.error("The thread was interrupted!");

        }

    }

    public static Configuration getConfiguration() {

        return config;

    }

    private static Configuration getConfigFromFile() {

        return Util.getJsonFromFile(SharedConstants.CONFIG_FILE_NAME, Configuration.class);

    }

    private static JiraCheckLoop.CheckedIssues getCheckedIssues() {

        return Util.getJsonFromFile(SharedConstants.CHECKED_ISSUES_FILE_NAME, JiraCheckLoop.CheckedIssues.class);

    }

    public static void saveConfiguration() {

        Util.saveJsonToFile(SharedConstants.CONFIG_FILE_NAME, config);

    }

    public static void saveCheckedIssues(JiraCheckLoop.CheckedIssues checkedIssues) {

        Util.saveJsonToFile(SharedConstants.CHECKED_ISSUES_FILE_NAME, checkedIssues);

    }

}