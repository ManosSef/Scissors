package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;

public record ChatCommandSource(Message commandMessage, User user) {

    public ChatCommandSource withMessage(Message newMessage) {

        if(newMessage == this.commandMessage) return this;
        return new ChatCommandSource(newMessage, this.user);

    }

    public ChatCommandSource withUser(User newUser) {

        if(newUser == this.user) return this;
        return new ChatCommandSource(this.commandMessage, newUser);

    }

    public void sendSuccess(String message) {

        this.commandMessage.reply("✅ " + message).setAllowedMentions(Collections.emptyList()).queue();

    }

    public void sendFailure(String message) {

        this.commandMessage.reply("❌ " + message).setAllowedMentions(Collections.emptyList()).queue();

    }

    public void sendError(String message) {

        this.commandMessage.reply("\uD83D\uDC1E\uD83E\uDEB2\uD83D\uDD77 " + message).setAllowedMentions(Collections.emptyList()).queue();

    }

}
