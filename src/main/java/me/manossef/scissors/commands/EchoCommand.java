package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import net.dv8tion.jda.api.entities.Message;

public class EchoCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("echo")
            .then(Commands.argument("text", StringArgumentType.greedyString())
                .executes(context -> echo(context.getSource(), context.getArgument("text", String.class)))
            )
        );

    }

    private static int echo(ChatCommandSource source, String message) {

        Message commandMessage = source.commandMessage();
        commandMessage.delete().queue();
        commandMessage.getChannel().sendMessage(message).queue();
        return 1;

    }

}
