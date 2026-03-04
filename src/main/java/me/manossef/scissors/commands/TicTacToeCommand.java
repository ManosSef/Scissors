package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.TicTacToe;
import net.dv8tion.jda.api.entities.User;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class TicTacToeCommand {

    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with yourself"));
    private static final SimpleCommandExceptionType NO_BOTS = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with a bot"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "tictactoe";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.argument("opponent", UserArgument.user())
                .executes(context -> startTicTacToeGame(context.getSource(), context.getArgument("opponent", User.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, "Starts a tic-tac-toe game between you and another user.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Starts a game of tic-tac-toe between you and the specified user.
                
                Syntax: %s
                
                For the %s argument, you can use a user ID or a mention (ping) of the user in question.
                
                Fails if the specified user does not exist in the server, is the same as the user running the command, or is a bot.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " <opponent>"),
            monospace("<opponent>")));

    }

    private static int startTicTacToeGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw Commands.USER_NOT_FOUND.create();
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a tic-tac-toe game with " + user.getAsMention());
        new TicTacToe(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

}
