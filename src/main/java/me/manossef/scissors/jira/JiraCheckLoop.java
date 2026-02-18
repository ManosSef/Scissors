package me.manossef.scissors.jira;

import kong.unirest.core.UnirestException;
import me.manossef.scissors.DevGuild;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.SharedConstants;
import me.manossef.scissors.jira.objects.Issue;

import java.util.ArrayList;
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

            try {

                CheckedIssues newChecked = this.checkIssues();
                List<Integer> uncheckedFixed = new ArrayList<>(newChecked.checkedFixed);
                uncheckedFixed.removeAll(this.checkedIssues.checkedFixed);
                if(SharedConstants.IS_STAGING) System.out.println("To post in #done-issues: " + uncheckedFixed);
                for(Integer number : uncheckedFixed)
                    DevGuild.logDoneIssue(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed());
                List<Integer> uncheckedInvalid = new ArrayList<>(newChecked.checkedInvalid);
                uncheckedInvalid.removeAll(this.checkedIssues.checkedInvalid);
                if(SharedConstants.IS_STAGING) System.out.println("To post in #invalid-issues: " + uncheckedInvalid);
                for(Integer number : uncheckedInvalid)
                    DevGuild.logInvalidIssue(Scissors.JIRA_API.getIssue("SCIS-" + number).makeEmbed());
                this.checkedIssues = newChecked;
                Scissors.saveCheckedIssues(newChecked);
                Thread.sleep(600000L);

            } catch(UnirestException e) {

                System.err.println("Something went wrong; ignoring and continuing as normal.");

            } catch(InterruptedException e) {

                System.err.println("The thread was interrupted!");

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
        if(SharedConstants.IS_STAGING) System.out.println("Checked issues, found: " + found);
        return found;

    }

    public record CheckedIssues(List<Integer> checkedFixed, List<Integer> checkedInvalid) {
    }

}
