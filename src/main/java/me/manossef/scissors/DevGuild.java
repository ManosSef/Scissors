package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class DevGuild {

    public static Guild getDevGuild() {

        Guild devGuild = Scissors.DISCORD_API.getGuildById(SharedConstants.DEV_GUILD_ID);
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

    public static void logMessage(String message, MessageChannel channel) {

        if(message.length() > 2000) {

            channel.sendMessage(message.substring(0, 1997) + "...").queue();
            System.out.println("Could not log entire commandMessage: " + message);
            return;

        }
        channel.sendMessage(message).queue();

    }

    public static MessageChannel getStatusLogChannel() {

        return getChannel(SharedConstants.STATUS_LOGS_CHANNEL_ID, "status-logs");

    }

    public static MessageChannel getCommandLogChannel() {

        return getChannel(SharedConstants.COMMAND_LOGS_CHANNEL_ID, "command-logs");

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
