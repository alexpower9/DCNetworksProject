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
        // try
        // {
        //     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        //     String request;
        //     int response;
        //     while((request = in.readLine()) != null)
        //     {
        //         //proccess all the requests from the server
        //         response = sendJob(request);
        //         out.println(response);
        //     }
        // } 
        // catch(IOException e)
        // {
        //     System.out.println("Error handling client: " + e.getMessage());
        // }
    }

    //use this to see if we can send simple messages
    public void sendJob(String job)
    {
        // try
        // {
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //     out.println(job);
        // }
        // catch(IOException e)
        // {
        //     System.out.println("Error sending job to client: " + e.getMessage());
        // }
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

    public int getTotal() throws IOException
    {
        return Integer.parseInt(in.readLine());
    }
}