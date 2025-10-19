package me.manossef.scissors.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListeners extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        if(message.getContentRaw().toLowerCase().contains("paper"))
            message.addReaction(Emoji.fromUnicode("✂️")).queue();

    }

}
