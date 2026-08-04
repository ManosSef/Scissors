package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;

public class CatFactCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "catfact";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> getCatFact(context.getSource()))
        );
        HelpCommand.addLine(baseLiteral, "Replies with a random fact about cats.");
        HelpCommand.addLiteral(baseLiteral, "Replies with a random fact about cats. Facts are sourced from https://catfact.ninja/fact.");
    }

    private static int getCatFact(ChatCommandSource source) throws CommandSyntaxException {
        try {
            String body = Unirest.get("https://catfact.ninja/fact").asString().getBody();
            CatFact catFact = Scissors.GSON.fromJson(body, CatFact.class);
            source.sendSuccess(catFact.fact, false);
            return catFact.length;
        } catch(UnirestException e) {
            throw Commands.IO_EXCEPTION.create();
        }
    }

    private record CatFact(String fact, int length) {
    }
}