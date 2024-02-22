package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request, response;
            while ((request = in.readLine()) != null) {
                // Process the request and generate a response
                response = processRequest(request);
                out.println(response);
            }
        } catch(IOException e)
        {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    public void sendJob(String job)
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(job);
        }
        catch(IOException e)
        {
            System.out.println("Error sending job to client: " + e.getMessage());
        }
    }

    private String processRequest(String request)
    {
        // Process the request and return a response
        // This is where you would put your code to process the request
        return "Processed request: " + request;
    }
}