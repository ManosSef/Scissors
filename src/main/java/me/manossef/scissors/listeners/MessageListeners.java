package me.manossef.scissors.listeners;

import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class MessageListeners extends ListenerAdapter {

    private static final List<String> GPPCT_RESPONSES = List.of(
        "As part of the Great Purge of Pointless Counting Threads (GPPCT), this channel has been closed due to the following criteria: **Not a counting channel.**",
        "RAHHH! No counting!",
        "I don't even get where's the fun in counting channels",
        "Be careful not to stationary zero too much or you might end up with zero brain cells",
        "Whoever invented the term \"stationary zeroing\" anyway?",
        "I hope this is hardcore counting so you are prevented from doing that again",
        "Reminder that this is not a counting channel",
        "What channel is this again?",
        "WHAT DID I SAY ABOUT COUNTING?",
        "HEY STOP IT",
        "I HOPE YOU GET BANNED FOR SPAMMING",
        "What's the punishment for spamming again?",
        "Did I miss this channel being renamed to #arbitrary-counting?",
        "BRO WHAT'S WITH YOU AND NUMBERS",
        "I'm made of numbers and I don't like them",
        "Right, now you have to be doing it on purpose",
        "Are you kidding me",
        "I'm gonna call the GPPCT police",
        "You know what, I guess you can keep doing it, it's not like I can stop you",
        "This isn't #counting, is it?",
        "Oh my goodness! Is that... a... a... *number*?",
        "Is that your age?"
    );

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        MessageChannelUnion channel = message.getChannel();
        if(content.matches("^[0-9]+$")) {

            if(Scissors.RANDOM.nextInt(10) == 0 && channel.canTalk()
                && !(channel.getName().toLowerCase().contains("counting") || (message.getCategory() != null && message.getCategory().getName().toLowerCase().contains("counting"))))
                channel.sendMessage(GPPCT_RESPONSES.get(Scissors.RANDOM.nextInt(GPPCT_RESPONSES.size()))).setMessageReference(message).mentionRepliedUser(false).queue();

        }
        if(content.toLowerCase().contains("scissors")) {

            if(Scissors.RANDOM.nextInt(5) == 0 && channel.canTalk())
                channel.sendMessage("RAHHH!").setMessageReference(message).mentionRepliedUser(false).queue();

        }
        if(content.toLowerCase().contains("paper"))
            message.addReaction(Emoji.fromUnicode("✂️")).queue();

    }

}
