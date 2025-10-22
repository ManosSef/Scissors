package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class CoinflipCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("coinflip")
            .executes(context -> flipCoin(context.getSource()))
        );

    }

    private static int flipCoin(ChatCommandSource source) {

        int random = Scissors.RANDOM.nextInt(12000);
        if(random < 5999) source.sendSuccess("You rolled **heads**");
        else if(random < 11998) source.sendSuccess("You rolled **tails**");
        else source.sendSuccess("**The coin landed on the edge!** \uD83E\uDE99\uD83C\uDF40");
        return random;

    }

}
