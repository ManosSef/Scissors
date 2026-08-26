package me.manossef.scissors.listeners;

import me.manossef.scissors.*;
import me.manossef.scissors.config.Configuration;
import me.manossef.scissors.config.Options;
import me.manossef.scissors.listeners.responses.ResponseType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Collections;

public class MessageListeners extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if(message.getAuthor().isBot() || message.getAuthor().isSystem()) return;
        String content = message.getContentRaw();
        if(content.startsWith(Commands.getPrefix(message.getChannel()))) return;
        Configuration config = Scissors.getConfiguration();
        for(ResponseType type : ResponseType.values()) {
            if(type.shouldRespondTo(message, config)) {
                this.replyWithRandomMessage(message, type);
                break;
            }
        }
        if(message.getContentRaw().toLowerCase().contains("paper") && config.getOptionForChannel(Options.REACT_TO_PAPER, message.getChannel()))
            message.addReaction(Emojis.SCISSORS).onErrorMap(e -> null).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMessageAuthorIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(!event.getEmoji().equals(Emojis.WASTEBASKET)) return;
        if(event.getUserIdLong() != Messages.MY_USER_ID) return;
        event.retrieveMessage().onSuccess(message -> message.delete().queue()).queue();
    }

    private void replyWithRandomMessage(Message message, ResponseType responseType) {
        if(message.getChannel().canTalk()) {
            String response = responseType.getResponse(message);
            message.getChannel().sendMessage(response)
                .setMessageReference(message)
                .mentionRepliedUser(false)
                .setAllowedMentions(Collections.emptyList())
                .queue();
            DevGuild.logResponse("Posted a " + responseType + " response to " + Messages.getLinkWithInfo(message));
            return;
        }
        DevGuild.logResponse("Could not post a " + responseType + " response to " + Messages.getLinkWithInfo(message) + "; no permission to talk");
    }
}