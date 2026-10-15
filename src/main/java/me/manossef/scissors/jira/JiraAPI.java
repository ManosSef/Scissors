package me.manossef.scissors.jira;

import com.google.gson.JsonSyntaxException;
import me.manossef.scissors.Scissors;
import me.manossef.scissors.jira.objects.Issue;
import me.manossef.scissors.jira.objects.SearchResults;
import okhttp3.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

public record JiraAPI(String baseUrl) {
    private static final String JIRA_EMAIL = System.getenv("JIRA_ALT_EMAIL");
    private static final String JIRA_API_TOKEN = System.getenv("JIRA_ALT_API_TOKEN");

    public boolean isLoggedIn() {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "myself")), Object.class) != null;
        } catch(JsonSyntaxException e) {
            return false;
        }
    }

    public Issue getIssue(String key) {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "issue/" + key)), Issue.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public Issue.Fields.Issuetype getIssuetype(String id) {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "issuetype/" + id)), Issue.Fields.Issuetype.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public Issue.Fields.Project getProject(String id) {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "project/" + id)), Issue.Fields.Project.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public Issue.Fields.Priority getPriority(String id) {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "priority/" + id)), Issue.Fields.Priority.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public Issue.Fields.CustomFieldOption getCustomFieldOption(String id) {
        try {
            return Scissors.GSON.fromJson(get(HttpUrl.get(this.baseUrl + "customFieldOption/" + id)), Issue.Fields.CustomFieldOption.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public SearchResults searchIssues(String jql, String fields) {
        HttpUrl url = HttpUrl.get(this.baseUrl + "search/jql").newBuilder()
            .addQueryParameter("jql", jql)
            .addQueryParameter("maxResults", "1000")
            .addQueryParameter("fields", fields)
            .build();
        try {
            return Scissors.GSON.fromJson(get(url), SearchResults.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    public Issue createIssue(String summary, String description, Issue.Fields.Issuetype issuetype, Issue.Fields.Project project, String reporterUserID, Issue.Fields.Priority priority, Issue.Fields.CustomFieldOption flagged) {
        String node = post(HttpUrl.get(this.baseUrl + "issue"), Scissors.GSON.toJson(new Issue(null, null, new Issue.Fields(
            issuetype, project, null, priority, summary, description, null,
            flagged == null ? null : new Issue.Fields.CustomFieldOption[]{flagged}, null, reporterUserID
        ), null, null)));
        try {
            return Scissors.GSON.fromJson(node, Issue.class);
        } catch(JsonSyntaxException e) {
            return null;
        }
    }

    private String get(HttpUrl endpoint) {
        Request request = new Request.Builder()
            .url(endpoint)
            .header("Authorization", this.getAuthHeader())
            .build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            return response.body().string();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String post(HttpUrl endpoint, String body) {
        Request request = new Request.Builder()
            .url(endpoint)
            .post(RequestBody.create(body, MediaType.get("application/json")))
            .header("Authorization", this.getAuthHeader())
            .header("Accept", "application/json")
            .build();
        try(Response response = Scissors.HTTP_CLIENT.newCall(request).execute()) {
            return response.body().string();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getAuthHeader() {
        return "Basic " + new String(Base64.getEncoder().encode((JIRA_EMAIL + ":" + JIRA_API_TOKEN).getBytes()));
    }
}