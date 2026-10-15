package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Messages;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.time.Duration;
import java.time.OffsetDateTime;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class PingCommand {
    private static final SimpleCommandExceptionType TOO_LONG = new SimpleCommandExceptionType(new LiteralMessage("It took me way too long to reply to this command"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "ping";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> pingUser(context.getSource(), false))
            .then(Commands.literal("nanos")
                .executes(context -> pingUser(context.getSource(), true))
            )
            .then(Commands.literal("discord")
                .executes(context -> pingDiscord(context.getSource()))
            )
            .then(Commands.literal("pong")
                .executes(context -> pong(context.getSource()))
            )
            .then(Commands.literal("manos")
                .executes(context -> mentionMe(context.getSource()))
            )
            .then(Commands.literal("manolhs")
                .executes(context -> mentionMe(context.getSource()))
            )
        );
        HelpCommand.addLine(baseLiteral, s -> "Pings Discord, measures the bot's reaction time, or replies with \"Pong!\"");
        HelpCommand.addLiteral(baseLiteral, source -> {
            Channel channel = source.commandMessage().getChannel();
            return String.format("""
                    Checks if or how quickly the bot replies to requests.
                   
                    Here are all available syntaxes for this command:
                    - %s: Measures how long it takes for the bot to reply to this command in milliseconds.
                    - %s: Measures how long it takes for the bot to reply to this command in nanoseconds.
                    - %s: Pings Discord's API.
                    - %s: Replies with "Pong!\"""",
                Commands.format(baseLiteral, channel),
                Commands.format(baseLiteral + " nanos", channel),
                Commands.format(baseLiteral + " discord", channel),
                Commands.format(baseLiteral + " pong", channel));
        });
    }

    private static int pingUser(ChatCommandSource source, boolean nanos) throws CommandSyntaxException {
        OffsetDateTime messageTime = source.commandMessage().getTimeCreated();
        OffsetDateTime now = OffsetDateTime.now();
        try {
            long ping = nanos ? Duration.between(messageTime, now).toNanos() : Duration.between(messageTime, now).toMillis();
            source.sendSuccess("It took me " + monospace(ping + (nanos ? "ns" : "ms")) + " to reply to this command", false);
            return (int) ping;
        } catch(ArithmeticException e) {
            throw TOO_LONG.create();
        }
    }

    private static int pingDiscord(ChatCommandSource source) {
        Long ping = Scissors.DISCORD_API.getRestPing().complete();
        source.sendSuccess("My ping to Discord is " + monospace(ping + "ms"), false);
        return ping.intValue();
    }

    private static int pong(ChatCommandSource source) {
        source.sendSuccess("Pong!", false);
        return 1;
    }

    private static int mentionMe(ChatCommandSource source) {
        source.sendSuccess(Messages.MY_MENTION, false);
        return 1;
    }
}