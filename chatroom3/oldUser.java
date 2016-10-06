import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
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
        int portNumber = Integer.parseInt(args[1]);
        // The default host.
        String host = args[0];
	String cin;

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
	
		while(!userSocket.isClosed()){
		
			cin = inputLine.readLine();
			output_stream.println(cin);

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
	boolean keepListening = true;
	
	try {
		while(keepListening){
		
			chatText = input_stream.readLine(); 
			if(chatText.startsWith("### BYE") && chatText.endsWith("###")){
				keepListening = false;
			}

			System.out.println(chatText);
		} 

		userSocket.close();

	} catch (IOException e){
		e.printStackTrace();
	}


    }
}



