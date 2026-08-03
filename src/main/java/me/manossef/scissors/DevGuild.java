package me.manossef.scissors;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.dv8tion.jda.api.utils.MarkdownUtil.codeblock;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class DevGuild {
    private static final long DEV_GUILD_ID = SharedConstants.IS_STAGING ? 1473455985690546227L : 1428446740855656542L;
    private static final long STATUS_LOGS_CHANNEL_ID = SharedConstants.IS_STAGING ? 1473456136526102631L : 1428451434373845114L;
    private static final long COMMAND_LOGS_CHANNEL_ID = SharedConstants.IS_STAGING ? 1473456151583654070L : 1428462041441501275L;
    private static final long RESPONSE_LOGS_CHANNEL_ID = SharedConstants.IS_STAGING ? 1473456190079111248L : 1473074962733600912L;
    private static final long DONE_ISSUES_CHANNEL_ID = SharedConstants.IS_STAGING ? 1473456208697626795L : 1429172420069298176L;
    private static final long INVALID_ISSUES_CHANNEL_ID = SharedConstants.IS_STAGING ? 1473456228482154638L : 1429172445067214988L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DevGuild.class);

    private static WebhookClient<Message> logWebhook;

    public static Guild getDevGuild() {
        Guild devGuild = Scissors.DISCORD_API.getGuildById(DEV_GUILD_ID);
        if(devGuild == null) {
            LOGGER.error("Could not find dev guild.");
            return null;
        }
        return devGuild;
    }

    public static void log(String message) {
        new Thread(() -> {
            if(logWebhook == null)
                logWebhook = WebhookClient.createClient(Scissors.DISCORD_API, SharedConstants.LOG_WEBHOOK_URL);
            String fixed = message.replace("`", "").strip();
            if(fixed.contains("\n")) {
                logWebhook.sendMessage(Util.truncate(codeblock(fixed))).queue();
                return;
            }
            logWebhook.sendMessage(Util.truncate(monospace(fixed))).queue();
        }, "DevGuildLogger").start();
    }

    public static void logStatus(String message) {
        logMessage(message, getStatusLogChannel());
    }

    public static void logCommand(String message) {
        logMessage(message, getCommandLogChannel());
    }

    public static void logCommandError(String message, Throwable exception) {
        logCommand(message);
        logCommand(codeblock(exception.getClass().getName() + ": " + exception.getMessage() + "\n" + Util.getStackTrace(exception)));
    }

    public static void logResponse(String message) {
        logMessage(message, getResponseLogChannel());
    }

    public static void logDoneIssue(MessageCreateData message) {
        embedMessage(message, getDoneIssuesChannel());
    }

    public static void logInvalidIssue(MessageCreateData message) {
        embedMessage(message, getInvalidIssuesChannel());
    }

    public static void logMessage(String message, MessageChannel channel) {
        if(channel == null) return;
        if(message.length() > Message.MAX_CONTENT_LENGTH) {
            channel.sendMessage(Util.truncate(message)).queue();
            LOGGER.warn("Could not log entire message: {}", message);
            return;
        }
        channel.sendMessage(message).queue();
    }

    public static void embedMessage(MessageCreateData message, MessageChannel channel) {
        if(channel == null) return;
        channel.sendMessage(message).queue();
    }

    public static MessageChannel getStatusLogChannel() {
        return getChannel(STATUS_LOGS_CHANNEL_ID, "status-logs");
    }

    public static MessageChannel getCommandLogChannel() {
        return getChannel(COMMAND_LOGS_CHANNEL_ID, "command-logs");
    }

    public static MessageChannel getResponseLogChannel() {
        return getChannel(RESPONSE_LOGS_CHANNEL_ID, "response-logs");
    }

    public static MessageChannel getDoneIssuesChannel() {
        return getChannel(DONE_ISSUES_CHANNEL_ID, "done-issues");
    }

    public static MessageChannel getInvalidIssuesChannel() {
        return getChannel(INVALID_ISSUES_CHANNEL_ID, "invalid-issues");
    }

    private static MessageChannel getChannel(long id, String name) {
        Guild devGuild = getDevGuild();
        if(devGuild == null) return null;
        GuildChannel logsChannel = DevGuild.getDevGuild().getGuildChannelById(id);
        if(logsChannel == null) {
            LOGGER.error("Could not find #{} in dev guild.", name);
            return null;
        }
        if(!(logsChannel instanceof MessageChannel messageChannel)) {
            LOGGER.error("Found #{} in dev guild, but it isn't a message channel.", name);
            return null;
        }
        return messageChannel;
    }
}