package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class RollCommand {
    private static final SimpleCommandExceptionType MAX_LESS_THAN_1 = new SimpleCommandExceptionType(new LiteralMessage("The maximum value cannot be less than 1"));
    private static final SimpleCommandExceptionType MAX_LESS_THAN_MIN = new SimpleCommandExceptionType(new LiteralMessage("The maximum value cannot be less than the minimum value"));
    private static final SimpleCommandExceptionType MAX_FLOAT_LESS_THAN_0 = new SimpleCommandExceptionType(new LiteralMessage("The maximum value must be greater than 0"));
    private static final SimpleCommandExceptionType MAX_FLOAT_LESS_THAN_MIN = new SimpleCommandExceptionType(new LiteralMessage("The maximum value must be greater than the minimum value"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "roll";
        dispatcher.register(Commands.literal(baseLiteral)
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
            .then(Commands.literal("long")
                .then(Commands.argument("maxLong", LongArgumentType.longArg())
                    .executes(context -> roll(context.getSource(), context.getArgument("maxLong", Long.class)))
                )
                .then(Commands.argument("minLong", LongArgumentType.longArg())
                    .then(Commands.argument("maxLong", LongArgumentType.longArg())
                        .executes(context -> roll(context.getSource(), context.getArgument("minLong", Long.class), context.getArgument("maxLong", Long.class)))
                    )
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
        HelpCommand.addLine(baseLiteral, "Rolls a random number from a range.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Rolls a random number from a specific range.
                
                Here are the available syntaxes for this command:
                - %3$s: If %2$s is an integer (from 1 to %9$s), rolls an integer from 1 to %2$s (inclusive). Otherwise, rolls a decimal number from 0 to %2$s with at most 9 decimal digits.
                - %4$s: Rolls a number from %1$s to %2$s. If both %1$s and %2$s are integers (from %10$s to %9$s), only rolls integers. Otherwise, rolls any decimal number in the range with at most 9 decimal digits.
                - %7$s: Rolls a long integer from 1 to %2$s (inclusive). %2$s must be at most %11$s.
                - %8$s: Rolls a long integer from %1$s to %2$s. %1$s and %2$s must be between %12$s and %11$s (inclusive).
                - %5$s: Rolls a decimal number from 0 to %2$s with at most 17 decimal digits.
                - %6$s: Rolls a decimal number from %1$s to %2$s with at most 17 decimal digits.""",
            monospace("<min>"),
            monospace("<max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " <max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " <min> <max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " double <max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " double <min> <max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " long <max>"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " long <min> <max>"),
            Integer.MAX_VALUE,
            Integer.MIN_VALUE,
            Long.MAX_VALUE,
            Long.MIN_VALUE));
    }

    private static int roll(ChatCommandSource source, int max) throws CommandSyntaxException {
        if(max < 1) throw MAX_LESS_THAN_1.create();
        return roll(source, 1, max);
    }

    private static int roll(ChatCommandSource source, int min, int max) throws CommandSyntaxException {
        if(max < min) throw MAX_LESS_THAN_MIN.create();
        int random = Scissors.RANDOM.nextInt(min, max + 1);
        source.sendSuccess("You rolled " + bold(String.valueOf(random)));
        return random;
    }

    private static int roll(ChatCommandSource source, long max) throws CommandSyntaxException {
        if(max < 1) throw MAX_LESS_THAN_1.create();
        return roll(source, 1, max);
    }

    private static int roll(ChatCommandSource source, long min, long max) throws CommandSyntaxException {
        if(max < min) throw MAX_LESS_THAN_MIN.create();
        long random = Scissors.RANDOM.nextLong(min, max + 1);
        source.sendSuccess("You rolled " + bold(String.valueOf(random)));
        return (int) random;
    }

    private static int roll(ChatCommandSource source, float max) throws CommandSyntaxException {
        if(max <= 0) throw MAX_FLOAT_LESS_THAN_0.create();
        return roll(source, 0, max);
    }

    private static int roll(ChatCommandSource source, float min, float max) throws CommandSyntaxException {
        if(max <= min) throw MAX_FLOAT_LESS_THAN_MIN.create();
        float random = Scissors.RANDOM.nextFloat(min, max);
        source.sendSuccess("You rolled " + bold(String.valueOf(random)));
        return (int) random;
    }

    private static int roll(ChatCommandSource source, double max) throws CommandSyntaxException {
        if(max <= 0) throw MAX_FLOAT_LESS_THAN_0.create();
        return roll(source, 0, max);
    }

    private static int roll(ChatCommandSource source, double min, double max) throws CommandSyntaxException {
        if(max <= min) throw MAX_FLOAT_LESS_THAN_MIN.create();
        double random = Scissors.RANDOM.nextDouble(min, max);
        source.sendSuccess("You rolled " + bold(String.valueOf(random)));
        return (int) random;
    }
}