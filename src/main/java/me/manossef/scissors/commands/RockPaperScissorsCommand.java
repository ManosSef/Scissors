package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.RockPaperScissors;
import net.dv8tion.jda.api.entities.User;

import static net.dv8tion.jda.api.utils.MarkdownUtil.*;

public class RockPaperScissorsCommand {

    private static final SimpleCommandExceptionType SAME_USER = new SimpleCommandExceptionType(new LiteralMessage("You cannot play rock paper scissors with yourself"));
    private static final SimpleCommandExceptionType NO_BOTS = new SimpleCommandExceptionType(new LiteralMessage("You cannot play rock paper scissors with a bot"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "rockpaperscissors";
        LiteralCommandNode<ChatCommandSource> node = dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.literal("paper")
                .executes(context -> rockPaperScissors(context.getSource(), RockPaperScissors.Move.PAPER))
            )
            .then(Commands.literal("rock")
                .executes(context -> rockPaperScissors(context.getSource(), RockPaperScissors.Move.ROCK))
            )
            .then(Commands.literal("scissors")
                .executes(context -> rockPaperScissors(context.getSource(), RockPaperScissors.Move.SCISSORS))
            )
            .then(Commands.argument("opponent", UserArgument.user())
                .executes(context -> startRockPaperScissorsGame(context.getSource(), context.getArgument("opponent", User.class)))
            )
        );
        String alias = "rps";
        dispatcher.register(Commands.literal(alias).redirect(node));
        HelpCommand.addLine(baseLiteral, "Plays rock paper scissors with the bot or starts a game of rock paper scissors with another user.", alias);
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Plays rock paper scissors with the bot or starts a game of rock paper scissors with another user.
                
                Here are all available syntaxes for this command:
                - %s: Rock paper scissors where your move is the specified move and the bot's move is random. Replies with the winner.
                - %s: Starts a game of rock paper scissors between you and the specified user. Fails if the specified user does not exist in the server, is the same as the user running the command, or is a bot.
                
                For the %s argument, you can use a user ID or a mention (ping) of the user in question.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " (rock|paper|scissors)"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " <opponent>"),
            monospace("<opponent>")), alias);

    }

    private static int rockPaperScissors(ChatCommandSource source, RockPaperScissors.Move move) {

        int random = Scissors.RANDOM.nextInt(-1, 2);
        switch(random) {

            case 0 -> source.sendSuccess("I chose " + bold(move.getName()) + "! It's a tie! Try again.");
            case -1 -> {

                RockPaperScissors.Move botMove = switch(move) {

                    case ROCK -> RockPaperScissors.Move.SCISSORS;
                    case PAPER -> RockPaperScissors.Move.ROCK;
                    case SCISSORS -> RockPaperScissors.Move.PAPER;

                };
                source.sendSuccess("I chose " + bold(botMove.getName()) + "! You win!" + (botMove == RockPaperScissors.Move.SCISSORS ? " " + italics("What?! How did I lose with scissors? This must be a glitch...") : ""));

            }
            case 1 -> {

                RockPaperScissors.Move botMove = switch(move) {

                    case ROCK -> RockPaperScissors.Move.PAPER;
                    case PAPER -> RockPaperScissors.Move.SCISSORS;
                    case SCISSORS -> RockPaperScissors.Move.ROCK;

                };
                source.sendSuccess("I chose " + bold(botMove.getName()) + "! I win!" + (botMove == RockPaperScissors.Move.SCISSORS ? " " + italics("Yay! I win with scissors again! I mean, that was expected.") : ""));

            }

        }
        return random;

    }

    private static int startRockPaperScissorsGame(ChatCommandSource source, User user) throws CommandSyntaxException {

        if(user == null) throw Commands.USER_NOT_FOUND.create();
        if(user.isBot() || user.isSystem()) throw NO_BOTS.create();
        if(user.getIdLong() == source.user().getIdLong()) throw SAME_USER.create();
        source.sendSuccess("Starting a game of rock paper scissors with " + user.getAsMention());
        new RockPaperScissors(source.user(), user, source.commandMessage().getChannel());
        return 1;

    }

}
