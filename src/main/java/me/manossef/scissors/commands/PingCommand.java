package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

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
        );
        HelpCommand.addLine(baseLiteral, "Pings Discord, measures the bot's reaction time, or replies with \"Pong!\"");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Checks if or how quickly the bot replies to requests.
               
                Here are all available syntaxes for this command:
                - %s: Measures how long it takes for the bot to reply to this command in milliseconds.
                - %s: Measures how long it takes for the bot to reply to this command in nanoseconds.
                - %s: Pings Discord's API.
                - %s: Replies with "Pong!\"""",
            Commands.format(baseLiteral),
            Commands.format(baseLiteral + " nanos"),
            Commands.format(baseLiteral + " discord"),
            Commands.format(baseLiteral + " pong")));
    }

    private static int pingUser(ChatCommandSource source, boolean nanos) throws CommandSyntaxException {
        OffsetDateTime messageTime = source.commandMessage().getTimeCreated();
        OffsetDateTime now = OffsetDateTime.now();
        try {
            long ping = nanos ? Duration.between(messageTime, now).toNanos() : Duration.between(messageTime, now).toMillis();
            source.sendSuccess("It took me " + monospace(ping + (nanos ? "ns" : "ms")) + " to reply to this command");
            return (int) ping;
        } catch(ArithmeticException e) {
            throw TOO_LONG.create();
        }
    }

    private static int pingDiscord(ChatCommandSource source) {
        Long ping = Scissors.DISCORD_API.getRestPing().complete();
        source.sendSuccess("My ping to Discord is " + monospace(ping + "ms"));
        return ping.intValue();
    }

    private static int pong(ChatCommandSource source) {
        source.sendSuccess("Pong!");
        return 1;
    }
}