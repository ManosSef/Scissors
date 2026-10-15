package me.manossef.scissors.commands.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.List;

public class ListChannelsCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("listchannels")
            .requires(Commands.devRestricted())
            .then(Commands.argument("guild", LongArgumentType.longArg())
                .executes(context -> listChannels(context.getSource(), LongArgumentType.getLong(context, "guild")))
            )
        );
    }

    private static int listChannels(ChatCommandSource source, long guildId) throws CommandSyntaxException {
        Guild guild = Scissors.DISCORD_API.getGuildById(guildId);
        if(guild == null) throw Commands.GUILD_NOT_FOUND.create();
        StringBuilder builder = new StringBuilder();
        List<GuildChannel> channels = guild.getChannels(true);
        for(GuildChannel channel : channels) builder.append(channel.toString()).append("\n");
        source.sendSuccess(builder.toString(), false);
        return channels.size();
    }
}
