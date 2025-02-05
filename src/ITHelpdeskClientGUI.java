import javax.swing.*;

public class ITHelpdeskClientGUI extends JFrame {
    public ITHelpdeskClientGUI() {
        setTitle("IT Helpdesk Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add your existing GUI components here
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true); // Start with the login window
            }
        });
    }
}