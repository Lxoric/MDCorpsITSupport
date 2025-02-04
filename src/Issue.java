import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Issue {
    private int id;
    private String description;
    private String reporterName;
    private String status;

    @JsonCreator
    public Issue(
            @JsonProperty("id") int id,
            @JsonProperty("description") String description,
            @JsonProperty("reporterName") String reporterName) {
        this.id = id;
        this.description = description;
        this.reporterName = reporterName;
        this.status = "Open";
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getReporterName() {
        return reporterName;
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