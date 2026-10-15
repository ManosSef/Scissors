package me.manossef.scissors.commands.core;

import me.manossef.scissors.Emojis;
import me.manossef.scissors.Messages;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public record SlashCommandSource(SlashCommandInteractionEvent event) implements CommandSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandSource.class);

    @Override
    public MessageChannelUnion channel() {
        return this.event.getChannel();
    }

    @Override
    public User user() {
        return this.event.getUser();
    }

    @Override
    public void sendSuccess(String message, boolean feedback) {
        this.event.reply(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(MessageEmbed... embeds) {
        this.event.reply(new MessageCreateBuilder().setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(String message, boolean feedback, MessageEmbed... embeds) {
        this.event.reply(new MessageCreateBuilder().setContent(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .setEmbeds(embeds).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendSuccess(String message, boolean feedback, MessageTopLevelComponent... components) {
        this.event.reply(new MessageCreateBuilder().setContent(Messages.truncateCommandResponse((feedback ? Emojis.WHITE_HEAVY_CHECK_MARK.getFormatted() + " " : "") + message, LOGGER))
            .addComponents(components).build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendFailure(String message) {
        this.event.reply(Messages.truncateCommandResponse(Emojis.CROSS_MARK.getFormatted() + " " + message, LOGGER)).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendError(String message) {
        this.event.reply(Messages.truncateCommandResponse(Emojis.LADY_BEETLE.getFormatted() + Emojis.BEETLE.getFormatted() + Emojis.SPIDER.getFormatted() + " " + message, LOGGER))
            .setAllowedMentions(Collections.emptyList()).queue();
    }
}