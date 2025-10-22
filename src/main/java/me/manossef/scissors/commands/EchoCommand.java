package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class EchoCommand {

    private static final SimpleCommandExceptionType CANNOT_DELETE = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete this type of message"));
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in channels outside servers"));
    private static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(new LiteralMessage("Cannot delete messages in this channel; no permission"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("echo")
            .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> echo(context.getSource(), context.getArgument("text", String.class)))
            )
        );

    }

    private static int echo(ChatCommandSource source, String message) throws CommandSyntaxException {

        Message commandMessage = source.commandMessage();
        if(!commandMessage.getType().canDelete()) throw CANNOT_DELETE.create();
        MessageChannelUnion channel = commandMessage.getChannel();
        if(!(channel instanceof GuildChannel)) throw NOT_IN_GUILD.create();
        try {

            commandMessage.delete().queue();

        } catch(InsufficientPermissionException e) {

            throw NO_PERMISSION.create();

        }
        channel.sendMessage(message).queue();
        return 1;

    }

}
