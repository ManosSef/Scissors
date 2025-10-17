package me.manossef.scissors.listeners;

import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        MessageChannel channel = event.getChannel();
        if(!channel.canTalk()) return;
        Message message = event.getMessage();
        if(!message.getContentRaw().startsWith(SharedConstants.COMMAND_PREFIX)) return;
        User user = event.getAuthor();
        if(user.isBot() || user.isSystem()) return;
        Commands.dispatch(message, user);

    }

}
