package me.manossef.scissors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Settings;
import me.manossef.scissors.config.SettingsAdapter;
import me.manossef.scissors.jira.JiraAPI;
import me.manossef.scissors.jira.JiraCheckLoop;
import me.manossef.scissors.listeners.CommandListener;
import me.manossef.scissors.listeners.MessageListeners;
import me.manossef.scissors.listeners.SecretListener;
import me.manossef.scissors.listeners.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.random.RandomGenerator;

public class Scissors {
    public static final JDA DISCORD_API = JDABuilder.createDefault(System.getenv("SCISSORS_BOT_TOKEN"))
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES)
        .addEventListeners(new Startup(), new CommandListener(), new MessageListeners(), new SecretListener())
        .build();
    public static final JiraAPI JIRA_API = new JiraAPI("https://manossef.atlassian.net/rest/api/2/");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Settings.class, new SettingsAdapter()).setStrictness(Strictness.STRICT).create();
    public static final RandomGenerator RANDOM = RandomGenerator.of("L64X128MixRandom");

    private static final Logger LOGGER = LoggerFactory.getLogger(Scissors.class);
    private static final String CHECKED_ISSUES_FILE_NAME = "checked_issues.json";
    private static final String CONFIG_FILE_NAME = "config.json";

    private static Configuration config;
    private static JiraCheckLoop jcl;

    public static void main(String[] args) {
        try {
            DISCORD_API.awaitReady();
            Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                LOGGER.error("Uncaught exception thrown!", exception);
                Util.createIssueForException(exception);
            });
            config = getConfigFromFile();
            if(config == null) config = new Configuration();
            saveConfiguration();
            JiraCheckLoop.CheckedIssues checkedIssues = getCheckedIssues();
            if(checkedIssues == null)
                checkedIssues = new JiraCheckLoop.CheckedIssues(new ArrayList<>(), new ArrayList<>());
            LOGGER.debug("Retrieved previous checked issues: {}", checkedIssues);
            startJiraCheckLoop(checkedIssues);
        } catch(InterruptedException e) {
            LOGGER.warn("The thread was interrupted!");
        }
    }

    public static Configuration getConfiguration() {
        return config;
    }

    private static Configuration getConfigFromFile() {
        return Util.getJsonFromFile(CONFIG_FILE_NAME, Configuration.class);
    }

    private static JiraCheckLoop.CheckedIssues getCheckedIssues() {
        return Util.getJsonFromFile(CHECKED_ISSUES_FILE_NAME, JiraCheckLoop.CheckedIssues.class);
    }

    public static JiraCheckLoop getJiraCheckLoop() {
        return jcl;
    }

    public static void saveConfiguration() {
        Util.saveJsonToFile(CONFIG_FILE_NAME, config);
    }

    public static void saveCheckedIssues(JiraCheckLoop.CheckedIssues checkedIssues) {
        Util.saveJsonToFile(CHECKED_ISSUES_FILE_NAME, checkedIssues);
    }

    public static void startJiraCheckLoop() {
        jcl = new JiraCheckLoop(getCheckedIssues());
        jcl.start();
    }

    public static void startJiraCheckLoop(JiraCheckLoop.CheckedIssues checkedIssues) {
        jcl = new JiraCheckLoop(checkedIssues);
        jcl.start();
    }
}