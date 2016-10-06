import java.io.*;
import java.util.*;
import java.net.*;



/*
 * A chat server that delivers public and private messages.
 */
public class Server {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket userSocket = null;
    // Maximum number of users 
    private static int maxUsersCount = 5;
    // An array of threads for users
    private static userThread[] threads = null;
    
    public static void main(String args[]) {
        
        // The default port number.
        int portNumber = Integer.parseInt(args[0]);
	int threadCount = 0;
       	
	try{	
	    serverSocket = new ServerSocket(portNumber);
	
	    threads = new userThread[maxUsersCount];
	    //YOUR CODE
	    System.out.println("Now using port number = " + portNumber + "\nMaximum user count = " + maxUsersCount);


	    /*
	     * Create a user socket for each connection and pass it to a new user
	     * thread.
	     */
	    while (true) {

		userSocket = serverSocket.accept();
        	//YOUR CODE
		//checks whether the max user capacity has been reached. If not, start a child process to handle the input/output of the User
        	if(threadCount < maxUsersCount){
		    	threads[threadCount] = new userThread(userSocket, threads);
		   	threads[threadCount].start();
			threadCount++;
		} 
		else{ // if the max user capacity has been reached, notify the User and send the "BYE" message so the User will close the socket
			PrintStream overflow_output = new PrintStream(userSocket.getOutputStream());
			overflow_output.println("Sorry, chat server has reached it's capacity and can accept no more connections");
			overflow_output.println("### BYE ###");
		}
		


	    }
	} catch(IOException e){
	    e.printStackTrace();
	}
										

    }


    
}

/*
 * Threads
 */
class userThread extends Thread {
    
    private String userName = null;
    private BufferedReader input_stream = null;
    private PrintStream output_stream = null;
    private Socket userSocket = null;
    private final userThread[] threads;
    private int maxUsersCount;
    private String input;
    private static Object writeLock = new Object();

    // only relevant for Part IV: adding friendship
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    //

    
    public userThread(Socket userSocket, userThread[] threads) {
        this.userSocket = userSocket;
        this.threads = threads;
        maxUsersCount = threads.length;
    }
    
    public void run() {

	/*
	 * Create input and output streams for this client, and start conversation.
	 */
	try {
	    //YOUR CODE

	    boolean notUnique = true;
	    String tryName = null;
	    input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
	    output_stream = new PrintStream(userSocket.getOutputStream());
 	    
	    synchronized(writeLock){
		    output_stream.println("Now using host = " + userSocket.getLocalAddress().getHostName() + ", and port: " + userSocket.getLocalPort());
	    }
		
	    //checks to make sure userName is unique
	    while(notUnique){
		synchronized(writeLock){
			output_stream.println("Enter your name: ");
		}
	    	tryName = input_stream.readLine();
		notUnique = false;
		for(userThread user : threads){
			if(user != null && user.userName != null && user.userName.equals(tryName)){
				notUnique = true;
				synchronized(writeLock){
					output_stream.println(tryName + " is already taken. Please choose another username");
				}
				break;
			}
		}
	    }
	   
           this.userName = tryName;

	   //welcome the user to the chatroom 
	   synchronized(writeLock){
		output_stream.println("Welcome, " + userName + " to our chat room. \nTo leave enter LogOff on a new line.");
	   }

		
	   // broadcast User's entry to the rest of the chatroom
	    synchronized(writeLock){
		for(userThread user : threads){
		    if(user != null && user.userName != this.userName){
			user.output_stream.println("*** A new user " + this.userName + " entered the chat room!!! *** ");
		    }
		} 
	    }


	   // take in user input and broadcast it to chatroom until user signals to log off
	    while((input = input_stream.readLine()) != null && !input.equals("LogOff")){
		synchronized(writeLock){
		    for(userThread user : threads){
			if(user != null){
			    user.output_stream.println("<" + this.userName + ">" + input);
			}
		    }
		}
	    }

	    synchronized(writeLock){
	    	output_stream.println("### BYE " + userName + " ###");
	    }

	    synchronized(writeLock){
		for(userThread user : threads){
		    if(user != null && user.userName != this.userName){
			user.output_stream.println("*** The user " + this.userName + " is leaving the chat room!!! *** ");
		    } 
		    
		} 
		
			
	    }

 	} catch (IOException e){
	    e.printStackTrace();
	}
    }

}


