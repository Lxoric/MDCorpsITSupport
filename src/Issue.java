public class Issue {
    private int id;
    private String description;
    private String reporterName;
    private String status;

    // No-argument constructor (required by Jackson)
    public Issue() {}

    // Constructor for creating an issue
    public Issue(int id, String description, String reporterName, String status) {
        this.id = id;
        this.description = description;
        this.reporterName = reporterName;
        this.status = status;
    }

    // Getters and setters (required by Jackson)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Issue ID: " + id + ", Description: " + description + ", Reporter: " + reporterName + ", Status: " + status;
    }
}