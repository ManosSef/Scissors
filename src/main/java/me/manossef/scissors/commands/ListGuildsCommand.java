package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class ListGuildsCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("listguilds")
            .requires(Commands.devRestricted())
            .executes(context -> listGuilds(context.getSource()))
        );

    }

    private static int listGuilds(ChatCommandSource source) {

        StringBuilder builder = new StringBuilder();
        List<Guild> guilds = Scissors.DISCORD_API.getGuilds();
        for(Guild guild : guilds)
            builder.append(guild.toString()).append("\n");
        source.sendSuccess(builder.toString());
        return guilds.size();

    }

}
