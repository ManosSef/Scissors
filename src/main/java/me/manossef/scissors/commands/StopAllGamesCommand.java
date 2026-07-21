package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.games.Game;
import me.manossef.scissors.puzzles.Puzzle;

public class StopAllGamesCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("stopallgames")
            .requires(Commands.devRestricted())
            .executes(context -> stopAllGames(context.getSource()))
        );
    }

    private static int stopAllGames(ChatCommandSource source) {
        int stopped = 0;
        for(Object listener : Scissors.DISCORD_API.getRegisteredListeners()) {
            if(listener instanceof Game game) {
                game.end();
                stopped++;
            } else if(listener instanceof Puzzle puzzle) {
                puzzle.end();
                stopped++;
            }
        }
        source.sendSuccess("Stopped all running games");
        return stopped;
    }
}