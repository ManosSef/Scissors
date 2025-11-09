package me.manossef.scissors.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.Scissors;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Collection;

public class UserArgument implements ArgumentType<User> {

    private static final SimpleCommandExceptionType INVALID_MENTION = new SimpleCommandExceptionType(new LiteralMessage("Invalid user mention"));
    private static final Collection<String> EXAMPLES = Arrays.asList("611151083141857286", "<@611151083141857286>");

    public static UserArgument user() {

        return new UserArgument();

    }

    @Override
    public User parse(StringReader reader) throws CommandSyntaxException {

        String remaining = reader.getRemaining().split(" ")[0];
        if(this.isLong(remaining)) {

            reader.setCursor(reader.getCursor() + remaining.length());
            return Scissors.DISCORD_API.getUserById(Long.parseLong(remaining));

        }
        if(remaining.startsWith("<@") && remaining.endsWith(">")) {

            String middle = remaining.substring(2, remaining.length() - 1);
            if(this.isLong(middle)) {

                reader.setCursor(reader.getCursor() + remaining.length());
                return Scissors.DISCORD_API.getUserById(Long.parseLong(middle));

            }
            String legacyMiddle = middle.replaceFirst("!", "");
            if(middle.startsWith("!") && this.isLong(legacyMiddle)) {

                reader.setCursor(reader.getCursor() + remaining.length());
                return Scissors.DISCORD_API.getUserById(Long.parseLong(legacyMiddle));

            }

        }
        throw INVALID_MENTION.create();

    }

    public Collection<String> getExamples() {

        return EXAMPLES;

    }

    private boolean isLong(String input) {

        try {

            Long.parseLong(input);
            return true;

        } catch(NumberFormatException e) {

            return false;

        }

    }

}
