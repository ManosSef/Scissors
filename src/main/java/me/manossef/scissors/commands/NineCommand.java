package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

public class NineCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("9")
            .executes(context -> compareNineToEight(context.getSource()))
        );

    }

    private static int compareNineToEight(ChatCommandSource source) {

        source.sendSuccess("8 is, in fact, less than 9. You are correct!");
        return 1;

    }

}
