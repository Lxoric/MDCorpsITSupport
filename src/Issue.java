public class Issue {
    private int id;
    private String description;
    private String firstName;
    private String lastName;
    private String department;
    private String status;

    // Constructor with all fields
    public Issue(int id, String description, String firstName, String lastName, String department, String status) {
        this.id = id;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.status = status;
    }

    public Issue() {} // Required for deserialization

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }
}