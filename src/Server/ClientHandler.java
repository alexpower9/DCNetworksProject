package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Got the ideas for this from the following sources:
 * https://www.geeksforgeeks.org/multithreaded-servers-in-java/
 * https://github.com/citruslogic/client-server/blob/master/ClientHandler.java
 * https://stackoverflow.com/questions/6328125/java-reading-from-a-buffered-reader-from-a-socket-is-pausing-the-thread
 */

public class ClientHandler implements Runnable {
    //each client handler basically takes care of each client
    private Socket socket;
    private ObjectOutputStream output;
    

    public ClientHandler(Socket socket) throws IOException
    {
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
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
    public void sendJob(ArrayList<String> job) throws IOException
    {
        output.writeObject(job);
        output.flush();
        output.reset();
    }

    public void sendEndOfJobs() throws IOException
    {
        // try
        // {
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //     out.println("END_OF_JOBS");
        // }
        // catch(IOException e)
        // {
        //     System.out.println("Error sending job to client: " + e.getMessage());
        // }
        ArrayList<String> endOfJobs = new ArrayList<String>(Arrays.asList("END_OF_JOBS"));
        output.writeObject(endOfJobs);
        output.flush();
        output.reset();
    }

    public String readResponse() throws IOException
    {
        String response = "empty";
        BufferedReader input = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream())); 
        //listens for a response for client

        //we know a response will be coming, so this works here
        while((response = input.readLine()) != null)
        {
            if(!response.equals("empty"))
            {
                return response;
                //when we notice a response, we return it
            }
        }

        return response;
    }
}