package me.manossef.scissors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.manossef.scissors.jira.JiraAPI;
import me.manossef.scissors.jira.JiraCheckLoop;
import me.manossef.scissors.listeners.CommandListener;
import me.manossef.scissors.listeners.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.util.ArrayList;

public class Scissors {

    public static final JDA DISCORD_API = JDABuilder.createDefault(SharedConstants.TOKEN)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES)
        .addEventListeners(new Startup(), new CommandListener())
        .build();
    public static final JiraAPI JIRA_API = new JiraAPI("https://manossef.atlassian.net/rest/api/2/");
    public static final Gson GSON = new Gson();

    public static void main(String[] args) {

        try {

            DISCORD_API.awaitReady();
            JiraCheckLoop.CheckedIssues checkedIssues = getCheckedIssues();
            if(checkedIssues == null)
                checkedIssues = new JiraCheckLoop.CheckedIssues(new ArrayList<>(), new ArrayList<>());
            System.out.println("Retrieved previous checked issues: " + checkedIssues);
            Thread jiraCheckLoop = new Thread(new JiraCheckLoop(checkedIssues));
            jiraCheckLoop.join();

        } catch(InterruptedException e) {

            System.err.println("The thread was interrupted!");

        }

    }

    private static JiraCheckLoop.CheckedIssues getCheckedIssues() {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(SharedConstants.CHECKED_ISSUES_FILE_NAME));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                builder.append(line).append("\n");
            reader.close();
            return GSON.fromJson(builder.toString(), JiraCheckLoop.CheckedIssues.class);

        } catch(IOException | JsonSyntaxException e) {

            System.err.println("Failed to read the checked_issues.json file.");
            return null;

        }

    }

    public static void saveCheckedIssues(JiraCheckLoop.CheckedIssues checkedIssues) {

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(SharedConstants.CHECKED_ISSUES_FILE_NAME));
            writer.write(GSON.toJson(checkedIssues));
            writer.close();

        } catch(IOException e) {

            System.err.println("Failed to save the checked_issues.json file.");

        }

    }

}