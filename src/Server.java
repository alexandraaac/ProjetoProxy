import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	String firstLine = "";
	String method = "";
	String URL = "";
	String version = "";
	private String host;
    private int port;
	Socket server ;

    final static int MAX_OBJECT_SIZE = 100000;
    byte[] body = new byte[MAX_OBJECT_SIZE];
    String headers1 = "";
    String headers2 = "";

    String statusLine = "";
	String[] tmp ;




	Server(){
		
	}
	@SuppressWarnings("deprecation")
	public void Handle(Socket client) throws IOException {
		

	    		 BufferedReader fromClient = new BufferedReader (new InputStreamReader (client.getInputStream()));
	    		 

	            	readRequisicao(fromClient);
	            	server = new Socket (host, port);
		    		DataOutputStream toServer = new DataOutputStream (server.getOutputStream());
		    		System.out.println(ReqtoString());
	        	    toServer.writeBytes (ReqtoString());
        	   
            if(method.equals("GET")) { 
            	Blocked site = new Blocked();
            	
            	if(site.isURLBlocked(URL) || site.isNAMEBlocked(URL)) {
            		System.out.println("BLOQUEADO");
            		
            		
            	}else {
        	    
        	    DataInputStream fromServer = new DataInputStream (server.getInputStream());
        	    

        	    
        	    Http(fromServer);
       	    DataOutputStream toClient = new DataOutputStream (client.getOutputStream());

            toClient.writeBytes (RestoString());
            toClient.write (body);
            
            	}
            	
             }else {

                 String ConnectResponse = "HTTP/1.0 200 Connection established\n" +
                         "Proxy-agent: ProxyServer/1.0\n" +
                         "\r\n";
            	 DataOutputStream toClient = new DataOutputStream (client.getOutputStream());

                 try
                 {


                     toClient.writeBytes (ConnectResponse);
                     toClient.write (body);
                     } catch(Exception e) {} 

                 
                 DataInputStream inputFromServer = new DataInputStream(server.getInputStream());
                 byte[] receivedData = new byte[1024];
                 while((inputFromServer.read(receivedData, 0, receivedData.length)) != -1)
                 {
                   System.out.println("write response back to client...");
                   System.out.println(new String(receivedData));
                   toClient.write(receivedData);
                   toClient.flush();
                 }

                 System.out.println("done sending");
                 toClient.close();
               }
            client.close();
     		server.close(); 
             
             }
          
         

        	    
           
           
	
        
	

    /** Return host for which this request is intended */

    
   
	public void readRequisicao (BufferedReader fromClient) {
		{String firstLine = "";

		try
	        {
		    firstLine = fromClient.readLine();
		}
	        catch (IOException e)
	        {
		    System.out.println("Error reading request line: " + e);
		}

		tmp = firstLine.split(" ");
		method = tmp [0];

			

			URL = tmp [1];
			version = tmp [2];

			System.out.println("Method is: " + method);
			System.out.println("URI is: " + URL);
			System.out.println("Version is: " + version);

			try
		        {
			    String line = fromClient.readLine();

			    while (line.length() != 0)
		            {
				headers1 += line + "\r\n";

				/* We need to find host header to know which server to
				 * contact in case the request URI is not complete. */
				if (line.startsWith("Host:"))
		                {
					System.out.println("teste");
				    tmp = line.split(" ");

				    /* The method returns the value of the index of
				     * the first occurrence of the character in the 
		 		     * current string.  I the character is not found in
				     * the current string, it returns -1. */
				    if (tmp[1].indexOf(':') > 0)
		                    {
					String[] tmp2 = tmp[1].split(":");
					host = tmp2[0];
					port = Integer.parseInt(tmp2[1]);
				    }
		                    else
		                    {
					host = tmp[1];
					port = 80;
				    }
				}
				line = fromClient.readLine();
			    }
			}
		        catch (IOException e)
		        {
			    System.out.println("Error reading from socket: " + e);
			    return;
			}
			System.out.println("Host to contact is: " + host + " at port " + port);
		}
		
	}
	public void Http(DataInputStream fromServer) {
		System.out.println("1");
		int length = -1;
		boolean gotStatusLine = false;
		System.out.println("2");

		/* First read status line and response headers */
		try
	        {
			System.out.println("3");

			String line = fromServer.readLine();
			System.out.println("4");

		    while (line.length() != 0)
	            {
			if (!gotStatusLine)
	                {
			    statusLine = line;
			    gotStatusLine = true;
			}
	                else
	                {
			    headers2 += line + "\r\n";
			}

			/* Get length of content as indicated by
			 * Content-Length header. Unfortunately this is not
			 * present in every response. Some servers return the
			 * header "Content-Length", others return
			 * "Content-length". You need to check for both
			 * here. */
			if (line.startsWith ("Content-Length:") || line.startsWith ("Content-length:"))
	                {
			    String[] tmp = line.split(" ");
			    length = Integer.parseInt(tmp[1]);
			}
			line = fromServer.readLine();
		    }
		}
	        catch (IOException e)
	        {
		    System.out.println("Error reading headers from server: " + e);
		    return;
		}

		try
	        {
		    int bytesRead = 0;
		    byte buf[] = new byte[8192];
		    boolean loop = false;

		    /* If we didn't get Content-Length header, just loop until
		     * the connection is closed. */
		    if (length == -1)
	            {
			loop = true;
		    }
		    
		    /* Read the body in chunks of BUF_SIZE and copy the chunk
		     * into body. Usually replies come back in smaller chunks
		     * than BUF_SIZE. The while-loop ends when either we have
		     * read Content-Length bytes or when the connection is
		     * closed (when there is no Connection-Length in the
		     * response. */
		    while (bytesRead < length || loop)
	            {
			/* Read it in as binary data */
			int res = fromServer.read (buf, 0, 8192);
			if (res == -1)
	                {
			    break;
			}
			/* Copy the bytes into body. Make sure we don't exceed
			 * the maximum object size. */
			for (int i = 0; i < res && (i + bytesRead) < MAX_OBJECT_SIZE; i++)
	                {
			    body [bytesRead + i] = buf [i];
			}
			bytesRead += res;
		    }
	 	}
	        catch (IOException e)
	        {
	 	    System.out.println("Error reading response body: " + e);
	 	    return;
	 	}
	}
	public void Https (Socket fromServer) {
		
	}
	 public String ReqtoString()
	    {
		String req = "";

		req = method + " " + URL + " " + version + "\r\n";
		req = req + headers1;
		/* This proxy does not support persistent connections */
		req += "Connection: close" + "\r\n";
		req += "\r\n";
		
		return req;
	    } 
	 public String RestoString()
	    {
		String res = "";

		res = statusLine + "\r\n";
		res += headers2;
		res += "\r\n";
		
		return res;
	    }
}
