package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.arguments.UserArgument;
import me.manossef.scissors.games.RockPaperScissors;

public class RockPaperScissorsCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        LiteralCommandNode<ChatCommandSource> node = dispatcher.register(Commands.literal("rockpaperscissors")
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
                .redirect(dispatcher.getRoot().getChild("game").getChild("rps").getChild("opponent"))
            )
        );
        dispatcher.register(Commands.literal("rps").redirect(node));

    }

    private static int rockPaperScissors(ChatCommandSource source, RockPaperScissors.Move move) {

        int random = Scissors.RANDOM.nextInt(-1, 2);
        switch(random) {

            case 0 -> source.sendSuccess("I chose **" + move.getName() + "**! It's a tie! Try again.");
            case -1 -> {

                RockPaperScissors.Move botMove = switch(move) {

                    case ROCK -> RockPaperScissors.Move.SCISSORS;
                    case PAPER -> RockPaperScissors.Move.ROCK;
                    case SCISSORS -> RockPaperScissors.Move.PAPER;

                };
                source.sendSuccess("I chose **" + botMove.getName() + "**! You win!" + (botMove == RockPaperScissors.Move.SCISSORS ? " *What?! How did I lose with scissors? This must be a glitch...*" : ""));

            }
            case 1 -> {

                RockPaperScissors.Move botMove = switch(move) {

                    case ROCK -> RockPaperScissors.Move.PAPER;
                    case PAPER -> RockPaperScissors.Move.SCISSORS;
                    case SCISSORS -> RockPaperScissors.Move.ROCK;

                };
                source.sendSuccess("I chose **" + botMove.getName() + "**! I win!" + (botMove == RockPaperScissors.Move.SCISSORS ? " *Yay! I win with scissors again! I mean, that was expected.*" : ""));

            }

        }
        return random;

    }

}
