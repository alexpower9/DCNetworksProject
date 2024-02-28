package Client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Client 
{
    public static int wordTotal = 0;
    public static void main(String[] args)
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
                
                BufferedReader input = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
                String serverResponse;
    
                while((serverResponse = input.readLine()) != null)
                {
                    wordTotal += countWords(serverResponse);
                    System.out.println("Total words so far: " + wordTotal);
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

    public static int countWords(String line)
    {
        int count = line.trim().split("\\s+").length;

        return count;
    }
}
