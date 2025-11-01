package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class RockPaperScissorsCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        LiteralCommandNode<ChatCommandSource> node = dispatcher.register(Commands.literal("rockpaperscissors")
            .then(Commands.literal("paper")
                .executes(context -> rockPaperScissors(context.getSource(), Move.PAPER))
            )
            .then(Commands.literal("rock")
                .executes(context -> rockPaperScissors(context.getSource(), Move.ROCK))
            )
            .then(Commands.literal("scissors")
                .executes(context -> rockPaperScissors(context.getSource(), Move.SCISSORS))
            )
        );
        dispatcher.register(Commands.literal("rps").redirect(node));

    }

    private static int rockPaperScissors(ChatCommandSource source, Move move) {

        int random = Scissors.RANDOM.nextInt(-1, 2);
        switch(random) {

            case 0 -> source.sendSuccess("I chose **" + move.name + "**! It's a tie! Try again.");
            case -1 -> {

                Move botMove = switch(move) {

                    case ROCK -> Move.SCISSORS;
                    case PAPER -> Move.ROCK;
                    case SCISSORS -> Move.PAPER;

                };
                source.sendSuccess("I chose **" + botMove.name + "**! You win!" + (botMove == Move.SCISSORS ? " *What?! How did I lose with scissors? This must be a glitch...*" : ""));

            }
            case 1 -> {

                Move botMove = switch(move) {

                    case ROCK -> Move.PAPER;
                    case PAPER -> Move.SCISSORS;
                    case SCISSORS -> Move.ROCK;

                };
                source.sendSuccess("I chose **" + botMove.name + "**! I win!" + (botMove == Move.SCISSORS ? " *Yay! I win with scissors again! I mean, that was expected.*" : ""));

            }

        }
        return random;

    }

    private enum Move {

        ROCK("rock"),
        PAPER("paper"),
        SCISSORS("scissors");

        private final String name;

        Move(String name) {

            this.name = name;

        }

    }

}
