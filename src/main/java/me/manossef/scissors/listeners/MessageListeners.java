package me.manossef.scissors.listeners;

import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.Util;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Option;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collections;
import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.strike;

public class MessageListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        if(content.startsWith(SharedConstants.COMMAND_PREFIX)) return;
        Configuration config = Scissors.getConfiguration();
        if(this.promptsGPPCT(message, config))
            replyWithRandomMessage(message, content.equals("67") ? Messages.GPPCT_BRAINROT_RESPONSES : Messages.GPPCT_RESPONSES, "GPPCT");
        else if(this.promptsPing(message, config))
            replyWithRandomMessage(message, Messages.PING_RESPONSES, "ping");
        else if(this.promptsMeme(message, config))
            replyWithRandomMessage(message, Messages.MEME_RESPONSES, "meme");
        else if(this.promptsScissors(message, config))
            replyWithRandomMessage(message, Messages.SCISSORS_RESPONSES, "scissors");
        if(message.getContentRaw().toLowerCase().contains("paper") && config.getOptionForChannel(Option.REACT_TO_PAPER, Boolean.class, message.getChannel()))
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
        DevGuild.logResponse("Posted a " + responseType + " response to " + Util.getMessageLink(message));

    }

    private boolean promptsGPPCT(Message message, Configuration config) {

        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().matches("^[0-9]+$")
            && channel.canTalk()
            && config.getOptionForChannel(Option.GPPCT_RESPONSES, Boolean.class, channel)
            && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Option.GPPCT_RESPONSE_CHANCE, Integer.class, channel);

    }

    private boolean promptsPing(Message message, Configuration config) {

        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().contains(Scissors.DISCORD_API.getSelfUser().getAsMention())
            && channel.canTalk()
            && config.getOptionForChannel(Option.PING_RESPONSES, Boolean.class, channel);

    }

    private boolean promptsScissors(Message message, Configuration config) {

        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().toLowerCase().contains("scissors")
            && channel.canTalk()
            && config.getOptionForChannel(Option.SCISSORS_RESPONSES, Boolean.class, channel)
            && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Option.SCISSORS_RESPONSE_CHANCE, Integer.class, channel);

    }

    private boolean promptsMeme(Message message, Configuration config) {

        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return false;
        MessageChannelUnion channel = message.getChannel();
        return referencedMessage.getAuthor().getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong()
            && referencedMessage.getContentRaw().equals("A number?! At this time of year? At this time of day? In this part of the country? " + strike("Localized entirely within your kitchen?!"))
            && referencedMessage.getReferencedMessage() != null
            && message.getContentRaw().toLowerCase().replaceAll("[^a-z]+", "").equals("yes")
            && channel.canTalk()
            && config.getOptionForChannel(Option.GPPCT_RESPONSES, Boolean.class, channel);

    }

    public static boolean isDisallowedForGPPCT(Channel channel) {

        return channel.getName().toLowerCase().contains("counting") || channel.getName().toLowerCase().contains("spam") ||
            (channel instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategory() != null && iCategorizableChannel.getParentCategory().getName().toLowerCase().contains("counting"));

    }

}
