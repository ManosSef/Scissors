package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import kong.unirest.core.UnirestException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.squaredle.PuzzleData;
import me.manossef.scissors.squaredle.PuzzleUtil;
import me.manossef.scissors.squaredle.TodayConfig;
import me.manossef.scissors.squaredle.TodayConfigReader;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class SquaredleCommand {
    private static final SimpleCommandExceptionType CONFIG_NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("Failed to get the daily Squaredle puzzle configuration"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "squaredle";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> sendDailySquaredle(context.getSource(), false))
            .then(Commands.literal("xp")
                .executes(context -> sendDailySquaredle(context.getSource(), true))
            )
        );
        HelpCommand.addLine(baseLiteral, "Displays information about today's Squaredle puzzle.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Provides information about today's Squaredle (https://squaredle.app/) puzzle. Displays the grid with emoji, and reports the word count (including the number of words of each length), the bonus word count, a hint for the \
                Bonus Word of the Day (the same as in-game), the difficulty, and the author.
                
                Here are the available syntaxes for this command:
                - %s: Provides information about today's Squaredle puzzle.
                - %s: Provides information about today's Squaredle Express puzzle.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " xp")));
    }

    private static int sendDailySquaredle(ChatCommandSource source, boolean xp) throws CommandSyntaxException {
        try {
            TodayConfig todayConfig = TodayConfigReader.readTodayPuzzleConfig();
            if(todayConfig == null) throw CONFIG_NOT_FOUND.create();
            String date = todayConfig.currentDate();
            PuzzleData puzzle = todayConfig.puzzles().get(date + (xp ? "-xp" : ""));
            source.sendSuccess(bold("Daily " + (xp ? "xp " : "") + date.replace("/", "-")) + "\n" + PuzzleUtil.getMessageText(puzzle));
            return 1;
        } catch(UnirestException e) {
            throw Commands.IO_EXCEPTION.create();
        }
    }
}
