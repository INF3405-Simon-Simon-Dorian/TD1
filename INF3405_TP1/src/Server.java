import java.net.*;
import java.io.*;

public class Server
{
	private static ServerSocket listener;
	
	// Application Serveur
	
	public static void main(String[] args) throws Exception
	{
		// Compteur de connexion
		
		int clientNumber = 0;
		
		// Adresse et port du serveur
		
		String serverAddress = "127.0.0.1";
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
	            
	            out.writeUTF("Hello from server - you are client#" + clientNumber);
	            
	        }
	        catch (IOException e)
	        {
	            System.out.println("Error handling client#" + clientNumber + ": " + e);
	        
	        }
	        
	        finally
	        {
	            try
	            {
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