import java.util.ArrayList;
import java.util.List;

public class Helpdesk {
    private List<Issue> issues;
    private int nextId;

    public Helpdesk() {
        issues = new ArrayList<>();
        nextId = 1;
    }

    public void reportIssue(String description, String reporterName) {
        Issue issue = new Issue(nextId++, description, reporterName);
        issues.add(issue);
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void resolveIssue(int id) {
        for (Issue issue : issues) {
            if (issue.getId() == id) {
                issue.setStatus("Resolved");
                return;
            }
        }
    }
}