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
        );
        HelpCommand.addLine(baseLiteral, "Flips a coin.");
        HelpCommand.addLiteral(baseLiteral, "Flips a coin and returns either " + bold("heads") + " or " + bold("tails") + ".");

    }

    private static int flipCoin(ChatCommandSource source) {

        int random = Scissors.RANDOM.nextInt(12000);
        if(random < 5999) source.sendSuccess("You rolled " + bold("heads"));
        else if(random < 11998) source.sendSuccess("You rolled " + bold("tails"));
        else source.sendSuccess(bold("The coin landed on the edge!") + " " + Emojis.COIN.getFormatted() + Emojis.FOUR_LEAF_CLOVER.getFormatted());
        return random;

    }

}
