package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.*;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class CoinflipCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "coinflip";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> flipCoin(context.getSource()))
            .then(Commands.literal("nofunnybusiness")
                .executes(context -> flipCoinNoFunnyBusiness(context.getSource()))
            )
            .then(Commands.literal("edge")
                .requires(Commands.devRestricted())
                .executes(context -> rollEdge(context.getSource()))
            )
        );
        HelpCommand.addLine(baseLiteral, "Flips a coin.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Flips a coin and returns either %s or %s.
                
                Here are all available syntaxes for this command:
                - %s: Flips a coin.
                - %s: Flips a coin with no funny business.""",
            bold("heads"),
            bold("tails"),
            Commands.format(baseLiteral),
            Commands.format(baseLiteral + " nofunnybusiness")));
    }

    private static int flipCoin(ChatCommandSource source) {
        int random = Scissors.RANDOM.nextInt(12000);
        if(random < 5999) source.sendSuccess("You rolled " + bold("heads"));
        else if(random < 11998) source.sendSuccess("You rolled " + bold("tails"));
        else sendEdgeSuccess(source);
        return random;
    }

    private static int flipCoinNoFunnyBusiness(ChatCommandSource source) {
        boolean random = Scissors.RANDOM.nextBoolean();
        if(random) source.sendSuccess("You rolled " + bold("heads"));
        else source.sendSuccess("You rolled " + bold("tails"));
        return random ? 1 : 0;
    }

    private static int rollEdge(ChatCommandSource source) {
        int random = 11998 + Scissors.RANDOM.nextInt(2);
        sendEdgeSuccess(source);
        return random;
    }

    private static void sendEdgeSuccess(ChatCommandSource source) {
        source.sendSuccess(bold("The coin landed on the edge!") + " " + Emojis.COIN.getFormatted() + Emojis.FOUR_LEAF_CLOVER.getFormatted());
    }
}