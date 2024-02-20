package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
    private static void handleClient(Socket clientSocket)
    {
        //This is where we will hand out jobs to the clients 
    }
    public static void main(String[] args)
    {
        try
        {
            System.out.println("Trying to start server...");
            ServerSocket serverSocket = new ServerSocket(1234); //listens on port 1234 for now
            //it would good for us to use the command line args for this, I think wed get a better grade

            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress()); //see who connected
                new Thread(() -> handleClient(clientSocket)).start(); //handle a connect in a new thread
            }
            
        }
        catch(IOException e)
        {
            System.out.print("Issue with setting up server: " + e.getMessage());
        }
    }    
}
