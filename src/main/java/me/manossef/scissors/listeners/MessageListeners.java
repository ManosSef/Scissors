package me.manossef.scissors.listeners;

import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
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
        if(content.matches("^[0-9]+$")) {

            if(Scissors.RANDOM.nextInt(10) == 0 && channel.canTalk()
                && !(channel.getName().toLowerCase().contains("counting") || channel.getName().toLowerCase().contains("spam") || (message.getCategory() != null && message.getCategory().getName().toLowerCase().contains("counting"))))
                replyWithRandomMessage(message, content.equals("67") ? Messages.GPPCT_BRAINROT_RESPONSES : Messages.GPPCT_RESPONSES);

        }
        if(content.toLowerCase().contains("scissors")) {

            if(Scissors.RANDOM.nextInt(5) == 0 && channel.canTalk())
                replyWithRandomMessage(message, Messages.SCISSORS_RESPONSES);

        }
        if(content.toLowerCase().contains("paper"))
            message.addReaction(Emoji.fromUnicode("✂️")).onErrorMap(e -> null).queue();

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        if(event.getMessageAuthorIdLong() != Scissors.DISCORD_API.getSelfUser().getIdLong()) return;
        if(!event.getEmoji().getName().equals("\uD83D\uDDD1\uFE0F")) return;
        if(event.getUserIdLong() != SharedConstants.MY_USER_ID) return;
        event.retrieveMessage().onSuccess(message -> message.delete().queue()).queue();

    }

    private void replyWithRandomMessage(Message message, List<String> possibleMessages) {

        message.getChannel().sendMessage(possibleMessages.get(Scissors.RANDOM.nextInt(possibleMessages.size())).formatted(message.getAuthor().getAsMention()))
            .setMessageReference(message)
            .mentionRepliedUser(false)
            .setAllowedMentions(Collections.emptyList())
            .queue();

    }

}
