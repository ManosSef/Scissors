package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.TicTacToe;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class TicTacToeCommand {
    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with yourself"));
    private static final SimpleCommandExceptionType NO_BOTS = new SimpleCommandExceptionType(new LiteralMessage("You cannot play tic-tac-toe with that bot"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "tictactoe";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("bot")
                .executes(context -> startBotTicTacToeGame(context.getSource()))
            )
            .then(Commands.argument("opponent", UserArgument.user())
                .executes(context -> startTicTacToeGame(context.getSource(), context.getArgument("opponent", User.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, s -> "Starts a tic-tac-toe game between you and the bot or another user.");
        HelpCommand.addLiteral(baseLiteral, source -> {
            Channel channel = source.commandMessage().getChannel();
            return String.format("""
                    Starts a game of tic-tac-toe between you and the bot or the specified user.
                    
                    Here are all available syntaxes for this command:
                    - %s: Starts a game of tic-tac-toe between you and the bot.
                    - %s: Starts a game of tic-tac-toe between you and the specified user. Fails if the specified user does not exist in the server, is the same as the user running the command, or is a bot other than Scissors.
                    
                    For the %s argument, you can use a user ID or a mention (ping) of the user in question.""",
                Commands.format(baseLiteral + " bot", channel),
                Commands.format(baseLiteral + " <opponent>", channel),
                monospace("<opponent>"));
        });
    }

    private static int startBotTicTacToeGame(ChatCommandSource source) {
        source.sendSuccess("Starting a tic-tac-toe game with the bot", true);
        new TicTacToe(source.user(), Scissors.DISCORD_API.getSelfUser(), source.commandMessage().getChannel());
        return 1;
    }

    private static int startTicTacToeGame(ChatCommandSource source, User user) throws CommandSyntaxException {
        if(user == null) throw Commands.USER_NOT_FOUND.create();
        if(user.isBot() && user.getIdLong() == Scissors.DISCORD_API.getSelfUser().getIdLong())
            return startBotTicTacToeGame(source);
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a tic-tac-toe game with " + user.getAsMention(), true);
        new TicTacToe(source.user(), user, source.commandMessage().getChannel());
        return 1;
    }
}