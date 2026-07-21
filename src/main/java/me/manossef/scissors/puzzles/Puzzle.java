package me.manossef.scissors.puzzles;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Puzzle extends ListenerAdapter {
    private final MessageChannel channel;

    public Puzzle(MessageChannel channel) {
        this.channel = channel;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }

    public abstract void start();

    public abstract void end();
}