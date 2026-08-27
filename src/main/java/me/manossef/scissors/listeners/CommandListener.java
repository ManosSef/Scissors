package me.manossef.scissors.listeners;

import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.commands.InfoCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if(!channel.canTalk()) return;
        Message message = event.getMessage();
        if(!message.getContentRaw().startsWith(Commands.getPrefix(channel))) return;
        User user = event.getAuthor();
        if(user.isBot() || user.isSystem()) return;
        Commands.dispatch(message, user);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(!event.getCustomId().equals("1")) return;
        event.getInteraction().deferEdit().queue();
        Message message = event.getMessage();
        message.reply(InfoCommand.getAllOptions(ChatCommandSource.of(message))).mentionRepliedUser(false).queue();
    }
}