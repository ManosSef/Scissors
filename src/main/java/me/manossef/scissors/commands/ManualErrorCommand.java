package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;

public class ManualErrorCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("manualerror")
            .executes(context -> recurse(50))
            .then(Commands.argument("times", IntegerArgumentType.integer())
                .executes(context -> recurse(IntegerArgumentType.getInteger(context, "times")))
            )
        );
    }

    private static int recurse(int times) {
        if(times <= 0)
            throw new RuntimeException("Manually triggered exception");
        return recurse(times - 1);
    }
}
