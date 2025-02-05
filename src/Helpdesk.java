import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Helpdesk {
    public void reportIssue(String description, String reporterName) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO issues (description, reporter_name) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, description);
            stmt.setString(2, reporterName);
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
                String reporterName = rs.getString("reporter_name");
                String status = rs.getString("status");
                issues.add(new Issue(id, description, reporterName, status));
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