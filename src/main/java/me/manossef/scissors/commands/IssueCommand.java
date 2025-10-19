package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;

public class IssueCommand {

    private static final DynamicCommandExceptionType ISSUE_NOT_FOUND = new DynamicCommandExceptionType(issue -> new LiteralMessage("Could not find issue " + issue));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {

        dispatcher.register(Commands.literal("issue")
            .then(Commands.argument("number", IntegerArgumentType.integer(1))
                .executes(context -> getIssue(context.getSource(), "SCIS-" + context.getArgument("number", Integer.class)))
            )
        );

    }

    private static int getIssue(ChatCommandSource source, String issueKey) throws CommandSyntaxException {

        Issue issue = Scissors.JIRA_API.getIssue(issueKey);
        if(issue.id() == null) throw ISSUE_NOT_FOUND.create(issueKey);
        source.commandMessage().reply(issue.makeEmbed()).queue();
        return 1;

    }

}
