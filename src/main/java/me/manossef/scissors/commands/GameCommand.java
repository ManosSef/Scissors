package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.RockPaperScissors;
import me.manossef.scissors.games.TicTacToe;
import net.dv8tion.jda.api.entities.User;

public class GameCommand {

    private static final SimpleCommandExceptionType USER_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("No user was found"));
    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot start a game with yourself"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("game")
            .then(Commands.literal("tictactoe")
                .then(Commands.argument("opponent", UserArgument.user())
                    .executes(context -> startTicTacToeGame(context.getSource(), context.getArgument("opponent", User.class)))
                )
            )
            .then(Commands.literal("rps")
                .then(Commands.argument("opponent", UserArgument.user())
                    .executes(context -> startRockPaperScissorsGame(context.getSource(), context.getArgument("opponent", User.class)))
                )
            )
        );

    }

    private static int startTicTacToeGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw USER_NOT_FOUND.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a tic-tac-toe game with " + user.getAsMention());
        new TicTacToe(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

    private static int startRockPaperScissorsGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw USER_NOT_FOUND.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a game of rock paper scissors with " + user.getAsMention());
        new RockPaperScissors(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

}
