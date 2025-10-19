package me.manossef.scissors.listeners;

import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Startup extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        Scissors.isReady = true;
        DevGuild.logStatus("Hello, world! I sure hope no rocks get in my way today!");

    }

}
