import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.util.List;

public class HelpdeskServer {
    private static Helpdesk helpdesk;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        helpdesk = new Helpdesk();
        int port = 5000; // Port to listen on

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Helpdesk Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Received request: " + request);

                    try {
                        JsonRequest jsonRequest = objectMapper.readValue(request, JsonRequest.class);

                        switch (jsonRequest.getCommand()) {
                            case "REPORT":
                                String description = jsonRequest.getData().get("description");
                                String reporterName = jsonRequest.getData().get("reporterName");
                                helpdesk.reportIssue(description, reporterName);
                                out.println(objectMapper.writeValueAsString(new JsonResponse("Issue reported successfully.")));
                                break;
                            case "VIEW":
                                List<Issue> issues = helpdesk.getIssues();
                                out.println(objectMapper.writeValueAsString(new JsonResponse("SUCCESS", issues)));
                                break;
                            case "RESOLVE":
                                int id = Integer.parseInt(jsonRequest.getData().get("id"));
                                helpdesk.resolveIssue(id);
                                out.println(objectMapper.writeValueAsString(new JsonResponse("Issue resolved successfully.")));
                                break;
                            default:
                                out.println(objectMapper.writeValueAsString(new JsonResponse("ERROR", "Invalid command.")));
                        }
                    } catch (IOException e) {
                        System.err.println("Error parsing JSON request: " + e.getMessage());
                        out.println(objectMapper.writeValueAsString(new JsonResponse("ERROR", "Invalid JSON request.")));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}