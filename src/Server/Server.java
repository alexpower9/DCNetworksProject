package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.net.InetAddress;

public class Server 
{
    //Will create our scanner to loop through the file and send the lines
    public static Scanner returnFileScanner(String path) throws FileNotFoundException
    {
        
        File file = new File(path);
        
        //file existence check
        if(!file.exists()) throw new FileNotFoundException();
        
        return new Scanner(file);
    }
    public static void main(String[] args) throws InterruptedException, ExecutionException
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
                // for (ClientHandler handler : clientHandlers)
                // {
                //     handler.sendJob("What up boyz welcome");
                // }

                if(clientHandlers.size() == 2)
                {
                    Scanner fileScanner = returnFileScanner("src/WordFile/TesterExample.txt");

                    // Create a list to hold the futures
                    List<CompletableFuture<Integer>> futures = new ArrayList<>();

                    while(fileScanner.hasNextLine())
                    {
                        String line = fileScanner.nextLine();

                        // Send the line to each client asynchronously
                        for (ClientHandler handler : clientHandlers)
                        {
                            // Create a new future for each line and add it to the list
                            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                                return handler.processRequest(line);
                            });
                            futures.add(future);
                        }
                    }

                    // Wait for all futures to complete
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                    // Aggregate the results
                    int total = 0;
                    for (CompletableFuture<Integer> future : futures)
                    {
                        total += future.get();
                    }

                    System.out.println("Total: " + total);
                }

            }
            
        }
        catch(IOException e)
        {
            System.out.print("Issue with setting up server: " + e.getMessage());
        }
    }    
}
