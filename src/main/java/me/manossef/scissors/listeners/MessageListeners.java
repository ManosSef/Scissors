package me.manossef.scissors.listeners;

import me.manossef.scissors.*;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Month;
import java.time.MonthDay;
import java.util.Collections;
import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.strike;

public class MessageListeners extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        if(content.startsWith(Commands.getPrefix(message.getChannel()))) return;
        Configuration config = Scissors.getConfiguration();
        if(this.promptsGPPCT(message, config))
            replyWithRandomMessage(message, isApril1st() ? Responses.GPPCT_APRIL_FOOLS_RESPONSES : content.equals("67") ? Responses.GPPCT_BRAINROT_RESPONSES : Responses.GPPCT_RESPONSES, "GPPCT");
        else if(this.promptsPing(message, config))
            replyWithRandomMessage(message, Responses.PING_RESPONSES, "ping");
        else if(this.promptsMeme(message, config))
            replyWithRandomMessage(message, Responses.MEME_RESPONSES, "meme");
        else if(this.promptsScissors(message, config))
            replyWithRandomMessage(message, Responses.SCISSORS_RESPONSES, "scissors");
        if(message.getContentRaw().toLowerCase().contains("paper") && config.getOptionForChannel(Options.REACT_TO_PAPER, message.getChannel()))
            message.addReaction(Emojis.SCISSORS).onErrorMap(e -> null).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMessageAuthorIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(!event.getEmoji().equals(Emojis.WASTEBASKET)) return;
        if(event.getUserIdLong() != me.manossef.scissors.Messages.MY_USER_ID) return;
        event.retrieveMessage().onSuccess(message -> message.delete().queue()).queue();
    }

    private void replyWithRandomMessage(Message message, List<String> possibleMessages, String responseType) {
        if(message.getChannel().canTalk()) {
            message.getChannel().sendMessage(possibleMessages.get(Scissors.RANDOM.nextInt(possibleMessages.size())).formatted(message.getAuthor().getAsMention()))
                .setMessageReference(message)
                .mentionRepliedUser(false)
                .setAllowedMentions(Collections.emptyList())
                .queue();
            DevGuild.logResponse("Posted a " + responseType + " response to " + me.manossef.scissors.Messages.getLinkWithInfo(message));
            return;
        }
        DevGuild.logResponse("Could not post a " + responseType + " response to " + me.manossef.scissors.Messages.getLinkWithInfo(message) + "; no permission to talk");
    }

    private boolean promptsGPPCT(Message message, Configuration config) {
        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().matches(config.getOptionForChannel(Options.GPPCT_HANDLING, message.getChannel()).getRegex())
            && config.getOptionForChannel(Options.GPPCT_RESPONSES, channel)
            && (!isDisallowedForGPPCT(channel) || config.getOptionForChannelOnly(Options.GPPCT_RESPONSES, channel).isPresent())
            && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Options.GPPCT_RESPONSE_CHANCE, channel);
    }

    private boolean promptsPing(Message message, Configuration config) {
        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().contains(Scissors.DISCORD_API.getSelfUser().getAsMention())
            && config.getOptionForChannel(Options.PING_RESPONSES, channel);
    }

    private boolean promptsScissors(Message message, Configuration config) {
        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().toLowerCase().contains("scissors")
            && config.getOptionForChannel(Options.SCISSORS_RESPONSES, channel)
            && Scissors.RANDOM.nextInt(100) < config.getOptionForChannel(Options.SCISSORS_RESPONSE_CHANCE, channel);
    }

    private boolean promptsMeme(Message message, Configuration config) {
        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return false;
        MessageChannelUnion channel = message.getChannel();
        return referencedMessage.getAuthor().getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong()
            && referencedMessage.getContentRaw().equals("A number?! At this time of year? At this time of day? In this part of the country? " + strike("Localized entirely within your kitchen?!"))
            && referencedMessage.getMessageReference() != null
            && message.getContentRaw().toLowerCase().replaceAll("[^a-z]+", "").equals("yes")
            && config.getOptionForChannel(Options.GPPCT_RESPONSES, channel);
    }

    public static boolean isDisallowedForGPPCT(Channel channel) {
        return channel.getName().toLowerCase().contains("counting") || channel.getName().toLowerCase().contains("spam") ||
            (channel instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategory() != null && iCategorizableChannel.getParentCategory().getName().toLowerCase().contains("counting"));
    }

    private static boolean isApril1st() {
        MonthDay now = MonthDay.now();
        return now.getMonth().equals(Month.APRIL) && now.getDayOfMonth() == 1;
    }
}