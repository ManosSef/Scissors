package me.manossef.scissors.listeners;

import me.manossef.scissors.DevGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Startup extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        DevGuild.logStatus("Hello, world! I sure hope no rocks get in my way today!");
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild guild = event.getGuild();
        DevGuild.logStatus("Joined server " + guild.getName() + " (ID: " + guild.getId() + ")");
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Guild guild = event.getGuild();
        DevGuild.logStatus("Left server " + guild.getName() + " (ID: " + guild.getId() + ")");
    }
}
