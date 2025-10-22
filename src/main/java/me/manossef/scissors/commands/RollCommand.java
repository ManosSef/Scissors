package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class RollCommand {

    private static final SimpleCommandExceptionType MAX_LESS_THAN_MIN = new SimpleCommandExceptionType(new LiteralMessage("The maximum value cannot be less than the minimum value"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("roll")
            .then(Commands.argument("max", IntegerArgumentType.integer(1))
                .executes(context -> roll(context.getSource(), context.getArgument("max", Integer.class)))
            )
            .then(Commands.argument("min", IntegerArgumentType.integer())
                .then(Commands.argument("max", IntegerArgumentType.integer())
                    .executes(context -> roll(context.getSource(), context.getArgument("min", Integer.class), context.getArgument("max", Integer.class)))
                )
            )
        );

    }

    private static int roll(ChatCommandSource source, int max) throws CommandSyntaxException {

        return roll(source, 1, max);

    }

    private static int roll(ChatCommandSource source, int min, int max) throws CommandSyntaxException {

        if(max < min) throw MAX_LESS_THAN_MIN.create();
        int random = Scissors.RANDOM.nextInt(min, max + 1);
        source.sendSuccess("You rolled " + random);
        return random;

    }

}
