package me.manossef.scissors.commands.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.Guild;

public class LeaveCommand {
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Not in that guild"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("leave")
            .requires(Commands.devRestricted())
            .executes(context -> leave(context.getSource()))
            .then(Commands.argument("guildId", LongArgumentType.longArg())
                .executes(context -> leave(context.getSource(), LongArgumentType.getLong(context, "guildId")))
            )
        );
    }

    private static int leave(ChatCommandSource source) {
        source.sendSuccess("Leaving guild", true);
        source.commandMessage().getGuild().leave().queue();
        return 1;
    }

    private static int leave(ChatCommandSource source, long id) throws CommandSyntaxException {
        Guild guild = Scissors.DISCORD_API.getGuildById(id);
        if(guild == null) throw NOT_IN_GUILD.create();
        source.sendSuccess("Leaving " + guild.getName(), true);
        guild.leave().queue();
        return 1;
    }
}