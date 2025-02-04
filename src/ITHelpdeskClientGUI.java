import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ITHelpdeskClientGUI extends JFrame {
    private JTextArea issueTextArea;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ITHelpdeskClientGUI() {
        // Set up the main window
        setTitle("IT Helpdesk Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField descriptionField = new JTextField();
        JTextField reporterField = new JTextField();
        JButton reportButton = new JButton("Report Issue");
        JButton viewButton = new JButton("View Issues");
        JTextField resolveField = new JTextField();
        JButton resolveButton = new JButton("Resolve Issue");

        issueTextArea = new JTextArea();
        issueTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(issueTextArea);

        // Add components to the input panel
        inputPanel.add(new JLabel("Issue Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Reporter Name:"));
        inputPanel.add(reporterField);
        inputPanel.add(reportButton);
        inputPanel.add(viewButton);

        // Add components to the resolve panel
        JPanel resolvePanel = new JPanel(new BorderLayout());
        resolvePanel.add(new JLabel("Issue ID to Resolve:"), BorderLayout.WEST);
        resolvePanel.add(resolveField, BorderLayout.CENTER);
        resolvePanel.add(resolveButton, BorderLayout.EAST);

        // Add panels to the main window
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(resolvePanel, BorderLayout.SOUTH);

        // Connect to the server
        connectToServer();

        // Add action listeners
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = descriptionField.getText();
                String reporterName = reporterField.getText();
                if (!description.isEmpty() && !reporterName.isEmpty()) {
                    Map<String, String> data = new HashMap<>();
                    data.put("description", description);
                    data.put("reporterName", reporterName);
                    sendRequest("REPORT", data);
                    descriptionField.setText("");
                    reporterField.setText("");
                } else {
                    JOptionPane.showMessageDialog(ITHelpdeskClientGUI.this, "Please fill in all fields.");
                }
            }
        });

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest("VIEW", null);
            }
        });

        resolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = resolveField.getText();
                if (!id.isEmpty()) {
                    Map<String, String> data = new HashMap<>();
                    data.put("id", id);
                    sendRequest("RESOLVE", data);
                    resolveField.setText("");
                } else {
                    JOptionPane.showMessageDialog(ITHelpdeskClientGUI.this, "Please enter an Issue ID.");
                }
            }
        });
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000); // Replace "localhost" with the server's IP address
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server: " + e.getMessage());
            System.exit(1);
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
                for (Issue issue : jsonResponse.getIssues()) {
                    issueTextArea.append(issue.toString() + "\n");
                }
            } else {
                JOptionPane.showMessageDialog(this, jsonResponse.getMessage());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error communicating with server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ITHelpdeskClientGUI().setVisible(true);
            }
        });
    }
}