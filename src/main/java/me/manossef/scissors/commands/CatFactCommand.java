package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.commands.core.CommandSource;
import me.manossef.scissors.commands.core.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

public class CatFactCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher, List<SlashCommandData> slashCommands) {
        String baseLiteral = "catfact";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> getCatFact(context.getSource()))
        );
        String description = "Replies with a random fact about cats.";
        HelpCommand.addLine(baseLiteral, s -> description);
        HelpCommand.addLiteral(baseLiteral, s -> "Replies with a random fact about cats. Facts are sourced from https://catfact.ninja/fact.");
        slashCommands.add(slash(baseLiteral, description));
        Commands.registerSlashCommand(baseLiteral, CatFactCommand::getCatFact);
    }

    private static int getCatFact(CommandSource source) throws CommandSyntaxException {
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