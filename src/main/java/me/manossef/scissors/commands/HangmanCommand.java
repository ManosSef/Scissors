package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.puzzles.Hangman;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class HangmanCommand {

    private static final SimpleCommandExceptionType CANNOT_START = new SimpleCommandExceptionType(new LiteralMessage("Cannot start hangman games in this session"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "hangman";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> startHangman(context.getSource(), Hangman.Difficulty.NORMAL))
            .then(Commands.literal("hard")
                .executes(context -> startHangman(context.getSource(), Hangman.Difficulty.HARD))
            )
            .then(Commands.literal("impossible")
                .executes(context -> startHangman(context.getSource(), Hangman.Difficulty.IMPOSSIBLE))
            )
        );
        HelpCommand.addLine(baseLiteral, "Starts a game of hangman.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Starts a game of hangman. A message that will keep track of your progress is posted after this command is run. Replying to that message with a letter or a word will guess that letter or word, causing the bot to edit the message \
                to reveal any appearances of the guessed letter or whether the guessed word is correct.
                
                Here are all available syntaxes for this command:
                - %s: Starts a game of hangman where you lose if you make 6 mistakes.
                - %s: Starts a game of hangman where you lose if you make 3 mistakes.
                - %s: Starts a game of hangman where you lose if you make 1 mistake.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " hard"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " impossible")));

    }

    private static int startHangman(ChatCommandSource source, Hangman.Difficulty difficulty) throws CommandSyntaxException {

        if(Hangman.canStart()) {

            source.sendSuccess("Starting a game of hangman of " + difficulty.toString().toLowerCase() + " difficulty");
            new Hangman(source.commandMessage().getChannel(), difficulty);
            return 1;

        }
        throw CANNOT_START.create();

    }

}
