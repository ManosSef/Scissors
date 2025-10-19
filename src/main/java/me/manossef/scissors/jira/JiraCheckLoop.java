package me.manossef.scissors.jira;

import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;

import java.util.Arrays;
import java.util.List;

public class JiraCheckLoop implements Runnable {

    private CheckedIssues checkedIssues;

    public JiraCheckLoop(CheckedIssues checkedIssues) {

        this.checkedIssues = checkedIssues;

    }

    @Override
    public void run() {

        while(true) {

            CheckedIssues newChecked = this.checkIssues();
            List<Integer> uncheckedFixed = newChecked.checkedFixed;
            uncheckedFixed.removeAll(this.checkedIssues.checkedFixed);
            for(Integer number : uncheckedFixed)
                DevGuild.logDoneIssue(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed());
            List<Integer> uncheckedInvalid = newChecked.checkedInvalid;
            uncheckedInvalid.removeAll(this.checkedIssues.checkedInvalid);
            for(Integer number : uncheckedInvalid)
                DevGuild.logInvalidIssue(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed());
            this.checkedIssues = newChecked;
            Scissors.saveCheckedIssues(newChecked);
            try {

                Thread.sleep(60000L);

            } catch(InterruptedException e) {

                System.err.println("The thread was interrupted!");

            }

        }

    }

    private CheckedIssues checkIssues() {

        Issue[] fixedIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution = Done ORDER BY created ASC", "id,key").issues();
        List<Integer> checkedFixed = Arrays.stream(fixedIssues)
            .map(issue -> issue.key().replace("SCIS-", ""))
            .map(Integer::parseInt)
            .toList();
        Issue[] invalidIssues = Scissors.JIRA_API.searchIssues("project = SCIS AND resolution IN (Invalid, \"Won't Do\", \"Works as Intended\") ORDER BY created ASC", "id,key,resolution").issues();
        List<Integer> checkedInvalid = Arrays.stream(invalidIssues)
            .map(issue -> issue.key().replace("SCIS-", ""))
            .map(Integer::parseInt)
            .toList();
        return new CheckedIssues(checkedFixed, checkedInvalid);

    }

    public record CheckedIssues(List<Integer> checkedFixed, List<Integer> checkedInvalid) {
    }

}
