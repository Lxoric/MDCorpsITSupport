import java.util.List;

public class JsonResponse {
    private String status;
    private String message;
    private List<Issue> issues;

    public JsonResponse(String message, String s) {
        this.status = "SUCCESS";
        this.message = message;
    }

    public JsonResponse(String status, List<Issue> issues) {
        this.status = status;
        this.issues = issues;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}