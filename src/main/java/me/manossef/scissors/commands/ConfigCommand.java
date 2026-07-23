package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.OptionProperties;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class ConfigCommand {
    private static final SimpleCommandExceptionType INVALID_CONTEXT = new SimpleCommandExceptionType(new LiteralMessage("Invalid option context"));
    private static final SimpleCommandExceptionType NOT_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("This channel is not in a server"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_GLOBAL = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; the global values of all options are already the default ones"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_GUILD = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; no explicit values for any option have been set for this server"));
    private static final SimpleCommandExceptionType ALREADY_DEFAULT_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage("Nothing changed; no explicit values for any option have been set for this channel"));
    private static final SimpleCommandExceptionType IMPOSSIBLE_ERROR = new SimpleCommandExceptionType(new LiteralMessage("This error should not have happened! Discord must be freaking out!"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_GUILD = new SimpleCommandExceptionType(new LiteralMessage("You need to have the \"Manage Server\" or \"Administrator\" permission to edit the bot's options for this server"));
    private static final SimpleCommandExceptionType NO_PERMS_IN_CHANNEL = new SimpleCommandExceptionType(new LiteralMessage("You need to have the \"Manage Channel\" or \"Administrator\" permission to edit the bot's options for this channel"));
    private static final DynamicCommandExceptionType NO_EXPLICIT_VALUE_IN_GUILD = new DynamicCommandExceptionType(option -> new LiteralMessage("No explicit value for the option " + option + " has been set for this server"));
    private static final DynamicCommandExceptionType NO_EXPLICIT_VALUE_IN_CHANNEL = new DynamicCommandExceptionType(option -> new LiteralMessage("No explicit value for the option " + option + " has been set for this channel"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "config";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(optionsArguments(Commands.literal("global"), OptionContext.GLOBAL).requires(Commands.devRestricted()))
            .then(optionsArguments(Commands.literal("server"), OptionContext.PER_GUILD))
            .then(optionsArguments(Commands.literal("channel"), OptionContext.PER_CHANNEL))
        );
        HelpCommand.addLine(baseLiteral, "Queries or edits the bot's settings.");
        HelpCommand.addLiteral(baseLiteral, String.format("""
                Queries or edits the bot's configuration options.
                
                Option values can apply either to a whole server or to a specific channel. The effective value of an option for a channel is the value explicitly applied to it if any, or the value applied to the server if any, or the default value. \
                Option values persist across restarts of the bot.
                
                Here are all available syntaxes for this command:
                - %3$s: Returns the effective value of the specified option for the server/channel the command was run in.
                - %4$s: Sets the value of the specified option for the server/channel the command was run in to the specified value.
                - %5$s: Resets the values of all options for the server/channel the command was run in to the default ones.
                - %6$s: Returns the explicitly applied value of the specified option for the server/channel the command was run in. Fails if this option has not been given an explicit value for this server/channel.
                
                Use %7$s as the first argument to affect the server, and %8$s to affect the channel the command was run in.
                
                %9$s commands always fail if run in a DM.
                
                You need the "Manage Server" permission to run %9$s commands, and the "Manage Channel" permission in the respective channel to run %10$s commands.
                
                Here are all available options:
                - %11$s: Whether counting responses are posted. The value is either %1$s or %2$s.
                - %12$s: The chance (from 0 to 100) that a response to each new message with only a number is posted.
                - %13$s: Whether responses to pings are posted. The value is either %1$s or %2$s.
                - %14$s: Whether responses to mentions of scissors are posted. The value is either %1$s or %2$s.
                - %15$s: The chance (from 0 to 100) that a response to each new message with a mention of scissors is posted.
                - %16$s: Whether the bot reacts to mentions of paper with the scissors emoji. The value is either %1$s or %2$s.
                - %17$s: Whether counting responses are posted for integers only (instead of all real numbers). The value is either %1$s or %2$s.""",
            monospace("true"),
            monospace("false"),
            Commands.format(baseLiteral + " (server|channel) <option>"),
            Commands.format(baseLiteral + " (server|channel) <option> <value>"),
            Commands.format(baseLiteral + " (server|channel) reset"),
            Commands.format(baseLiteral + " (server|channel) explicit <option>"),
            monospace("server"),
            monospace("channel"),
            Commands.format(baseLiteral + " server ..."),
            Commands.format(baseLiteral + " channel ..."),
            monospace(Option.GPPCT_RESPONSES.properties().getName()),
            monospace(Option.GPPCT_RESPONSE_CHANCE.properties().getName()),
            monospace(Option.PING_RESPONSES.properties().getName()),
            monospace(Option.SCISSORS_RESPONSES.properties().getName()),
            monospace(Option.SCISSORS_RESPONSE_CHANCE.properties().getName()),
            monospace(Option.REACT_TO_PAPER.properties().getName()),
            monospace(Option.GPPCT_ON_INTEGERS_ONLY.properties().getName())));
    }

    private static ArgumentBuilder<ChatCommandSource, ?> optionsArguments(ArgumentBuilder<ChatCommandSource, ?> argument, OptionContext optionContext) {
        argument.then(Commands.literal("reset")
            .executes(context -> resetOptions(context.getSource(), optionContext))
        );
        ArgumentBuilder<ChatCommandSource, ?> onlyArgument = Commands.literal("explicit");
        for(Option option : Option.values()) {
            ArgumentBuilder<ChatCommandSource, ?> optionArgument = Commands.literal(option.properties().getName())
                .executes(context -> getOptionValue(context.getSource(), option, option.properties().getType(), optionContext, true));
            if(option.isBoolean())
                optionArgument.then(Commands.argument("value", BoolArgumentType.bool())
                    .executes(context -> setOptionValue(context.getSource(), option, BoolArgumentType.getBool(context, "value"), optionContext))
                );
            if(option.isInteger()) {
                OptionProperties.IntOptionProperties intOption = (OptionProperties.IntOptionProperties) option.properties();
                optionArgument.then(Commands.argument("value", IntegerArgumentType.integer(intOption.getMin(), intOption.getMax()))
                    .executes(context -> setOptionValue(context.getSource(), option, IntegerArgumentType.getInteger(context, "value"), optionContext))
                );
            }
            argument.then(optionArgument);
            onlyArgument.then(Commands.literal(option.properties().getName())
                .executes(context -> getOptionValue(context.getSource(), option, option.properties().getType(), optionContext, false))
            );
        }
        argument.then(onlyArgument);
        return argument;
    }

    private static int resetOptions(ChatCommandSource source, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext) {
            case GLOBAL -> {
                if(Scissors.getConfiguration().resetGlobal())
                    source.sendSuccess("Reset the global values of all options to the default ones");
                else
                    throw ALREADY_DEFAULT_GLOBAL.create();
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(!canEditPerGuild(source.user(), guildChannel.getGuild()))
                    throw NO_PERMS_IN_GUILD.create();
                if(Scissors.getConfiguration().resetForGuild(source.commandMessage().getGuild()))
                    source.sendSuccess("Removed the explicit values of all options for this server");
                else
                    throw ALREADY_DEFAULT_GUILD.create();
            }
            case PER_CHANNEL -> {
                if(!canEditPerChannel(source.user(), source.commandMessage().getChannel()))
                    throw NO_PERMS_IN_CHANNEL.create();
                if(Scissors.getConfiguration().resetForChannel(source.commandMessage().getChannel()))
                    source.sendSuccess("Removed the explicit values of all options for this channel");
                else
                    throw ALREADY_DEFAULT_CHANNEL.create();
            }
        }
        Scissors.saveConfiguration();
        return 1;
    }

    private static <T> int getOptionValue(ChatCommandSource source, Option option, Class<T> type, OptionContext optionContext, boolean defaultToHigherPower) throws CommandSyntaxException {
        T value;
        switch(optionContext) {
            case GLOBAL -> {
                value = Scissors.getConfiguration().getGlobalOption(option, type);
                source.sendSuccess("The current global value of the option " + monospace(option.properties().getName()) + " is " + bold(value.toString()));
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel))
                    throw NOT_IN_GUILD.create();
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForGuild(option, type, source.commandMessage().getGuild());
                    source.sendSuccess("The current effective value of the option " + monospace(option.properties().getName()) + " for this server is " + bold(value.toString()));
                } else {
                    value = Scissors.getConfiguration().getOptionForGuildOnly(option, type, source.commandMessage().getGuild());
                    if(value == null) throw NO_EXPLICIT_VALUE_IN_GUILD.create(monospace(option.properties().getName()));
                    source.sendSuccess("The current explicit value of the option " + monospace(option.properties().getName()) + " for this server is " + bold(value.toString()));
                }
            }
            case PER_CHANNEL -> {
                if(defaultToHigherPower) {
                    value = Scissors.getConfiguration().getOptionForChannel(option, type, source.commandMessage().getChannel());
                    source.sendSuccess("The current effective value of the option " + monospace(option.properties().getName()) + " for this channel is " + bold(value.toString()));
                } else {
                    value = Scissors.getConfiguration().getOptionForChannelOnly(option, type, source.commandMessage().getChannel());
                    if(value == null) throw NO_EXPLICIT_VALUE_IN_CHANNEL.create(monospace(option.properties().getName()));
                    source.sendSuccess("The current explicit value of the option " + monospace(option.properties().getName()) + " for this channel is " + bold(value.toString()));
                }
            }
            default -> throw INVALID_CONTEXT.create();
        }
        return option.isInteger() ? (int) value : (boolean) value ? 1 : 0;
    }

    private static <T> int setOptionValue(ChatCommandSource source, Option option, T value, OptionContext optionContext) throws CommandSyntaxException {
        switch(optionContext) {
            case GLOBAL -> {
                Scissors.getConfiguration().setGlobalOption(option, value);
                source.sendSuccess("Set the global value of the option " + monospace(option.properties().getName()) + " to " + bold(value.toString()));
            }
            case PER_GUILD -> {
                if(!(source.commandMessage().getChannel() instanceof GuildChannel guildChannel))
                    throw NOT_IN_GUILD.create();
                if(canEditPerGuild(source.user(), guildChannel.getGuild())) {
                    Scissors.getConfiguration().setOptionForGuild(option, value, source.commandMessage().getGuild());
                    source.sendSuccess("Set the value of the option " + monospace(option.properties().getName()) + " for this server to " + bold(value.toString()));
                } else throw NO_PERMS_IN_GUILD.create();
            }
            case PER_CHANNEL -> {
                if(canEditPerChannel(source.user(), source.commandMessage().getChannel())) {
                    Scissors.getConfiguration().setOptionForChannel(option, value, source.commandMessage().getChannel());
                    source.sendSuccess("Set the value of the option " + monospace(option.properties().getName()) + " for this channel to " + bold(value.toString()));
                } else throw NO_PERMS_IN_CHANNEL.create();
            }
            default -> throw INVALID_CONTEXT.create();
        }
        Scissors.saveConfiguration();
        return option.isInteger() ? (int) value : (boolean) value ? 1 : 0;
    }

    private static boolean canEditPerGuild(User user, Guild guild) throws CommandSyntaxException {
        if(user.getIdLong() == SharedConstants.MY_USER_ID)
            return true;
        Member member = guild.retrieveMemberById(user.getIdLong()).complete();
        if(member == null)
            throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(Permission.MANAGE_SERVER);
    }

    private static boolean canEditPerChannel(User user, MessageChannelUnion channel) throws CommandSyntaxException {
        if(user.getIdLong() == SharedConstants.MY_USER_ID)
            return true;
        if(!(channel instanceof GuildChannel guildChannel))
            return true;
        Member member = guildChannel.getGuild().retrieveMemberById(user.getIdLong()).complete();
        if(member == null)
            throw IMPOSSIBLE_ERROR.create();
        return member.hasPermission(guildChannel, Permission.MANAGE_CHANNEL);
    }

    private enum OptionContext {
        GLOBAL,
        PER_GUILD,
        PER_CHANNEL
    }
}