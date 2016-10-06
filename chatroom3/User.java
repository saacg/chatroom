import java.io.*;
import java.net.*;
import java.util.*;




public class User extends Thread {
    
    // The user socket
    private static Socket userSocket = null;
    // The output stream
    private static PrintStream output_stream = null;
    // The input stream
    private static BufferedReader input_stream = null;
    
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    
    public static void main(String[] args) {
        
        // The default port.
        int portNumber = 8000;
        // The default host.
        String host = "localhost";
	String input;
	
	if(args.length < 2){
		System.out.println("Running on default host: localhost and default port: 8000");
	}
	else{
		host = args[0];
		portNumber = Integer.parseInt(args[1]);
	}

	/*
         * Open a socket on a given host and port. Open input and output streams.
         */

        //YOUR CODE
	try {
		userSocket = new Socket(host, portNumber);

		input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

		inputLine = new BufferedReader(new InputStreamReader(System.in));

	        output_stream = new PrintStream(userSocket.getOutputStream());				

	
		/*
		 * If everything has been initialized then create a listening thread to 
		 * read from the server. 
		 * Also send any user’s message to server until user logs out.
	     	*/

		// YOUR CODE
		User listenThread = new User();
		listenThread.start();
	
		while(!userSocket.isClosed() &&(input = inputLine.readLine()) != null){
		
			output_stream.println(input);
		}

		

	} catch (IOException e){
		e.printStackTrace();
	}   

    }
    
 
    public void run() {
        /*
         * Keep on reading from the socket till we receive “### Bye …” from the
         * server. Once we received that then we want to break and close the connection.
         */
        

        //YOUR CODE
	String chatText;
	boolean keepRunning = true;
	
	try {
		while(!userSocket.isClosed() && keepRunning && (chatText = input_stream.readLine()) != null) {
		
			if(chatText.startsWith("### BYE") && chatText.endsWith("###")){
				keepRunning = false;
			}

			System.out.println(chatText);
		}

		System.out.println("Press ENTER to exit");
		input_stream.close();
		inputLine.close();
		output_stream.close();
		userSocket.close(); 
		

	} catch (IOException e){
		e.printStackTrace();
	}


    }
}



