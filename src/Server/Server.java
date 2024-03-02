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
            //synchronized list is necessary since we are using multithreading (no race condition here), only
            //one thread can access the list at a time, which should guarantee all the lines get sent out properly
            //later on

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                System.out.println(clientHandlers.size() + " clients connected");
              
                if(clientHandlers.size() == 2) //change this number to whatever the number of clients we want to solve it with
                {
                    //Start a timer to see how long it takes
                    long startime = System.currentTimeMillis();

                    ExecutorService executor = Executors.newFixedThreadPool(clientHandlers.size()); //thread pool
                    Scanner fileScanner = returnFileScanner("src/WordFile/ProjectTextFile.txt"); //change this path for whatever file you want to count

                    int batchSize = 20;
                    ArrayList<String> lines = new ArrayList<>(batchSize);

                    Iterator<ClientHandler> handlerIterator = clientHandlers.iterator();

                    while(fileScanner.hasNextLine())
                    {
                        for (int i = 0; i < batchSize && fileScanner.hasNextLine(); i++)
                        {
                            lines.add(fileScanner.nextLine());
                        }
                    
                        //if we have gone through all the handlers, go back to the first
                        if (!handlerIterator.hasNext())
                        {
                            handlerIterator = clientHandlers.iterator();
                        }
                    
                        //go to the next handler
                        ClientHandler handler = handlerIterator.next();
                    
                        executor.submit(() ->
                        {
                            try 
                            {
                                handler.sendJob(lines);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        });
                    }

                    executor.shutdown();
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //wait for all threads to finish sending their lines out to clients
                    System.out.println("\n*****All lines have been sent to clients*****\n");

                    int totalWords = 0;

                    //let clients know that the lines are done sending, get their response and add it
                    for (ClientHandler handler : clientHandlers)
                    {
                        handler.sendEndOfJobs();
                        String clientResponse = handler.readResponse();
                        totalWords += Integer.parseInt(clientResponse);

                        System.out.println("Recieved a total of " + clientResponse + " words from client " + (clientHandlers.indexOf(handler) + 1));
                    }

                    long endTime = System.currentTimeMillis();
                    double elaspedTimeInSeconds = (double) (endTime - startime) / 1000; //convert to seconds

                    System.out.println("\nTotal words: " + totalWords);

                    System.out.println("The total time it took in seconds is " + elaspedTimeInSeconds);

                 }
            }
            
        }
        catch(IOException e)
        {
            System.out.print("Issue with setting up server: " + e.getMessage());
        }
    }    
}