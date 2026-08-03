package me.manossef.scissors.jira;

import kong.unirest.core.UnirestException;
import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JiraCheckLoop implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JiraCheckLoop.class);
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long MINUTES_BETWEEN_LOOPS = 10L;
    private static final long TIME_BETWEEN_LOOPS = MINUTES_BETWEEN_LOOPS * SECONDS_PER_MINUTE * NANOS_PER_SECOND;

    private CheckedIssues checkedIssues;

    public JiraCheckLoop(CheckedIssues checkedIssues) {
        this.checkedIssues = checkedIssues;
    }

    @Override
    public void run() {
        long stopwatch = TIME_BETWEEN_LOOPS;
        Instant lastTimeCheck = Instant.now();
        while(stopwatch >= 0) {
            boolean runCheck = stopwatch >= TIME_BETWEEN_LOOPS;
            Instant now = Instant.now();
            stopwatch += Duration.between(lastTimeCheck, now).toNanos();
            lastTimeCheck = now;
            if(runCheck) stopwatch -= TIME_BETWEEN_LOOPS;
            else continue;
            try {
                CheckedIssues newChecked = this.checkIssues();
                List<Integer> uncheckedFixed = new ArrayList<>(newChecked.checkedFixed);
                uncheckedFixed.removeAll(this.checkedIssues.checkedFixed);
                LOGGER.debug("To post in #done-issues: {}", uncheckedFixed);
                for(Integer number : uncheckedFixed)
                    DevGuild.logDoneIssue(MessageCreateData.fromEmbeds(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed()));
                List<Integer> uncheckedInvalid = new ArrayList<>(newChecked.checkedInvalid);
                uncheckedInvalid.removeAll(this.checkedIssues.checkedInvalid);
                LOGGER.debug("To post in #invalid-issues: {}", uncheckedInvalid);
                for(Integer number : uncheckedInvalid)
                    DevGuild.logInvalidIssue(MessageCreateData.fromEmbeds(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed()));
                this.checkedIssues = newChecked;
                Scissors.saveCheckedIssues(newChecked);
            } catch(UnirestException e) {
                LOGGER.warn("Something went wrong; ignoring and continuing as normal.");
            }
        }
        Scissors.startJiraCheckLoop(this.checkedIssues);
    }

    private CheckedIssues checkIssues() {
        Issue[] fixedIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution = Done ORDER BY created ASC", "id,key").issues();
        List<Integer> checkedFixed;
        if(fixedIssues != null) checkedFixed = Arrays.stream(fixedIssues)
            .map(issue -> issue.key().replace("SCIS-", ""))
            .map(Integer::parseInt)
            .toList();
        else checkedFixed = this.checkedIssues.checkedFixed;
        Issue[] invalidIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution IN (Invalid, \"Won't Do\", \"Works as Intended\") ORDER BY created ASC", "id,key,resolution").issues();
        List<Integer> checkedInvalid;
        if(invalidIssues != null) checkedInvalid = Arrays.stream(invalidIssues)
            .map(issue -> issue.key().replace("SCIS-", ""))
            .map(Integer::parseInt)
            .toList();
        else checkedInvalid = this.checkedIssues.checkedInvalid;
        CheckedIssues found = new CheckedIssues(checkedFixed, checkedInvalid);
        LOGGER.debug("Checked issues, found: {}", found);
        return found;
    }

    public record CheckedIssues(List<Integer> checkedFixed, List<Integer> checkedInvalid) {
    }
}