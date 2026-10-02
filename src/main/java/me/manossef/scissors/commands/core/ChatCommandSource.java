package me.manossef.scissors.commands.core;

import me.manossef.scissors.Emojis;
import me.manossef.scissors.Messages;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public record ChatCommandSource(Message commandMessage, User user) implements CommandSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatCommandSource.class);

    public static ChatCommandSource of(Message message) {
        return new ChatCommandSource(message, message.getAuthor());
    }

    @Override
    public MessageChannelUnion channel() {
        return this.commandMessage.getChannel();
    }

    @Override
    public void sendSuccess(String message, boolean feedback) {
        this.commandMessage.reply(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(MessageEmbed... embeds) {
        this.commandMessage.reply(new MessageCreateBuilder().setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(String message, boolean feedback, MessageEmbed... embeds) {
        this.commandMessage.reply(new MessageCreateBuilder().setContent(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(String message, boolean feedback, MessageTopLevelComponent... components) {
        this.commandMessage.reply(new MessageCreateBuilder().setContent(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .addComponents(components).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendFailure(String message) {
        this.commandMessage.reply(Messages.truncateCommandResponse(Emojis.CROSS_MARK.getFormatted() + " " + message, LOGGER)).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendError(String message) {
        this.commandMessage.reply(Messages.truncateCommandResponse(Emojis.LADY_BEETLE.getFormatted() + Emojis.BEETLE.getFormatted() + Emojis.SPIDER.getFormatted() + " " + message, LOGGER))
            .setAllowedMentions(Collections.emptyList()).queue();
    }
}