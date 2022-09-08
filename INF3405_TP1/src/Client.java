import java.net.*;
import java.io.*;

public class Client
{
	private static Socket socket;
	
	// Application client
	
	public static void main(String[] args) throws Exception
	{
		// Adresse et port du serveur
		
		String serverAddress = "127.0.0.1";
		int serverPort = 5000;
		
		// Création d'une connexion avec le serveur
		
		socket = new Socket(serverAddress, serverPort);
		System.out.format("The server is running %s:%d%n", serverAddress, serverPort);
		
		// Création d'un canal pour recevoir les messages d'un serveur
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		// Réception du message et impression
		
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		// Fermeture de la connexion avec le serveur
		
		socket.close();
	}

}
