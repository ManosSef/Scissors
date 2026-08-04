package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.config.Options;

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
            .then(Commands.literal("options")
                .executes(context -> sendOptions(context.getSource()))
            )
        );
        HelpCommand.addLine(baseLiteral, "Provides information about the bot.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Provides information about the bot.
                
                Here are the available syntaxes for this command:
                - %s: Makes the bot introduce itself.
                - %s: Posts the invite link to the bot's development server.
                - %s: Posts the link to the bot's GitHub repository.""",
            Commands.format(baseLiteral),
            Commands.format(baseLiteral + " devserver"),
            Commands.format(baseLiteral + " github")));
    }

    private static int sendGenericInfo(ChatCommandSource source) {
        source.sendSuccess(String.format("""
                Hi! I'm a Discord bot that can do one or two things. Mostly cutting paper.
                
                I was created for a server called Chess Rock Community, a server full of amazing people my developer has interacted with for quite a long time and made a lot of inside jokes with. A few of them are integrated into me, \
                so if you don't get them you either haven't been keeping up or have no idea what I'm talking about.
                
                I was created by %s. If this says "unknown user" for you, %s
                
                If you'd like to know what I can do, type %s. You can also type %s if you find something wrong with me, %s if you'd like to suggest a new feature for me, or %s if you have an idea for improving me.""",
            SharedConstants.MY_MENTION,
            italics("how did I get here?"),
            Commands.format("help"),
            Commands.format("suggest bug <summary>"),
            Commands.format("suggest feature <summary>"),
            Commands.format("suggest improvement <summary>")), false);
        return 1;
    }

    private static int sendDevServer(ChatCommandSource source) {
        source.sendSuccess("My development server can be joined at https://discord.gg/FjRTdwBdM8", false);
        return 1;
    }

    private static int sendGithub(ChatCommandSource source) {
        source.sendSuccess("The GitHub repository where my code is hosted can be found at https://github.com/ManosSef/Scissors", false);
        return 1;
    }

    private static int sendOptions(ChatCommandSource source) {
        source.sendSuccess(String.format("""
                Here are all my configuration options, which can be queried or edited using %3$s:
                - %4$s: Whether counting responses are posted. The value is either %1$s or %2$s.
                - %5$s: The chance (from 0 to 100) that a response to each new message with only a number is posted.
                - %6$s: Whether responses to pings are posted. The value is either %1$s or %2$s.
                - %7$s: Whether responses to mentions of scissors are posted. The value is either %1$s or %2$s.
                - %8$s: The chance (from 0 to 100) that a response to each new message with a mention of scissors is posted.
                - %9$s: Whether the bot reacts to mentions of paper with the scissors emoji. The value is either %1$s or %2$s.
                - %10$s: Whether counting responses are posted for integers only (instead of all real numbers). The value is either %1$s or %2$s.""",
            monospace("true"),
            monospace("false"),
            Commands.format("config"),
            monospace(Options.GPPCT_RESPONSES.getName()),
            monospace(Options.GPPCT_RESPONSE_CHANCE.getName()),
            monospace(Options.PING_RESPONSES.getName()),
            monospace(Options.SCISSORS_RESPONSES.getName()),
            monospace(Options.SCISSORS_RESPONSE_CHANCE.getName()),
            monospace(Options.REACT_TO_PAPER.getName()),
            monospace(Options.GPPCT_ON_INTEGERS_ONLY.getName())), false);
        return 1;
    }
}