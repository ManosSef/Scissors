package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
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
            .requires(Commands.devRestricted())
            .then(Commands.argument("key", StringArgumentType.word())
                .executes(context -> getIssue(context.getSource(), context.getArgument("key", String.class)))
            )
        );

    }

    private static int getIssue(ChatCommandSource source, String issueKey) throws CommandSyntaxException {

        Issue issue = Scissors.JIRA_API.getIssue(issueKey);
        if(issue.id() == null) throw ISSUE_NOT_FOUND.create(issueKey);
        source.sendSuccess(issue.toString());
        return 1;

    }

}
