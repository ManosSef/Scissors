package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.squaredle.PuzzleData;
import me.manossef.scissors.squaredle.PuzzleUtil;
import me.manossef.scissors.squaredle.TodayConfig;
import me.manossef.scissors.squaredle.TodayConfigReader;

public class SquaredleCommand {

    private static final SimpleCommandExceptionType CONFIG_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("Failed to get the daily Squaredle puzzle configuration"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("squaredle")
            .executes(context -> sendDailySquaredle(context.getSource(), false))
            .then(Commands.literal("xp")
                .executes(context -> sendDailySquaredle(context.getSource(), true))
            )
        );

    }

    private static int sendDailySquaredle(ChatCommandSource source, boolean xp) throws CommandSyntaxException {

        TodayConfig todayConfig = TodayConfigReader.readTodayPuzzleConfig();
        if(todayConfig == null) throw CONFIG_NOT_FOUND.create();
        String date = todayConfig.currentDate();
        PuzzleData puzzle = todayConfig.puzzles().get(date + (xp ? "-xp" : ""));
        source.sendSuccess("**Daily " + (xp ? "xp " : "") + date.replace("/", "-") + "\n" + PuzzleUtil.getMessageText(puzzle));
        return 1;

    }

}
