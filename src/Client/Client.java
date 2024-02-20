package Client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class Client 
{
    public static void main(String[] args)
    {
        //If you want to hardcode the server IP, you can do it here by replacing the currentIP with the IP
        //as a string, and then commenting out the try/catch block that reads the config file, I just did this
        //because our IP's should not be getting pushed to github.
        Properties prop = new Properties();
        String currentIP = "";

        try
        {
            prop.load(new FileInputStream("config.properties"));
            currentIP = prop.getProperty("server-ip");
        }
        catch(IOException e)
        {
            System.out.println("Issue with reading config file: " + e.getMessage());
        }
        //Now we can set up the socket
        try
        {
            Socket socket = new Socket(currentIP, 1234);
            System.out.println("Connected to server");
        }
        catch(IOException e)
        {
            System.out.println("Issue with connecting to server: " + e.getMessage());
        }
    }
}
