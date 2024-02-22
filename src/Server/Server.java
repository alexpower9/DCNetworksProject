package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.InetAddress;

public class Server 
{
    
    public static void main(String[] args)
    {
        try
        {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Current ip is " + ip.getHostAddress()); //whatver IP is printed here is what the client should use to connect to the server


            System.out.println("Trying to start server...");
            ServerSocket serverSocket = new ServerSocket(1234); //listens on port 1234 for now
            //it would good for us to use the command line args for this, I think wed get a better grade

            List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
            //synchronized list is necessary since we are using multithreading (I dont really understand it all too well)

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                //start a new thread for the client
                new Thread(clientHandler).start();

                System.out.println(clientHandlers.size() + " clients connected");

                //This will send a "job" or message to all the clients once someone joins, just to show how the connection works
                for (ClientHandler handler : clientHandlers) {
                    handler.sendJob("What up boyz welcome");
                }

            }
            
        }
        catch(IOException e)
        {
            System.out.print("Issue with setting up server: " + e.getMessage());
        }
    }    
}
