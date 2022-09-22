import java.net.*;
import java.io.*;
import java.util.*;


public class Server
{
	static boolean portGood = false;
	static boolean IPGood = false;
	
	private static ServerSocket listener;
	
	static String askIP(Scanner scan) {
		System.out.println("Veuillez entrer une addresse IP:");
		
		String IP = scan.nextLine();
//		String IP = "10.200.29.155";
		int count = 0;
		String[] str = IP.split("\\.");
		
		if(str.length != 4) {
			return "0.0.0.0";
		}
		for(String a: str) {
			int octet = Integer.valueOf(a);
			
			if(octet < 255 && octet > 0){
				count++;
				if(count == 4) {
					System.out.println(IP);
					IPGood = true;
					return IP;
				}
			}
		}
		return "0.0.0.0";
	}
	
	static int askPort(Scanner scan){
		System.out.println("Veuillez entrer un port entre 5000 et 5050:");
		int port = Integer.valueOf(scan.nextLine());
//		int port = 5000;
		if(port >= 5000 && port <= 5050) {
			System.out.println(port);
			portGood = true;
			return port;
		}
		askPort(scan);
		return 0;
	}
	

	// Application Serveur
	
	public static void main(String[] args) throws Exception
	{
		// Compteur de connexion
		
		int clientNumber = 0;
		
		// Adresse et port du serveur
		
//		String serverAddress = "10.200.29.155";
		String serverAddress = "0.0.0.0";
//		int serverPort = 5000;
		int serverPort = 0; 
		
		Scanner scanner = new Scanner(System.in);
		
		while(!IPGood) {
			serverAddress = askIP(scanner);
		}
		
		while(!portGood) {
			serverPort = askPort(scanner);
		}
		// Création d'une connexion avec les clients
		
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		
		// Association de l'adresse et du port
		
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running %s:%d%n", serverAddress, serverPort);
		
		try
		{
			// Pour chaque connexion d'un client
			
			while(true)
			{
				// On attend le prochain client
				// Note : la fonction accept est bloquante
				
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		}
		finally
		{
			// Fermeture de la connexion avec le client
			
			listener.close();
		}
	}
	
	private static class ClientHandler extends Thread
	{
	    private Socket socket;
	    private int clientNumber;
		// creer un hashmap pour les usernames / mdp
		
		HashMap<String, String> clientData = new HashMap<String, String>();
		
	    public ClientHandler(Socket socket, int clientNumber)
	    {
	        this.socket = socket;
	        this.clientNumber = clientNumber;
	        
	        System.out.println("New connection with client#" + clientNumber + " at" + socket);
	        
	        
	    }
	    
	    public void run()
	    {
	        try
	        {
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	            DataInputStream in = new DataInputStream(socket.getInputStream());
	            
	            out.writeUTF("Hello from server - you are client#" + clientNumber);

	            
	            
	            
	            
	            String username = in.readUTF();
	            String password = in.readUTF();
	            
	            String line;
	            
	            while ((line = in.readUTF()) != null) {
//	               out.writeUTF(line);
	            	System.out.println(line);
	               
	            }
	            
	            
	            clientData.put("peepoo", "peepoo");
	            
	            if(clientData.isEmpty()) {
	            	clientData.put(username, password);
	            	out.writeUTF("Account created under name: " + username);
	            }
	            else{
	            	if(clientData.containsKey(username)) {
	            		if(clientData.get(username) == password) {
	            		out.writeUTF("account validated");
	            	}
	            	else {
	            		out.writeUTF("Invalid username or password.");
	            	}
	            }
	            
	            System.out.println("The username that you have entered is:" + username + "\n" + "The password is: " + password);
	            
	            
	            }
	        }
	        catch (IOException e)
	        {
	            System.out.println("Error handling client#" + clientNumber + ": " + e);
	        
	        }
	        
	        finally
	        {
	            try
	            {
//	            	socket.setKeepAlive(isAlive());
	                socket.close();
	            }
	            catch(IOException e)
	            {
	                System.out.println("could not close a socket");
	                
	            }
	            System.out.println("Connection with client#" + clientNumber + "closed");
	        }
	        
	    }
	}

}