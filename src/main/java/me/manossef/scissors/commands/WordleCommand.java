package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.puzzles.Wordle;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class WordleCommand {
    private static final SimpleCommandExceptionType CANNOT_START = new SimpleCommandExceptionType(new LiteralMessage("Cannot start Wordle games in this session"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "wordle";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> startWordle(context.getSource(), false))
            .then(Commands.literal("hard")
                .executes(context -> startWordle(context.getSource(), true))
            )
            .then(Commands.argument("answer", StringArgumentType.word())
                .requires(Commands.devRestricted())
                .executes(context -> startWordle(context.getSource(), false, context.getArgument("answer", String.class)))
                .then(Commands.literal("hard")
                    .executes(context -> startWordle(context.getSource(), true, context.getArgument("answer", String.class)))
                )
            )
        );
        HelpCommand.addLine(baseLiteral, "Starts a game of Wordle.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Starts a game of Wordle. A message that will keep track of your progress is posted after this command is run. Replying to that message with a 5-letter word will guess that word, causing the bot to edit the message \
                to reveal the colors for each letter in the word.
                
                Here are all available syntaxes for this command:
                - %s: Starts a normal game of Wordle.
                - %s: Starts a game of Wordle in hard mode, which works like the hard mode in the New York Times' Wordle.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " hard")
        ));
    }

    private static int startWordle(ChatCommandSource source, boolean hardMode) throws CommandSyntaxException {
        if(Wordle.canStart()) {
            source.sendSuccess("Starting a game of Wordle" + (hardMode ? " in hard mode" : ""));
            new Wordle(source.commandMessage().getChannel(), hardMode);
            return 1;
        }
        throw CANNOT_START.create();
    }

    private static int startWordle(ChatCommandSource source, boolean hardMode, String answer) throws CommandSyntaxException {
        if(Wordle.canStart()) {
            source.sendSuccess("Starting a game of Wordle" + (hardMode ? " in hard mode" : ""));
            new Wordle(source.commandMessage().getChannel(), hardMode, answer);
            return 1;
        }
        throw CANNOT_START.create();
    }
}
