import java.util.List;

public class JsonResponse {
    private String status;
    private String message;
    private List<Issue> issues;

    // No-argument constructor (required by Jackson)
    public JsonResponse() {}

    // Constructor for success/error messages
    public JsonResponse(String message) {
        this.status = "SUCCESS";
        this.message = message;
    }

    // Constructor for status and message
    public JsonResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Constructor for issues list
    public JsonResponse(String status, List<Issue> issues) {
        this.status = status;
        this.issues = issues;
    }

    // Getters and setters (required by Jackson)
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