package me.manossef.scissors.listeners;

import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collections;
import java.util.List;

public class MessageListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        MessageChannelUnion channel = message.getChannel();
        Configuration config = Scissors.getConfiguration();
        if(content.matches("^[0-9]+$") && config.getOptionForChannel(Options.GPPCT_RESPONSES, Boolean.class, channel)) {

            boolean isDisallowedChannel = channel.getName().toLowerCase().contains("counting") || channel.getName().toLowerCase().contains("spam") || (message.getCategory() != null && message.getCategory().getName().toLowerCase().contains("counting"));
            Boolean optionForChannelOnly = config.getOptionForChannelOnly(Options.GPPCT_RESPONSES, Boolean.class, channel);
            boolean isChannelExplicitlyAllowed = optionForChannelOnly != null && optionForChannelOnly;
            if(channel.canTalk() && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Options.GPPCT_RESPONSE_CHANCE, Integer.class, channel)
                && (!isDisallowedChannel || isChannelExplicitlyAllowed))
                replyWithRandomMessage(message, content.equals("67") ? Messages.GPPCT_BRAINROT_RESPONSES : Messages.GPPCT_RESPONSES, "GPPCT");

        } else if(content.contains(Scissors.DISCORD_API.getSelfUser().getAsMention()) && config.getOptionForChannel(Options.PING_RESPONSES, Boolean.class, channel))
            replyWithRandomMessage(message, Messages.PING_RESPONSES, "ping");
        else if(content.toLowerCase().contains("scissors") && config.getOptionForChannel(Options.SCISSORS_RESPONSES, Boolean.class, channel)) {

            if(channel.canTalk() && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Options.SCISSORS_RESPONSE_CHANCE, Integer.class, channel))
                replyWithRandomMessage(message, Messages.SCISSORS_RESPONSES, "scissors");

        }
        if(content.toLowerCase().contains("paper") && config.getOptionForChannel(Options.REACT_TO_PAPER, Boolean.class, channel))
            message.addReaction(Emoji.fromUnicode("✂️")).onErrorMap(e -> null).queue();

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        if(event.getMessageAuthorIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(!event.getEmoji().getName().equals("\uD83D\uDDD1\uFE0F")) return;
        if(event.getUserIdLong() != SharedConstants.MY_USER_ID) return;
        event.retrieveMessage().onSuccess(message -> message.delete().queue()).queue();

    }

    private void replyWithRandomMessage(Message message, List<String> possibleMessages, String responseType) {

        message.getChannel().sendMessage(possibleMessages.get(Scissors.RANDOM.nextInt(possibleMessages.size())).formatted(message.getAuthor().getAsMention()))
            .setMessageReference(message)
            .mentionRepliedUser(false)
            .setAllowedMentions(Collections.emptyList())
            .queue();
        DevGuild.logResponse("Posted a " + responseType + " response to https://discord.com/channels/" + message.getGuildId() + "/" + message.getChannelId() + "/" + message.getId());

    }

}
