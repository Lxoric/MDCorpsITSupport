import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ITHelpdeskClientGUI extends JFrame {
    private JTextArea issueTextArea;
    private JTextField descriptionField;
    private JTextField reporterNameField; // Non-editable field for the submitter's name
    private JButton reportButton;
    private JButton viewButton;
    private JTextField resolveField;
    private JButton resolveButton;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String loggedInUsername; // Store the logged-in username

    // Constructor that accepts the logged-in username
    public ITHelpdeskClientGUI(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername; // Set the logged-in username
        initializeUI();
    }

    private void initializeUI() {
        setTitle("IT Helpdesk Client");
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
        cardPanel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); // Limit width

        // Title
        JLabel titleLabel = new JLabel("IT Helpdesk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(20)); // Spacing

        // Reporter name field (non-editable)
        JPanel reporterPanel = createFormField("Reporter Name:", reporterNameField = new JTextField(20));
        reporterNameField.setText(loggedInUsername); // Automatically populate with the logged-in username
        reporterNameField.setEditable(false); // Make the field non-editable
        cardPanel.add(reporterPanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Issue description field
        JPanel descriptionPanel = createFormField("Issue Description:", descriptionField = new JTextField(20));
        cardPanel.add(descriptionPanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Report button
        reportButton = new JButton("Report Issue");
        reportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportButton.setBackground(new Color(0, 123, 255)); // Blue background
        reportButton.setForeground(Color.WHITE); // White text
        reportButton.setFocusPainted(false);
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportIssue();
            }
        });
        cardPanel.add(reportButton);
        cardPanel.add(Box.createVerticalStrut(20));

        // View issues area
        issueTextArea = new JTextArea();
        issueTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(issueTextArea);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        cardPanel.add(scrollPane);
        cardPanel.add(Box.createVerticalStrut(10));

        // Resolve issue field
        JPanel resolvePanel = createFormField("Issue ID to Resolve:", resolveField = new JTextField(20));
        cardPanel.add(resolvePanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Resolve button
        resolveButton = new JButton("Resolve Issue");
        resolveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resolveButton.setBackground(new Color(0, 123, 255)); // Blue background
        resolveButton.setForeground(Color.WHITE); // White text
        resolveButton.setFocusPainted(false);
        resolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resolveIssue();
            }
        });
        cardPanel.add(resolveButton);

        // Add card panel to main panel
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Connect to the server
        connectToServer();
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(150, 30)); // Fixed label width
        panel.add(label);

        textField.setMaximumSize(new Dimension(300, 30)); // Fixed text field size
        panel.add(textField);

        return panel;
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void reportIssue() {
        String description = descriptionField.getText().trim();
        String reporterName = reporterNameField.getText().trim(); // Automatically populated

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Issue description cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("description", description);
            data.put("reporterName", reporterName);
            sendRequest("REPORT", data);
            descriptionField.setText(""); // Clear the description field
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resolveIssue() {
        String id = resolveField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Issue ID cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("id", id);
            sendRequest("RESOLVE", data);
            resolveField.setText(""); // Clear the resolve field
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendRequest(String command, Map<String, String> data) {
        try {
            JsonRequest request = new JsonRequest();
            request.setCommand(command);
            request.setData(data);
            String jsonRequest = objectMapper.writeValueAsString(request);
            System.out.println("Sending request: " + jsonRequest);
            out.println(jsonRequest);

            String response = in.readLine();
            if (response == null) {
                throw new IOException("Server closed the connection.");
            }
            System.out.println("Received response: " + response);

            JsonResponse jsonResponse = objectMapper.readValue(response, JsonResponse.class);
            if (jsonResponse.getStatus().equals("ERROR")) {
                throw new IOException(jsonResponse.getMessage());
            }

            if (command.equals("VIEW")) {
                issueTextArea.setText("");
                if (jsonResponse.getIssues() != null) {
                    for (Issue issue : jsonResponse.getIssues()) {
                        issueTextArea.append(issue.toString() + "\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, jsonResponse.getMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        //For testing purposes only
        SwingUtilities.invokeLater(new Runnable() {
           @Override
            public void run() {
                //new ITHelpdeskClientGUI("TestUser").setVisible(true); // Only for testing
                new LoginWindow();
            }
        });
    }
}