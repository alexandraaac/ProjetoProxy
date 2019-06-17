import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main { 
    /** Socket for client connections */
    private static ServerSocket socket;
    /** Port for the proxy */
    private static int port;
    
	public static void main(String args[])
    {
	int myPort = 0;
	
	try
        {
	    myPort = 4533;
	}
        catch (ArrayIndexOutOfBoundsException e)
        {
	    System.out.println("Need port number as argument");
	    System.exit(-1);
	}
        catch (NumberFormatException e)
        {
	    System.out.println("Please give port number as integer.");
	    System.exit(-1);
	}
	
	init(myPort);
        System.out.println("Ready and listening at port: " + myPort + "\n\n");    

	/** Main loop. Listen for incoming connections and spawn a new
	 * thread for handling them */
	Socket client = null;
	
	while (true)
        {
	    try
            {
		client = socket.accept();
		Server server = new Server();
		server.Handle(client);
		}
            catch (IOException e)
            {
		System.out.println("Error reading request from client: " + e);
		/* Definitely cannot continue processing this request,
		 * so skip to next iteration of while loop. */
		continue;
	    }
	}

    }
	 public static void init(int p)
	    {
		port = p;

		try
	        {
		    socket = new ServerSocket (port);
		}
	        catch (IOException e)
	        {
		    System.out.println("Error creating socket: " + e);
		    System.exit(-1);
		}
	    }
}
