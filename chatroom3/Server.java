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
        int portNumber = 8000;
	int threadCount = 0;

	if(args.length > 0){
		portNumber = Integer.parseInt(args[0]);
	}
       	
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

        	//YOUR CODE
        	if(threadCount < maxUsersCount){
		    	userSocket = serverSocket.accept();
		    	threads[threadCount] = new userThread(userSocket, threads);
		   	threads[threadCount].start();
		}
		threadCount++;


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
	    input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
	    output_stream = new PrintStream(userSocket.getOutputStream());
 	
	    output_stream.println("Now using host = " + userSocket.getLocalAddress().getHostName() + ", " + userSocket.getLocalPort());
	    output_stream.println("Enter your name: ");
	    userName = input_stream.readLine();
	    while(userName.startsWith("@")){
		synchronized(writeLock){
			output_stream.println("Invalid username. Usernames may not start with '@' ");
			userName = input_stream.readLine();
		}

	    }
	    output_stream.println("Welcome, " + userName + " to our chat room. \nTo leave enter LogOff on a new line.");

	    synchronized(writeLock){
		for(userThread user : threads){
		    if(user != null && user.userName != this.userName){
			user.output_stream.println("*** A new user " + this.userName + " entered the chat room!!! *** ");
		    }
		} 
	    }

	    while((input = input_stream.readLine()) != null && !input.equals("LogOff")){
		if(input.startsWith("@")){
			for(userThread user : threads){
				if(user!= null && "@".concat(user.userName).equals(input.substring(0, input.indexOf(" ")))){
					synchronized(writeLock){
						user.output_stream.println("<" + this.userName + ">" + input.substring(input.indexOf(" ") + 1));
						this.output_stream.println("<" + this.userName + ">" + input.substring(input.indexOf(" ") + 1));
						break;
					}
				} 
			}
				
		}
		else{
			synchronized(writeLock){
		    		for(userThread user : threads){
					if(user != null){
			   			 user.output_stream.println("<" + this.userName + ">" + input);
					}
				}
		    	}				
		}
	    }

	    output_stream.println("### BYE " + userName + " ###");
	    
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


