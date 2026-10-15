package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CatFactCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "catfact";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> getCatFact(context.getSource()))
        );
        HelpCommand.addLine(baseLiteral, s -> "Replies with a random fact about cats.");
        HelpCommand.addLiteral(baseLiteral, s -> "Replies with a random fact about cats. Facts are sourced from https://catfact.ninja/fact.");
    }

    private static int getCatFact(ChatCommandSource source) throws CommandSyntaxException {
        Request request = new Request.Builder().url("https://catfact.ninja/fact").build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            String body = response.body().string();
            CatFact catFact = Scissors.GSON.fromJson(body, CatFact.class);
            source.sendSuccess(catFact.fact, false);
            return catFact.length;
        } catch(IOException e) {
            throw Commands.IO_EXCEPTION.create();
        }
    }

    private record CatFact(String fact, int length) {
    }
}