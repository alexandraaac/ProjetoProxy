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
		
		ProxyCacheThread request = new ProxyCacheThread(client);

		// Criar uma  nova thread para processar a requisição.
		Thread thread = new Thread(request);
		//Iniciar a thread.
		thread.start();
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
class ProxyCacheThread implements Runnable
{
	private Socket client = null;
	
	public ProxyCacheThread(Socket socket)
	{
		this.client = socket;
	}
	@Override
	public void run() {
		Server server = new Server(); 
		try {
			server.Handle(client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
