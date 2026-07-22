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
    private static final int TIME_BETWEEN_LOOPS = 600000;

    private CheckedIssues checkedIssues;

    public JiraCheckLoop(CheckedIssues checkedIssues) {
        this.checkedIssues = checkedIssues;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        int stopwatch = 0;
        Instant lastTimeCheck = Instant.now();
        while(true) {
            Instant now = Instant.now();
            stopwatch += (int) Duration.between(lastTimeCheck, now).toMillis();
            lastTimeCheck = now;
            if(stopwatch >= TIME_BETWEEN_LOOPS) {
                stopwatch -= TIME_BETWEEN_LOOPS;
                continue;
            }
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
    }

    private CheckedIssues checkIssues() {
        Issue[] fixedIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution = Done ORDER BY created ASC", "id,key").issues();
        List<Integer> checkedFixed;
        if(fixedIssues != null)
            checkedFixed = Arrays.stream(fixedIssues)
            .map(issue -> issue.key().replace("SCIS-", ""))
            .map(Integer::parseInt)
            .toList();
        else checkedFixed = this.checkedIssues.checkedFixed;
        Issue[] invalidIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution IN (Invalid, \"Won't Do\", \"Works as Intended\") ORDER BY created ASC", "id,key,resolution").issues();
        List<Integer> checkedInvalid;
        if(invalidIssues != null)
            checkedInvalid = Arrays.stream(invalidIssues)
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