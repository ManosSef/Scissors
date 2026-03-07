package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.puzzles.Wordle;

public class WordleCommand {

    private static final SimpleCommandExceptionType CANNOT_START = new SimpleCommandExceptionType(new LiteralMessage("Cannot start Wordle games in this session"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "wordle";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> startWordle(context.getSource()))
            .then(Commands.argument("answer", StringArgumentType.word())
                .requires(Commands.devRestricted())
                .executes(context -> startWordle(context.getSource(), context.getArgument("answer", String.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, "Starts a game of Wordle.");
        HelpCommand.addLiteral(baseLiteral, """
            Starts a game of Wordle. A message that will keep track of your progress is posted after this command is run. Replying to that message with a 5-letter word will guess that word, causing the bot to edit the message \
            to reveal the colors for each letter in the word.""");

    }

    private static int startWordle(ChatCommandSource source) throws CommandSyntaxException {

        if(Wordle.canStart()) {

            source.sendSuccess("Starting a game of Wordle");
            new Wordle(source.commandMessage().getChannel());
            return 1;

        }
        throw CANNOT_START.create();

    }

    private static int startWordle(ChatCommandSource source, String answer) throws CommandSyntaxException {

        if(Wordle.canStart()) {

            source.sendSuccess("Starting a game of Wordle");
            new Wordle(source.commandMessage().getChannel(), answer);
            return 1;

        }
        throw CANNOT_START.create();

    }

}
