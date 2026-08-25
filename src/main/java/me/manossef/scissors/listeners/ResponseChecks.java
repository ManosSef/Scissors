package me.manossef.scissors.listeners;

import me.manossef.scissors.Environment;
import me.manossef.scissors.LazilyFormattedText;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.time.Month;
import java.time.MonthDay;
import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.strike;

public class ResponseChecks {
    private static final MonthDay APRIL_FOOLS = MonthDay.of(Month.APRIL, 1);
    private static final MonthDay SCISSORS_BIRTHDAY = MonthDay.of(Month.OCTOBER, 16);

    static boolean promptsGPPCT(Message message, Configuration config) {
        MessageChannelUnion channel = message.getChannel();
        return message.getContentRaw().matches(config.getOptionForChannel(Options.GPPCT_HANDLING, channel).getRegex())
            && (!isDisallowedForGPPCT(channel) || config.getOptionForChannelOnly(Options.GPPCT_RESPONSES, channel).isPresent());
    }

    static List<LazilyFormattedText> getGPPCTResponses(String content) {
        return MonthDay.now().equals(APRIL_FOOLS) ? Responses.GPPCT_APRIL_FOOLS_RESPONSES
            : isScissorsBirthday() ? Responses.GPPCT_BIRTHDAY_RESPONSES
            : content.equals("67") ? Responses.GPPCT_BRAINROT_RESPONSES : Responses.GPPCT_RESPONSES;
    }

    static boolean promptsPing(Message message, Configuration ignored) {
        return message.getContentRaw().contains(Scissors.DISCORD_API.getSelfUser().getAsMention());
    }

    static boolean promptsScissors(Message message, Configuration ignored) {
        return message.getContentRaw().toLowerCase().contains("scissors");
    }

    static boolean promptsMeme(Message message, Configuration ignored) {
        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return false;
        return referencedMessage.getAuthor().getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong()
            && referencedMessage.getContentRaw().equals("A number?! At this time of year? At this time of day? In this part of the country? " + strike("Localized entirely within your kitchen?!"))
            && referencedMessage.getMessageReference() != null
            && message.getContentRaw().toLowerCase().replaceAll("[^a-z]+", "").equals("yes");
    }

    static boolean promptsBirthday(Message message, Configuration ignored) {
        String content;
        return isScissorsBirthday()
            && ((content = message.getContentRaw().toLowerCase()).contains("happy birthday scissors")
                || content.contains("happy birthday " + Scissors.DISCORD_API.getSelfUser().getAsMention())
                || repliesWithWish(message));
    }

    private static boolean isScissorsBirthday() {
        return Environment.DEBUG_SCISSORS_BIRTHDAY || MonthDay.now().equals(SCISSORS_BIRTHDAY);
    }

    private static boolean repliesWithWish(Message message) {
        Message referencedMessage = message.getReferencedMessage();
        if(referencedMessage == null) return false;
        return referencedMessage.getAuthor().getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong()
            && message.getContentRaw().toLowerCase().contains("happy birthday");
    }

    private static boolean isDisallowedForGPPCT(Channel channel) {
        return channel.getName().toLowerCase().contains("counting") || channel.getName().toLowerCase().contains("spam") ||
            (channel instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategory() != null && iCategorizableChannel.getParentCategory().getName().toLowerCase().contains("counting"));
    }
}