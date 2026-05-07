package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Emojis;
import me.manossef.scissors.Scissors;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;

public class CoinflipCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        String baseLiteral = "coinflip";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> flipCoin(context.getSource()))
            .then(Commands.literal("edge")
                .requires(Commands.devRestricted())
                .executes(context -> rollEdge(context.getSource()))
            )
        );
        HelpCommand.addLine(baseLiteral, "Flips a coin.");
        HelpCommand.addLiteral(baseLiteral, "Flips a coin and returns either " + bold("heads") + " or " + bold("tails") + ".");

    }

    private static int flipCoin(ChatCommandSource source) {

        int random = Scissors.RANDOM.nextInt(12000);
        if(random < 5999) source.sendSuccess("You rolled " + bold("heads"));
        else if(random < 11998) source.sendSuccess("You rolled " + bold("tails"));
        else sendEdgeSuccess(source);
        return random;

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
