package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.RockPaperScissors;
import me.manossef.scissors.games.TicTacToe;
import net.dv8tion.jda.api.entities.User;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class GameCommand {

    private static final SimpleCommandExceptionType USER_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No user was found"));
    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot start a game with yourself"));
    private static final SimpleCommandExceptionType NO_BOTS = new SimpleCommandExceptionType(new LiteralMessage("You cannot start a game with a bot"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "game";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("rps")
                .then(Commands.argument("opponent", UserArgument.user())
                    .executes(context -> startRockPaperScissorsGame(context.getSource(), context.getArgument("opponent", User.class)))
                )
            )
            .then(Commands.literal("tictactoe")
                .then(Commands.argument("opponent", UserArgument.user())
                    .executes(context -> startTicTacToeGame(context.getSource(), context.getArgument("opponent", User.class)))
                )
            )
        );
        HelpCommand.addLine(baseLiteral, "Starts a rock paper scissors or tic-tac-toe game between you and another user.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Starts a game of either rock paper scissors or tic-tac-toe between you and the specified user.
                
                Here are the available syntaxes for this command:
                - %s: Starts a game of rock paper scissors between you and the specified user.
                - %s: Starts a game of tic-tac-toe between you and the specified user.
                
                For the %s argument, you can use a user ID or a mention (ping) of the user in question.
                
                Fails if the specified user does not exist in the server, is the same as the user running the command, or is a bot.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " rps <opponent>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " tictactoe <opponent>"),
            monospace("<opponent>")));

    }

    private static int startTicTacToeGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw USER_NOT_FOUND.create();
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a tic-tac-toe game with " + user.getAsMention());
        new TicTacToe(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

    static int startRockPaperScissorsGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw USER_NOT_FOUND.create();
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a game of rock paper scissors with " + user.getAsMention());
        new RockPaperScissors(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

}
