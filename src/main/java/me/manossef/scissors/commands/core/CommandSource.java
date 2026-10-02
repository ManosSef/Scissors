package me.manossef.scissors.commands.core;

import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface CommandSource {
    MessageChannelUnion channel();

    User user();

    void sendSuccess(String message, boolean feedback);

    void sendSuccess(MessageEmbed... embeds);

    void sendSuccess(String message, boolean feedback, MessageEmbed... embeds);

    void sendSuccess(String message, boolean feedback, MessageTopLevelComponent... components);

    void sendFailure(String message);

    void sendError(String message);

    default String getDetails() {
        return this.getClass().getName() + "[channel=" + this.channel().getJumpUrl() + ", user=" + this.user().getName() + " (" + this.user().getId() + ")]";
    }
}