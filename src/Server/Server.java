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

import java.util.Iterator;

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

            System.out.println("Starting server...");
            ServerSocket serverSocket = new ServerSocket(1234); //listens on port 1234 for now

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


                if(clientHandlers.size() == 2)
                {
                    Scanner fileScanner = returnFileScanner("src/WordFile/ProjectTextFile.txt");

                    // Create a list to hold the futures
                    List<CompletableFuture<Integer>> futures = new ArrayList<>();
                    Iterator<ClientHandler> handlerIterator = clientHandlers.iterator();

                    while(fileScanner.hasNextLine())
                    {
                        String line = fileScanner.nextLine();
                    
                        //if we have gone through all the handlers, go back to the first
                        if (!handlerIterator.hasNext())
                        {
                            handlerIterator = clientHandlers.iterator();
                        }
                    
                        // Get the next handler and send the line to it
                        ClientHandler handler = handlerIterator.next();
                    
                        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                            return handler.processRequest(line);
                        });
                        futures.add(future);
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
