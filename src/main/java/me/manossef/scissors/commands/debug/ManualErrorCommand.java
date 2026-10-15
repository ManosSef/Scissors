package me.manossef.scissors.commands.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.manossef.scissors.commands.core.CommandSource;
import me.manossef.scissors.commands.core.Commands;

public class ManualErrorCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("manualerror")
            .executes(context -> recurse(50))
            .then(Commands.argument("times", IntegerArgumentType.integer())
                .executes(context -> recurse(IntegerArgumentType.getInteger(context, "times")))
            )
        );
    }

    @SuppressWarnings("InfiniteRecursion")
    private static int recurse(int times) {
        if(times <= 0)
            throw new RuntimeException("Manually triggered exception");
        return recurse(times - 1);
    }
}