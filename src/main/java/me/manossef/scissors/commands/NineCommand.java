package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Emojis;

public class NineCommand {
    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("9")
            .executes(context -> compareNineToEight(context.getSource()))
        );
    }

    private static int compareNineToEight(ChatCommandSource source) {
        String prefix = Commands.getPrefix(source.commandMessage().getChannel()).replace(" ", "");
        if(prefix.equals("8<"))
            source.sendSuccess("8 is, in fact, less than 9. You are correct!", false);
        else if(prefix.equals("8>"))
            source.sendSuccess("No, 8 is not greater than 9. You might need to go back to school", false);
        else
            source.sendSuccess("Nooooo you ruined my joke by changing the command prefix " + Emojis.LOUDLY_CRYING_FACE.getFormatted(), false);
        return 1;
    }
}