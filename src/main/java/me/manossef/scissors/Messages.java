package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;

public class Messages {
    public static final long MY_USER_ID = 611151083141857286L;
    public static final String MY_MENTION = "<@" + MY_USER_ID + ">";

    public static String getLinkWithInfo(Message message) {
        return message.getJumpUrl() + " (channel: " + message.getChannelId() + ", message: " + message.getId() + ")";
    }

    public static String truncate(String message) {
        if(message.length() <= Message.MAX_CONTENT_LENGTH)
            return message;
        if(message.endsWith("```"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 6) + "...```";
        else if(message.endsWith("``"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 5) + "...``";
        else if(message.endsWith("`"))
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 4) + "...`";
        else
            return message.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "...";
    }

    public static String truncateCommandResponse(String content, Logger logger) {
        if(content.length() > Message.MAX_CONTENT_LENGTH) {
            logger.warn("Tried to send too long a command response; truncating: {}", content);
            return Messages.truncate(content);
        }
        return content;
    }
}