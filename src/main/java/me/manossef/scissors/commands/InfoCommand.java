package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;

import static net.dv8tion.jda.api.utils.MarkdownUtil.italics;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class InfoCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "info";
        dispatcher.register(Commands.literal(baseLiteral)
            .executes(context -> sendGenericInfo(context.getSource()))
            .then(Commands.literal("devserver")
                .executes(context -> sendDevServer(context.getSource()))
            )
            .then(Commands.literal("github")
                .executes(context -> sendGithub(context.getSource()))
            )
        );
        HelpCommand.addLine(baseLiteral, "Provides information about the bot.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Provides information about the bot.
                
                Here are the available syntaxes for this command:
                - %s: Makes the bot introduce itself.
                - %s: Posts the invite link to the bot's development server.
                - %s: Posts the link to the bot's GitHub repository.""",
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " devserver"),
            monospace(SharedConstants.COMMAND_PREFIX + baseLiteral + " github")));
    }

    private static int sendGenericInfo(ChatCommandSource source) {
        source.sendSuccess(String.format("""
                Hi! I'm a Discord bot that can do one or two things. Mostly cutting paper.
                
                I was created for a server called Chess Rock Community, a server full of amazing people my developer has interacted with for quite a long time and made a lot of inside jokes with. A few of them are integrated into me, \
                so if you don't get them you either haven't been keeping up or have no idea what I'm talking about.
                
                I was created by <@%s>. If this says "unknown user" for you, %s
                
                If you'd like to know what I can do, type %s. You can also type %s if you find something wrong with me, %s if you'd like to suggest a new feature for me, or %s if you have an idea for improving me.""",
            SharedConstants.MY_USER_ID,
            italics("how did I get here?"),
            monospace(SharedConstants.COMMAND_PREFIX + "help"),
            monospace(SharedConstants.COMMAND_PREFIX + "suggest bug <summary>"),
            monospace(SharedConstants.COMMAND_PREFIX + "suggest feature <summary>"),
            monospace(SharedConstants.COMMAND_PREFIX + "suggest improvement <summary>")));
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
