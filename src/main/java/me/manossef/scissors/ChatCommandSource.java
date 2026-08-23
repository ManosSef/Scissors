package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public record ChatCommandSource(Message commandMessage, User user) {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatCommandSource.class);

    public ChatCommandSource withMessage(Message newMessage) {
        if(newMessage == this.commandMessage) return this;
        return new ChatCommandSource(newMessage, this.user);
    }

    public ChatCommandSource withUser(User newUser) {
        if(newUser == this.user) return this;
        return new ChatCommandSource(this.commandMessage, newUser);
    }

    public void sendSuccess(String message, boolean feedback) {
        this.commandMessage.reply(truncate((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message))
                .setAllowedMentions(Collections.emptyList()).queue();
    }

    public void sendSuccess(String message, boolean feedback, MessageEmbed... embeds) {
        this.commandMessage.reply(new MessageCreateBuilder().setContent(truncate((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message))
                .setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    public void sendFailure(String message) {
        this.commandMessage.reply(truncate(Emojis.CROSS_MARK.getFormatted() + " " + message)).setAllowedMentions(Collections.emptyList()).queue();
    }

    public void sendError(String message) {
        this.commandMessage.reply(truncate(Emojis.LADY_BEETLE.getFormatted() + Emojis.BEETLE.getFormatted() + Emojis.SPIDER.getFormatted() + " " + message)).setAllowedMentions(Collections.emptyList()).queue();
    }

    private String truncate(String content) {
        if(content.length() > Message.MAX_CONTENT_LENGTH) {
            LOGGER.warn("Tried to send too long a command response; truncating: {}", content);
            return Messages.truncate(content);
        }
        return content;
    }
}