package me.manossef.scissors.commands.debug;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.commands.core.CommandSource;
import me.manossef.scissors.commands.core.Commands;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

public class ListGuildsCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("listguilds")
            .requires(Commands.devRestricted())
            .executes(context -> listGuilds(context.getSource()))
        );
    }

    private static int listGuilds(CommandSource source) {
        StringBuilder builder = new StringBuilder();
        List<Guild> guilds = Scissors.DISCORD_API.getGuilds();
        for(Guild guild : guilds)
            builder.append(guild.toString()).append("\n");
        source.sendSuccess(builder.toString(), false);
        return guilds.size();
    }
}