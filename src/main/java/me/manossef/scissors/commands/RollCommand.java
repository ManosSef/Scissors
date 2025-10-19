package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class RollCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("roll")
            .then(Commands.argument("max", IntegerArgumentType.integer())
                .executes(context -> roll(context.getSource(), context.getArgument("max", Integer.class)))
            )
            .then(Commands.argument("min", IntegerArgumentType.integer())
                .then(Commands.argument("max", IntegerArgumentType.integer())
                    .executes(context -> roll(context.getSource(), context.getArgument("min", Integer.class), context.getArgument("max", Integer.class)))
                )
            )
        );

    }

    private static int roll(ChatCommandSource source, int max) {

        return roll(source, 1, max);

    }

    private static int roll(ChatCommandSource source, int min, int max) {

        int random = Scissors.RANDOM.nextInt(min, max + 1);
        source.sendSuccess("You rolled " + random);
        return random;

    }

}
