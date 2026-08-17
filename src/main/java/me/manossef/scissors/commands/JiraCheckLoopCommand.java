package me.manossef.scissors.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.manossef.scissors.ChatCommandSource;
import me.manossef.scissors.Commands;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.JiraCheckLoop;

public class JiraCheckLoopCommand {
    private static final SimpleCommandExceptionType NO_LOOP = new SimpleCommandExceptionType(new LiteralMessage("No Jira check loop is running"));
    private static final SimpleCommandExceptionType NOT_RUNNING = new SimpleCommandExceptionType(new LiteralMessage("The current Jira check loop is not running"));
    private static final SimpleCommandExceptionType INTERRUPTED = new SimpleCommandExceptionType(new LiteralMessage("The current Jira check loop is interrupted"));
    private static final SimpleCommandExceptionType ALREADY_INTERRUPTED = new SimpleCommandExceptionType(new LiteralMessage("The current Jira check loop is already interrupted"));

    public static void register(CommandDispatcher<ChatCommandSource> dispatcher) {
        dispatcher.register(Commands.literal("jcl")
            .requires(Commands.devRestricted())
            .executes(context -> check(context.getSource()))
            .then(Commands.literal("stop")
                .executes(context -> stop(context.getSource()))
            )
            .then(Commands.literal("restart")
                .executes(context -> restart(context.getSource()))
            )
        );
    }

    private static int check(ChatCommandSource source) throws CommandSyntaxException {
        JiraCheckLoop jcl = Scissors.getJiraCheckLoop();
        if(jcl == null) throw NO_LOOP.create();
        if(jcl.isInterrupted()) throw INTERRUPTED.create();
        if(jcl.isAlive()) {
            source.sendSuccess("Running loop: " + jcl, false);
            return 1;
        }
        throw NOT_RUNNING.create();
    }

    private static int stop(ChatCommandSource source) throws CommandSyntaxException {
        JiraCheckLoop jcl = Scissors.getJiraCheckLoop();
        if(jcl == null) throw NO_LOOP.create();
        if(jcl.isInterrupted()) throw ALREADY_INTERRUPTED.create();
        if(!jcl.isAlive()) throw NOT_RUNNING.create();
        jcl.interrupt();
        source.sendSuccess("Stopped the Jira check loop", true);
        return 1;
    }

    private static int restart(ChatCommandSource source) {
        JiraCheckLoop jcl = Scissors.getJiraCheckLoop();
        if(jcl == null || jcl.isInterrupted() || !jcl.isAlive()) {
            Scissors.startJiraCheckLoop();
            source.sendSuccess("Started a new Jira check loop", true);
            return 2;
        }
        jcl.interrupt();
        Scissors.startJiraCheckLoop();
        source.sendSuccess("Stopped the currently running Jira check loop and started a new one", true);
        return 1;
    }
}