package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import kong.unirest.core.UnirestException;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

public class IssueCommand {
    private static final DynamicCommandExceptionType ISSUE_NOT_FOUND = new DynamicCommandExceptionType(issue -> new LiteralMessage("Could not find issue " + issue));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        String baseLiteral = "issue";
        dispatcher.register(Commands.literal(baseLiteral)
            .then(Commands.argument("number", IntegerArgumentType.integer(1))
                .executes(context -> getIssue(context.getSource(), "SCIS-" + context.getArgument("number", Integer.class)))
            )
        );
        HelpCommand.addLine(baseLiteral, s -> "Fetches a work item from the bot's internal Jira instance and shows information about it.");
        HelpCommand.addLiteral(baseLiteral, source -> String.format("""
                Fetches a work item from the bot's internal Jira instance and displays information about it with an embed. The work item to be fetched has the key %s, where %s is the specified number.
                
                The embed is colored gray if the work item is unresolved, green if it's resolved as "Done" and red if it's resolved with any other resolution.
                
                Syntax: %s
                
                Fails if there is no work item with the specified number.""",
            monospace("SCIS-<#>"),
            monospace("<#>"),
            Commands.format(baseLiteral + " <number>", source.commandMessage().getChannel())));
    }

    private static int getIssue(ChatCommandSource source, String issueKey) throws CommandSyntaxException {
        try {
            Issue issue = Scissors.JIRA_API.getIssue(issueKey);
            if(issue.id() == null || !canSeeIssue(source, issue)) throw ISSUE_NOT_FOUND.create(issueKey);
            source.sendSuccess("Successfully found issue " + issueKey + ":", false, issue.makeEmbed());
            return 1;
        } catch(UnirestException e) {
            throw Commands.IO_EXCEPTION.create();
        }
    }

    private static boolean canSeeIssue(ChatCommandSource source, Issue issue) {
        if(issue.fields().flagged() == null) return true;
        return source.commandMessage().getChannel() instanceof PrivateChannel && source.user().getId().equals(issue.fields().reporterUserID());
    }
}