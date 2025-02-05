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
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(13, 2));

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

        // First name field
        add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        firstNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateFirstName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateFirstName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateFirstName();
            }
        });
        add(firstNameField);
        firstNameErrorLabel = new JLabel();
        firstNameErrorLabel.setForeground(Color.RED);
        add(firstNameErrorLabel);

        // Middle name field
        add(new JLabel("Middle Name:"));
        middleNameField = new JTextField();
        add(middleNameField);

        // Last name field
        add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        lastNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateLastName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateLastName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateLastName();
            }
        });
        add(lastNameField);
        lastNameErrorLabel = new JLabel();
        lastNameErrorLabel.setForeground(Color.RED);
        add(lastNameErrorLabel);

        // Prefix field
        add(new JLabel("Prefix:"));
        prefixField = new JTextField();
        add(prefixField);

        // Suffix field
        add(new JLabel("Suffix:"));
        suffixField = new JTextField();
        add(suffixField);

        // Department field
        add(new JLabel("Department:"));
        departmentField = new JTextField();
        departmentField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateDepartment();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateDepartment();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateDepartment();
            }
        });
        add(departmentField);
        departmentErrorLabel = new JLabel();
        departmentErrorLabel.setForeground(Color.RED);
        add(departmentErrorLabel);

        // Register button
        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        add(registerButton);

        // Login button (redirect to login window)
        loginButton = new JButton("Already have an account? Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the registration window
                new LoginWindow().setVisible(true); // Open the login window
            }
        });
        add(loginButton);

        setVisible(true);
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}