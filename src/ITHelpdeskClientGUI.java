import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ITHelpdeskClientGUI extends JFrame {
    private JTextField descriptionField;
    private JTextField reporterNameField;
    private JButton reportButton;
    private JButton resolveButton;
    private JTextField resolveField;
    private JTable issueTable;
    private DefaultTableModel tableModel;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String loggedInUsername;

    public ITHelpdeskClientGUI(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
        initializeUI();
        startAutoRefresh(); // Start the timer for automatic updates
    }

    private void initializeUI() {
        setTitle("IT Helpdesk Client");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

        // Title
        JLabel titleLabel = new JLabel("IT Helpdesk");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Reporter name field (non-editable)
        JPanel reporterPanel = createFormField("Reporter Name:", reporterNameField = new JTextField(20));
        reporterNameField.setText(loggedInUsername);
        reporterNameField.setEditable(false);
        cardPanel.add(reporterPanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Issue description field
        JPanel descriptionPanel = createFormField("Issue Description:", descriptionField = new JTextField(20));
        cardPanel.add(descriptionPanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Report button
        reportButton = new JButton("Report Issue");
        reportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reportButton.setBackground(new Color(0, 123, 255));
        reportButton.setForeground(Color.WHITE);
        reportButton.setFocusPainted(false);
        reportButton.addActionListener(e -> reportIssue());
        cardPanel.add(reportButton);
        cardPanel.add(Box.createVerticalStrut(20));

        // Table to display issues
        String[] columnNames = {"ID", "Description", "Reporter", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        issueTable = new JTable(tableModel);
        issueTable.setAutoCreateRowSorter(true); // Enable sorting
        JScrollPane scrollPane = new JScrollPane(issueTable);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        cardPanel.add(scrollPane);
        cardPanel.add(Box.createVerticalStrut(20));

        // Resolve issue field
        JPanel resolvePanel = createFormField("Issue ID to Resolve:", resolveField = new JTextField(20));
        cardPanel.add(resolvePanel);
        cardPanel.add(Box.createVerticalStrut(10));

        // Resolve button
        resolveButton = new JButton("Resolve Issue");
        resolveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resolveButton.setBackground(new Color(0, 123, 255));
        resolveButton.setForeground(Color.WHITE);
        resolveButton.setFocusPainted(false);
        resolveButton.addActionListener(e -> resolveIssue());
        cardPanel.add(resolveButton);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);

        connectToServer();
    }

    private JPanel createFormField(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(150, 30));
        panel.add(label);

        textField.setMaximumSize(new Dimension(300, 30));
        panel.add(textField);

        return panel;
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Start the timer to auto-refresh every 15 seconds
    private void startAutoRefresh() {
        Timer timer = new Timer(15000, e -> sendRequest("VIEW", null));
        timer.setRepeats(true);
        timer.start();
    }

    // Update the table with new issues
    private void updateTable(List<Issue> issues) {
        tableModel.setRowCount(0); // Clear existing data
        for (Issue issue : issues) {
            tableModel.addRow(new Object[]{
                    issue.getId(),
                    issue.getDescription(),
                    issue.getReporterName(),
                    issue.getStatus()
            });
        }
    }

    private void reportIssue() {
        String description = descriptionField.getText().trim();
        String reporterName = reporterNameField.getText().trim();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Issue description cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("description", description);
        data.put("reporterName", reporterName);
        sendRequest("REPORT", data);
        descriptionField.setText("");
    }

    private void resolveIssue() {
        String id = resolveField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Issue ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("id", id);
        sendRequest("RESOLVE", data);
        resolveField.setText("");
    }

    private void sendRequest(String command, Map<String, String> data) {
        try {
            JsonRequest request = new JsonRequest();
            request.setCommand(command);
            request.setData(data);
            String jsonRequest = objectMapper.writeValueAsString(request);
            out.println(jsonRequest);

            String response = in.readLine();
            if (response == null) return;

            JsonResponse jsonResponse = objectMapper.readValue(response, JsonResponse.class);
            if (jsonResponse.getStatus().equals("ERROR")) {
                throw new IOException(jsonResponse.getMessage());
            }

            if (command.equals("VIEW")) {
                updateTable(jsonResponse.getIssues()); // Update the table with new data
            } else {
                JOptionPane.showMessageDialog(this, jsonResponse.getMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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