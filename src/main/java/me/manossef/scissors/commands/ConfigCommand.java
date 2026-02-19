package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.config.Option;
import me.manossef.scissors.config.OptionProperties;

public class ConfigCommand {

    private static final SimpleCommandExceptionType INVALID_CONTEXT = new SimpleCommandExceptionType(new LiteralMessage("Invalid option context"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("config")
            .then(optionsArguments(Commands.literal("global"), OptionContext.GLOBAL))
            .then(optionsArguments(Commands.literal("server"), OptionContext.PER_GUILD))
            .then(optionsArguments(Commands.literal("channel"), OptionContext.PER_CHANNEL))
        );

    }

    private static ArgumentBuilder<ChatCommandSource, ?> optionsArguments(ArgumentBuilder<ChatCommandSource, ?> argument, OptionContext optionContext) {

        for(Option option : Option.values()) {

            ArgumentBuilder<ChatCommandSource, ?> optionArgument = Commands.literal(option.properties().getName())
                .executes(context -> getOptionValue(context.getSource(), option, option.properties().getType(), optionContext));
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

        }
        return argument;

    }

    private static <T> int getOptionValue(ChatCommandSource source, Option option, Class<T> type, OptionContext optionContext) throws CommandSyntaxException {

        T value;
        switch(optionContext) {

            case GLOBAL -> {

                value = Scissors.getConfiguration().getGlobalOption(option, type);
                source.sendSuccess("The current global value of the option \"" + option.properties().getName() + "\" is " + value);

            }
            case PER_GUILD -> {

                value = Scissors.getConfiguration().getOptionForGuild(option, type, source.commandMessage().getGuild());
                source.sendSuccess("The current effective value of the option \"" + option.properties().getName() + "\" for this server is " + value);

            }
            case PER_CHANNEL -> {

                value = Scissors.getConfiguration().getOptionForChannel(option, type, source.commandMessage().getChannel());
                source.sendSuccess("The current effective value of the option \"" + option.properties().getName() + "\" for this channel is " + value);

            }
            default -> throw INVALID_CONTEXT.create();

        }
        return option.isInteger() ? (int) value : (boolean) value ? 1 : 0;

    }

    private static <T> int setOptionValue(ChatCommandSource source, Option option, T value, OptionContext optionContext) throws CommandSyntaxException {

        switch(optionContext) {

            case GLOBAL -> {

                Scissors.getConfiguration().setGlobalOption(option, value);
                source.sendSuccess("Set the global value of the option \"" + option.properties().getName() + "\" to " + value);

            }
            case PER_GUILD -> {

                Scissors.getConfiguration().setOptionForGuild(option, value, source.commandMessage().getGuild());
                source.sendSuccess("Set the value of the option \"" + option.properties().getName() + "\" for this server to " + value);

            }
            case PER_CHANNEL -> {

                Scissors.getConfiguration().setOptionForChannel(option, value, source.commandMessage().getChannel());
                source.sendSuccess("Set the value of the option \"" + option.properties().getName() + "\" for this channel to " + value);

            }
            default -> throw INVALID_CONTEXT.create();

        }
        Scissors.saveConfiguration();
        return option.isInteger() ? (int) value : (boolean) value ? 1 : 0;

    }

    private enum OptionContext {

        GLOBAL,
        PER_GUILD,
        PER_CHANNEL

    }

}
