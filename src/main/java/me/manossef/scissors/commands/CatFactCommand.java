package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import kong.unirest.core.Unirest;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class CatFactCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("catfact")
            .executes(context -> getCatFact(context.getSource()))
        );

    }

    private static int getCatFact(ChatCommandSource source) {

        String body = Unirest.get("https://catfact.ninja/fact").asString().getBody();
        CatFact catFact = Scissors.GSON.fromJson(body, CatFact.class);
        source.sendSuccess(catFact.fact);
        return 1;

    }

    private record CatFact(String fact, int length) {
    }

}
