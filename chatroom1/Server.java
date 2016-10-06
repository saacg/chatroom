import java.io.*;
import java.net.*;

class Client {

	public static void main(String argv[]) throws Exception{

		// The default port.
        	int portNumber = 8000;
       	        // The default host.
        	String host = "localhost";
      		String responseLine;
        
        	if (argv.length < 2) {
            		System.out.println("Usage: java User <host> <portNumber>\n"
                             + "Now using host=" + host + ", portNumber=" + portNumber);
        	}
		else {
            		host = argv[0];
            		portNumber = Integer.parseInt(argv[1]);
		}
	
		String sentence;
		String returnSentence;
		try{		
			// create input stream
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

			// create client socket and connect to server
			Socket clientSocket = new Socket(host, portNumber);

			// create output stream attacked to the socket
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

			// create input stream attacked to the socket
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// get user input
			sentence = inFromUser.readLine();

			// send it to server
			outToServer.writeBytes(sentence + '\n');
		
			//receive response from server
			returnSentence = inFromServer.readLine();

			System.out.println("FROM SERVER: " + returnSentence);

			// close the socket
			outToServer.close();
			inFromServer.close();
			clientSocket.close();
		} catch(IOException e){
			e.printStackTrace();
		}

	}

}
