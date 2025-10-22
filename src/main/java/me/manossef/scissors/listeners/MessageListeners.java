package me.manossef.scissors.listeners;

import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MessageListeners extends ListenerAdapter {

    private static final List<String> GPPCT_RESPONSES = new ArrayList<>() {{

        add("As part of the Great Purge of Pointless Counting Threads (GPPCT), this channel has been closed due to the following criteria: **Not a counting channel.**");
        add("RAHHH! No counting!");
        add("I don't even get where's the fun in counting channels");
        add("Be careful not to stationary zero too much or you might end up with zero brain cells");
        add("Whoever invented the term \"stationary zeroing\" anyway?");
        add("I hope this is hardcore counting so you are prevented from doing that again");
        add("Reminder that this is not a counting channel");
        add("What channel is this again?");

    }};

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
