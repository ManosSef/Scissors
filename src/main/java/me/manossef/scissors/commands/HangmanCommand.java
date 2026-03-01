package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.puzzles.Hangman;

public class HangmanCommand {

    private static final SimpleCommandExceptionType CANNOT_START = new SimpleCommandExceptionType(new LiteralMessage("Cannot start hangman games in this session"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "hangman";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> startHangman(context.getSource()))
        );
        HelpCommand.addLine(baseLiteral, "Starts a game of hangman.");
        HelpCommand.addLiteral(baseLiteral, """
            Starts a game of hangman. A message that will keep track of your progress is posted after this command is run. Replying to that message with a letter or a word will guess that letter or word, causing the bot to edit the message \
            to reveal any appearances of the guessed letter or whether the guessed word is correct.""");

    }

    private static int startHangman(ChatCommandSource source) throws CommandSyntaxException {

        if(Hangman.canStart()) {

            source.sendSuccess("Starting a game of hangman");
            new Hangman(source.commandMessage().getChannel());
            return 1;

        }
        throw CANNOT_START.create();

    }

}
