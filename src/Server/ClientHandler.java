package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Got the ideas for this from the following sources:
 * https://www.geeksforgeeks.org/multithreaded-servers-in-java/
 * https://github.com/citruslogic/client-server/blob/master/ClientHandler.java
 * https://stackoverflow.com/questions/6328125/java-reading-from-a-buffered-reader-from-a-socket-is-pausing-the-thread
 */

public class ClientHandler implements Runnable {
    //each client handler basically takes care of each client, Runnable creates a new thread
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) throws IOException
    {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket()
    {
        return socket;
    }

    @Override
    public void run()
    {
        
    }

    //use this to see if we can send simple messages
    public void sendJob(String job)
    {
        this.out.println(job);
    }

    public void sendEndOfJobs()
    {
        try
        {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("END_OF_JOBS");
        }
        catch(IOException e)
        {
            System.out.println("Error sending job to client: " + e.getMessage());
        }
    }

    public String readResponse() throws IOException
    {
        String response = "empty";
        BufferedReader input = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

        while((response = input.readLine()) != null)
        {
            if(!response.equals("empty"))
            {
                return response;
            }
        }

        return response;
    }
}