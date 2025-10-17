package me.manossef.scissors.listeners;

import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Startup extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        Guild devGuild = Scissors.DISCORD_API.getGuildById(SharedConstants.DEV_GUILD_ID);
        if(devGuild == null) {

            System.err.println("Could not find dev guild.");
            return;

        }
        GuildChannel logsChannel = devGuild.getGuildChannelById(SharedConstants.STATUS_LOGS_CHANNEL_ID);
        if(logsChannel == null) {

            System.err.println("Could not find #status-logs in dev guild.");
            return;

        }
        if(logsChannel instanceof MessageChannel messageChannel)
            messageChannel.sendMessage("Hello, world! I sure hope no rocks get in my way today!").queue();

    }

}
