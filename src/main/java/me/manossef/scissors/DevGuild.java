package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class DevGuild {

    private static final long DEV_GUILD_ID = 1428446740855656542L;
    private static final long STATUS_LOGS_CHANNEL_ID = 1428451434373845114L;
    private static final long COMMAND_LOGS_CHANNEL_ID = 1428462041441501275L;
    private static final long DONE_ISSUES_CHANNEL_ID = 1429172420069298176L;
    private static final long INVALID_ISSUES_CHANNEL_ID = 1429172445067214988L;

    public static Guild getDevGuild() {

        Guild devGuild = Scissors.DISCORD_API.getGuildById(DEV_GUILD_ID);
        if(devGuild == null) {

            System.err.println("Could not find dev guild.");
            return null;

        }
        return devGuild;

    }

    public static void logStatus(String message) {

        logMessage(message, getStatusLogChannel());

    }

    public static void logCommand(String message) {

        logMessage(message, getCommandLogChannel());

    }

    public static void logDoneIssue(MessageCreateData message) {

        embedMessage(message, getDoneIssuesChannel());

    }

    public static void logInvalidIssue(MessageCreateData message) {

        embedMessage(message, getInvalidIssuesChannel());

    }

    public static void logMessage(String message, MessageChannel channel) {

        if(message.length() > 2000) {

            channel.sendMessage(message.substring(0, 1997) + "...").queue();
            System.out.println("Could not log entire commandMessage: " + message);
            return;

        }
        channel.sendMessage(message).queue();

    }

    public static void embedMessage(MessageCreateData message, MessageChannel channel) {

        channel.sendMessage(message).queue();

    }

    public static MessageChannel getStatusLogChannel() {

        return getChannel(STATUS_LOGS_CHANNEL_ID, "status-logs");

    }

    public static MessageChannel getCommandLogChannel() {

        return getChannel(COMMAND_LOGS_CHANNEL_ID, "command-logs");

    }

    public static MessageChannel getDoneIssuesChannel() {

        return getChannel(DONE_ISSUES_CHANNEL_ID, "done-issues");

    }

    public static MessageChannel getInvalidIssuesChannel() {

        return getChannel(INVALID_ISSUES_CHANNEL_ID, "invalid-issues");

    }

    private static MessageChannel getChannel(long id, String name) {

        Guild devGuild = getDevGuild();
        if(devGuild == null) return null;
        GuildChannel logsChannel = DevGuild.getDevGuild().getGuildChannelById(id);
        if(logsChannel == null) {

            System.err.println("Could not find #" + name + " in dev guild.");
            return null;

        }
        if(!(logsChannel instanceof MessageChannel messageChannel)) {

            System.err.println("Found #" + name + " in dev guild, but it isn't a commandMessage channel.");
            return null;

        }
        return messageChannel;

    }

}
