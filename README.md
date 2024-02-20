If you want to test your own client/server connection, take the following steps.

1. Clone the repository to your local machine.

2. Add a file named "config.properties" to your local repo, and add the following lines to the file:
```server-ip=Write the server IP here

Make sure the server-ip is NOT a string, just list it as the number. Obviously, you have to actually check to see what the IP address of your machine is first before you canb add it to the file.

However, you could technically hard code it and it add it to the client code, but that's not recommended. If you do, make sure to comment the try catch block in the client code, and you can see on your server that the client is connected via the print statement added.

Overall, the steps to run this are:
1. Run the server file from one machine

2. Adding the config.properties file to your other machine and include: server-ip="Write the server IP here"

3. Run the client file from the other machine

Now you can see the connection works by checking the console of the server.