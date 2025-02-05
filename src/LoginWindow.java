import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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

    public LoginWindow() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Username field
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateUsername();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateUsername();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateUsername();
            }
        });
        add(usernameField);
        usernameErrorLabel = new JLabel();
        usernameErrorLabel.setForeground(Color.RED);
        add(usernameErrorLabel);

        // Password field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePassword();
            }
        });
        add(passwordField);
        passwordErrorLabel = new JLabel();
        passwordErrorLabel.setForeground(Color.RED);
        add(passwordErrorLabel);

        // Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        add(loginButton);

        // Register button (redirect to registration window)
        registerButton = new JButton("Don't have an account? Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the login window
                new RegistrationWindow().setVisible(true); // Open the registration window
            }
        });
        add(registerButton);

        setVisible(true);
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
                new ITHelpdeskClientGUI().setVisible(true); // Open the main IT Helpdesk window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}