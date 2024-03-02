package Client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Client 
{
    public static int wordTotal = 0;
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws ClassNotFoundException
    {
        //Now, we can pass it our IP from the command line. The IP will be printed on the servers console, which we can copy
        //and enter into the client console to actually connect to our server
        if(args.length == 1)
        {
            String currentIP = args[0];
            try
            {
                Socket socket = new Socket(currentIP, 1234);
                System.out.println("Connected to server");
                
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                ArrayList<String> serverResponse;
    
                while((serverResponse = (ArrayList<String>) input.readObject()) != null) //we can ignore the warning here since we know the server is sending out lists
                {
                    if(serverResponse != null && serverResponse.get(0).equals("END_OF_JOBS"))
                    {
                        output.println(wordTotal);
                        System.out.println("Sent total word count of " + wordTotal + " to server. Closing connection...");
                        break;
                    }
                    wordTotal += countWords(serverResponse);
                }
            }
            catch(IOException e)
            {
                System.out.println("Issue with connecting to server: " + e.getMessage());
            }
        }
        else
        {
            System.out.println("Please enter an IP into the command line as an arg to connect to the server.");
        }
    }

    public static int countWords(ArrayList<String> lines)
    {
        int count = 0;

        for(String line : lines)
        {
            count += line.trim().split("\\s+").length;
        }

        return count;
    }
}
