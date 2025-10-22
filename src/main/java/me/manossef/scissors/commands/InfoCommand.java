package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;

public class InfoCommand {

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("info")
            .executes(context -> sendGenericInfo(context.getSource()))
            .then(Commands.literal("devserver")
                .executes(context -> sendDevServer(context.getSource()))
            )
            .then(Commands.literal("github")
                .executes(context -> sendGithub(context.getSource()))
            )
        );

    }

    private static int sendGenericInfo(ChatCommandSource source) {

        source.sendSuccess(String.format("""
                Hi! I'm a Discord bot that can do one or two things. Mostly cutting paper.
                
                I was created for a server called Chess Rock Community, a server full of amazing people my developer has interacted with for quite a long time and made a lot of inside jokes with. A few of them are integrated into me, so if you don't get them you either haven't been keeping up or have no idea what I'm talking about.
                
                I was created by <@%1$s>. If this says "unknown user" for you, *how did I get here?*
                
                If you'd like to know what I can do, type `%2$shelp`. You can also type `%2$ssuggest bug <summary>` if you find something wrong with me, `%2$ssuggest feature <summary>` if you'd like to suggest a new feature for me, or `%2$ssuggest improvement <summary>` if you have an idea for improving me.""",
            SharedConstants.MY_USER_ID, SharedConstants.COMMAND_PREFIX));
        return 1;

    }

    private static int sendDevServer(ChatCommandSource source) {

        source.sendSuccess("My development server can be joined at https://discord.gg/FjRTdwBdM8");
        return 1;

    }

    private static int sendGithub(ChatCommandSource source) {

        source.sendSuccess("The GitHub repository where my code is hosted can be found at https://github.com/ManosSef/Scissors");
        return 1;

    }

}
