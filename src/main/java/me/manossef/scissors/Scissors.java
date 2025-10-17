package me.manossef.scissors;

import com.google.gson.Gson;
import me.manossef.scissors.jira.JiraAPI;
import me.manossef.scissors.listeners.CommandListener;
import me.manossef.scissors.listeners.Startup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Scissors {

    public static final JDA DISCORD_API = JDABuilder.createDefault(SharedConstants.TOKEN)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES)
        .addEventListeners(new Startup(), new CommandListener())
        .build();
    public static final JiraAPI JIRA_API = new JiraAPI("https://manossef.atlassian.net/rest/api/2/");
    public static final Gson GSON = new Gson();

    void main() {
    }

}