import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel usernameErrorLabel;
    private JLabel passwordErrorLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true); // Start with the login window
            }
        });
    }

    public LoginWindow() {
        setTitle("Login");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        // Card panel for the form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE)); // Limit width

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(20)); // Spacing

        // Username field
        JPanel usernamePanel = createFormField("Username:", usernameField = new JTextField(20));
        usernameErrorLabel = new JLabel();
        usernameErrorLabel.setForeground(Color.RED);
        usernamePanel.add(usernameErrorLabel);
        cardPanel.add(usernamePanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Password field
        JPanel passwordPanel = createFormField("Password:", passwordField = new JPasswordField(20));
        passwordErrorLabel = new JLabel();
        passwordErrorLabel.setForeground(Color.RED);
        passwordPanel.add(passwordErrorLabel);
        cardPanel.add(passwordPanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(0, 123, 255)); // Blue background
        loginButton.setForeground(Color.WHITE); // White text
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalStrut(10));

        // Register button (redirect to registration window)
        registerButton = new JButton("Don't have an account? Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(new Color(0, 123, 255)); // Blue text
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the login window
                new RegistrationWindow().setVisible(true); // Open the registration window
            }
        });
        cardPanel.add(registerButton);

        // Add card panel to main panel
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true); // Ensure the window is visible
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 30)); // Fixed label width
        panel.add(label);

        textField.setMaximumSize(new Dimension(200, 30)); // Fixed text field size
        panel.add(textField);

        return panel;
    }

    private void loginUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validate fields before proceeding
        validateUsername();
        validatePassword();

        if (!usernameErrorLabel.getText().isEmpty() || !passwordErrorLabel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fix the errors before submitting.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, compare hashed passwords
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose(); // Close the login window
                new ITHelpdeskClientGUI(username).setVisible(true); // Open the main IT Helpdesk window with the logged-in username
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            usernameErrorLabel.setText("Username cannot be empty.");
        } else {
            usernameErrorLabel.setText("");
        }
    }

    private void validatePassword() {
        String password = new String(passwordField.getPassword()).trim();
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password cannot be empty.");
        } else {
            passwordErrorLabel.setText("");
        }
    }
}