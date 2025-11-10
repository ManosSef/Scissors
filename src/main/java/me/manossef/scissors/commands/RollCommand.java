package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class RollCommand {

    private static final SimpleCommandExceptionType MAX_LESS_THAN_1 = new SimpleCommandExceptionType(new LiteralMessage("The maximum value cannot be less than 1"));
    private static final SimpleCommandExceptionType MAX_LESS_THAN_MIN = new SimpleCommandExceptionType(new LiteralMessage("The maximum value cannot be less than the minimum value"));

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
            .then(Commands.argument("maxFloat", FloatArgumentType.floatArg())
                .executes(context -> roll(context.getSource(), context.getArgument("maxFloat", Float.class)))
            )
            .then(Commands.argument("minFloat", FloatArgumentType.floatArg())
                .then(Commands.argument("maxFloat", FloatArgumentType.floatArg())
                    .executes(context -> roll(context.getSource(), context.getArgument("minFloat", Float.class), context.getArgument("maxFloat", Float.class)))
                )
            )
            .then(Commands.literal("double")
                .then(Commands.argument("maxDouble", DoubleArgumentType.doubleArg())
                    .executes(context -> roll(context.getSource(), context.getArgument("maxDouble", Double.class)))
                )
                .then(Commands.argument("minDouble", DoubleArgumentType.doubleArg())
                    .then(Commands.argument("maxDouble", DoubleArgumentType.doubleArg())
                        .executes(context -> roll(context.getSource(), context.getArgument("minDouble", Double.class), context.getArgument("maxDouble", Double.class)))
                    )
                )
            )
        );

    }

    private static int roll(ChatCommandSource source, int max) throws CommandSyntaxException {

        if(max < 1) throw MAX_LESS_THAN_1.create();
        return roll(source, 1, max);

    }

    private static int roll(ChatCommandSource source, int min, int max) throws CommandSyntaxException {

        if(max < min) throw MAX_LESS_THAN_MIN.create();
        int random = Scissors.RANDOM.nextInt(min, max + 1);
        source.sendSuccess("You rolled " + random);
        return random;

    }

    private static int roll(ChatCommandSource source, float max) throws CommandSyntaxException {

        if(max < 1) throw MAX_LESS_THAN_1.create();
        return roll(source, 1, max);

    }

    private static int roll(ChatCommandSource source, float min, float max) throws CommandSyntaxException {

        if(max < min) throw MAX_LESS_THAN_MIN.create();
        float random = Scissors.RANDOM.nextFloat(min, max);
        source.sendSuccess("You rolled " + random);
        return (int) random;

    }

    private static int roll(ChatCommandSource source, double max) throws CommandSyntaxException {

        if(max < 1) throw MAX_LESS_THAN_1.create();
        return roll(source, 1, max);

    }

    private static int roll(ChatCommandSource source, double min, double max) throws CommandSyntaxException {

        if(max < min) throw MAX_LESS_THAN_MIN.create();
        double random = Scissors.RANDOM.nextDouble(min, max);
        source.sendSuccess("You rolled " + random);
        return (int) random;

    }

}
