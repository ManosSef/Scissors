package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

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

        this.commandMessage.reply(truncate("✅ " + message)).setAllowedMentions(Collections.emptyList()).queue();

    }

    public void sendSuccess(String message, MessageEmbed... embeds) {

        this.commandMessage.reply(new MessageCreateBuilder().setContent(truncate("✅ " + message)).setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();

    }

    public void sendFailure(String message) {

        this.commandMessage.reply(truncate("❌ " + message)).setAllowedMentions(Collections.emptyList()).queue();

    }

    public void sendError(String message) {

        this.commandMessage.reply(truncate("\uD83D\uDC1E\uD83E\uDEB2\uD83D\uDD77 " + message)).setAllowedMentions(Collections.emptyList()).queue();

    }

    private String truncate(String content) {

        if(content.length() > 2000) {

            if(SharedConstants.IS_STAGING) Scissors.LOGGER.warn("Could not send entire command response: {}", content);
            return Util.truncate(content);

        }
        return content;

    }

}
