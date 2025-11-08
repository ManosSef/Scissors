package me.manossef.scissors.games;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Game extends ListenerAdapter {

    private final User player1;
    private final User player2;
    private final MessageChannel channel;

    public Game(User player1, User player2, MessageChannel channel) {

        this.player1 = player1;
        this.player2 = player2;
        this.channel = channel;

    }

    public User getPlayer1() {

        return this.player1;

    }

    public User getPlayer2() {

        return this.player2;

    }

    public MessageChannel getChannel() {

        return this.channel;

    }

    public abstract boolean hasStarted();

    public abstract void start();

    public abstract void end();

}
