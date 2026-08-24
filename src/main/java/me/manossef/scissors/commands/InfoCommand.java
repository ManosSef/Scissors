package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Messages;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.Options;
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.Set;
import java.util.TreeSet;

import static net.dv8tion.jda.api.utils.MarkdownUtil.italics;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class InfoCommand {
    private static final SimpleCommandExceptionType IMPOSSIBLE_ERROR = new SimpleCommandExceptionType(new LiteralMessage("This error should not have happened! Options should have been bootstrapped by now!"));
    private static final Set<String> OPTIONS = new TreeSet<>();

    public static void addOption(Option<?> option, String line) {
        OPTIONS.add(monospace(option.getName()) + ": " + line);
    }

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
        HelpCommand.addLine(baseLiteral, s -> "Provides information about the bot.");
        HelpCommand.addLiteral(baseLiteral, source -> {
            Channel channel = source.commandMessage().getChannel();
            return String.format("""
                    Provides information about the bot.
                    
                    Here are the available syntaxes for this command:
                    - %s: Makes the bot introduce itself.
                    - %s: Posts the invite link to the bot's development server.
                    - %s: Posts the link to the bot's GitHub repository.""",
                Commands.format(baseLiteral, channel),
                Commands.format(baseLiteral + " devserver", channel),
                Commands.format(baseLiteral + " github", channel));
        });
    }

    private static int sendGenericInfo(ChatCommandSource source) {
        Channel channel = source.commandMessage().getChannel();
        source.sendSuccess(String.format("""
                Hi! I'm a Discord bot that can do one or two things. Mostly cutting paper.
                
                I was created for a server called Chess Rock Community, a server full of amazing people my developer has interacted with for quite a long time and made a lot of inside jokes with. A few of them are integrated into me, \
                so if you don't get them you either haven't been keeping up or have no idea what I'm talking about.
                
                I was created by %s. If this says "unknown user" for you, %s
                
                If you'd like to know what I can do, type %s. You can also type %s if you find something wrong with me, %s if you'd like to suggest a new feature for me, or %s if you have an idea for improving me.""",
            Messages.MY_MENTION,
            italics("how did I get here?"),
            Commands.format("help", channel),
            Commands.format("suggest bug <summary>", channel),
            Commands.format("suggest feature <summary>", channel),
            Commands.format("suggest improvement <summary>", channel)), false);
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

    private static int sendOptions(ChatCommandSource source) throws CommandSyntaxException {
        if(Options.values().length != OPTIONS.size()) throw IMPOSSIBLE_ERROR.create();
        StringBuilder builder = new StringBuilder("Here are all my configuration options, which can be queried or edited using " + Commands.format("config", source.commandMessage().getChannel()) + ":");
        for(String line : OPTIONS)
            builder.append("\n- ").append(line);
        source.sendSuccess(builder.toString(), false);
        return OPTIONS.size();
    }
}