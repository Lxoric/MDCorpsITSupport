import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JTextField lastNameField;
    private JTextField prefixField;
    private JTextField suffixField;
    private JTextField departmentField;
    private JButton registerButton;
    private JButton loginButton;
    private JLabel usernameErrorLabel;
    private JLabel passwordErrorLabel;
    private JLabel firstNameErrorLabel;
    private JLabel lastNameErrorLabel;
    private JLabel departmentErrorLabel;

    public RegistrationWindow() {
        setTitle("Registration");
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
        cardPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE)); // Limit width

        // Title
        JLabel titleLabel = new JLabel("Registration");
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
        cardPanel.add(Box.createVerticalStrut(10));

        // First name field
        JPanel firstNamePanel = createFormField("First Name:", firstNameField = new JTextField(20));
        firstNameErrorLabel = new JLabel();
        firstNameErrorLabel.setForeground(Color.RED);
        firstNamePanel.add(firstNameErrorLabel);
        cardPanel.add(firstNamePanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Middle name field
        cardPanel.add(createFormField("Middle Name:", middleNameField = new JTextField(20)));
        cardPanel.add(Box.createVerticalStrut(10));

        // Last name field
        JPanel lastNamePanel = createFormField("Last Name:", lastNameField = new JTextField(20));
        lastNameErrorLabel = new JLabel();
        lastNameErrorLabel.setForeground(Color.RED);
        lastNamePanel.add(lastNameErrorLabel);
        cardPanel.add(lastNamePanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Prefix field
        cardPanel.add(createFormField("Prefix:", prefixField = new JTextField(20)));
        cardPanel.add(Box.createVerticalStrut(10));

        // Suffix field
        cardPanel.add(createFormField("Suffix:", suffixField = new JTextField(20)));
        cardPanel.add(Box.createVerticalStrut(10));

        // Department field
        JPanel departmentPanel = createFormField("Department:", departmentField = new JTextField(20));
        departmentErrorLabel = new JLabel();
        departmentErrorLabel.setForeground(Color.RED);
        departmentPanel.add(departmentErrorLabel);
        cardPanel.add(departmentPanel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Register button
        registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(new Color(0, 123, 255)); // Blue background
        registerButton.setForeground(Color.WHITE); // White text
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        cardPanel.add(registerButton);
        cardPanel.add(Box.createVerticalStrut(10));

        // Login button (redirect to login window)
        loginButton = new JButton("Already have an account? Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setForeground(new Color(0, 123, 255)); // Blue text
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the registration window
                new LoginWindow().setVisible(true); // Open the login window
            }
        });
        cardPanel.add(loginButton);

        // Add card panel to main panel
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 30)); // Fixed label width
        panel.add(label);

        textField.setMaximumSize(new Dimension(300, 30)); // Fixed text field size
        panel.add(textField);

        return panel;
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String prefix = prefixField.getText().trim();
        String suffix = suffixField.getText().trim();
        String department = departmentField.getText().trim();

        // Validate all fields before proceeding
        validateUsername();
        validatePassword();
        validateFirstName();
        validateLastName();
        validateDepartment();

        if (!usernameErrorLabel.getText().isEmpty() ||
                !passwordErrorLabel.getText().isEmpty() ||
                !firstNameErrorLabel.getText().isEmpty() ||
                !lastNameErrorLabel.getText().isEmpty() ||
                !departmentErrorLabel.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fix the errors before submitting.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO users (username, password, first_name, middle_name, last_name, prefix, suffix, department) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, hash the password before storing it
            stmt.setString(3, firstName);
            stmt.setString(4, middleName);
            stmt.setString(5, lastName);
            stmt.setString(6, prefix);
            stmt.setString(7, suffix);
            stmt.setString(8, department);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose(); // Close the registration window
            new LoginWindow().setVisible(true); // Redirect to the login window
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Duplicate entry (e.g., username already exists)
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            usernameErrorLabel.setText("Username cannot be empty.");
        } else if (username.length() < 4 || username.length() > 20) {
            usernameErrorLabel.setText("Username must be 4-20 characters.");
        } else {
            usernameErrorLabel.setText("");
        }
    }

    private void validatePassword() {
        String password = new String(passwordField.getPassword()).trim();
        if (password.isEmpty()) {
            passwordErrorLabel.setText("Password cannot be empty.");
        } else if (password.length() < 6) {
            passwordErrorLabel.setText("Password must be at least 6 characters.");
        } else {
            passwordErrorLabel.setText("");
        }
    }

    private void validateFirstName() {
        String firstName = firstNameField.getText().trim();
        if (firstName.isEmpty()) {
            firstNameErrorLabel.setText("First Name cannot be empty.");
        } else if (firstName.length() > 50) {
            firstNameErrorLabel.setText("First Name must be less than 50 characters.");
        } else {
            firstNameErrorLabel.setText("");
        }
    }

    private void validateLastName() {
        String lastName = lastNameField.getText().trim();
        if (lastName.isEmpty()) {
            lastNameErrorLabel.setText("Last Name cannot be empty.");
        } else if (lastName.length() > 50) {
            lastNameErrorLabel.setText("Last Name must be less than 50 characters.");
        } else {
            lastNameErrorLabel.setText("");
        }
    }

    private void validateDepartment() {
        String department = departmentField.getText().trim();
        if (department.isEmpty()) {
            departmentErrorLabel.setText("Department cannot be empty.");
        } else if (department.length() > 50) {
            departmentErrorLabel.setText("Department must be less than 50 characters.");
        } else {
            departmentErrorLabel.setText("");
        }
    }
}