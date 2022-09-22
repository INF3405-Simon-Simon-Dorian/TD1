import java.net.*;
import java.io.*;
import java.util.*;


public class Server
{
	private static ServerSocket listener;
	

	// Application Serveur
	
	public static void main(String[] args) throws Exception
	{
		// Compteur de connexion
		
		int clientNumber = 0;
		
		// Adresse et port du serveur
		
		String serverAddress = "10.200.29.155";
		int serverPort = 5000;
		
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