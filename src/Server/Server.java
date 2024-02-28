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
import java.util.concurrent.ExecutionException;

import java.util.Iterator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.net.InetSocketAddress;

public class Server 
{
    /*
     * This code may seem weird, but using InetAddress is inconsistent it seems,
     * especially with macs. So, if we just connect to google, we can get the ip
     * our machine is using on our network
     */


    public static void printIP() throws IOException
    {
        Socket s = new Socket();
        s.connect(new InetSocketAddress("google.com", 80));
        String ip = s.getLocalAddress().getHostAddress();
        s.close();
        System.out.println("Current IP is " + ip);
    }
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

            printIP();

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


                if(clientHandlers.size() == 2) //change this number to whatever the number of clients we want to solve it with
                {
                    ExecutorService executor = Executors.newFixedThreadPool(clientHandlers.size()); //thread pool
                    Scanner fileScanner = returnFileScanner("src/WordFile/TesterExample.txt");

                    // Create a list to hold the futures
                    //List<CompletableFuture<Integer>> futures = new ArrayList<>();
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
                    
                        executor.submit(() ->
                        {
                            handler.sendJob(line);
                        });
                    }

                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                    int total = 0;
                    for (ClientHandler handler : clientHandlers)
                    {
                        handler.sendEndOfJobs();
                        total += handler.getTotal();
                    }
                    System.out.println(total);
                }

            }
            
        }
        catch(IOException e)
        {
            System.out.print("Issue with setting up server: " + e.getMessage());
        }
    }    
}