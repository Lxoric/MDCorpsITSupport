import java.util.ArrayList;
import java.util.List;

public class JsonResponse {
    private String status;
    private String message;
    private List<Issue> issues = new ArrayList<>(); // Now an ArrayList

    public JsonResponse() {}

    // Constructor for messages
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
        this.issues = new ArrayList<>(issues); // Enforce ArrayList
    }

    // Getters and setters
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

    // Setter copies the list into an ArrayList
    // Setter for issues
    public void setIssues(List<Issue> issues) {
        this.issues = new ArrayList<>(issues); // Enforce ArrayList
    }
}