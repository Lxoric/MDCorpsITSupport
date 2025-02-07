import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Helpdesk {
    public void reportIssue(String description, String reporterName) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO issues (description, reporter_name, first_name, last_name, department, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, description);
            stmt.setString(2, reporterName);

            // Fetch additional user details (first name, last name, department) from the database
            String userSql = "SELECT first_name, last_name, department FROM users WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, reporterName);
            ResultSet rs = userStmt.executeQuery();

            if (rs.next()) {
                stmt.setString(3, rs.getString("first_name"));
                stmt.setString(4, rs.getString("last_name"));
                stmt.setString(5, rs.getString("department"));
            } else {
                throw new SQLException("User not found: " + reporterName);
            }

            stmt.setString(6, "Open"); // Default status
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }

    public List<Issue> getIssues() {
        List<Issue> issues = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM issues";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String department = rs.getString("department");
                String status = rs.getString("status");

                // Create Issue object with all fields
                issues.add(new Issue(id, description, firstName, lastName, department, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }

        return issues;
    }

    public void resolveIssue(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "UPDATE issues SET status = 'Resolved' WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, null);
        }
    }
}