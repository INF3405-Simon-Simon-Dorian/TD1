import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
	static boolean portGood = false;
	static boolean IPGood = false;
	
	private static Socket socket;
	
	static String askIP(Scanner scan) {
		System.out.println("Veuillez entrer une addresse IP:");
		
		String IP = scan.nextLine();
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
		if(port >= 5000 && port <= 5050) {
			portGood = true;
			return port;
		}
		askPort(scan);
		return 0;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		
		//Objet scanner
		Scanner scanner = new Scanner(System.in);
		
		// Adresse et port du serveur
		
		String serverAddress = "0";
		int serverPort = 5000;
//		boolean lol = false;
		
		// demander ux sont IP/port
		while(!IPGood) {
			serverAddress = askIP(scanner);
		}
		
		while(!portGood) {
			serverPort = askPort(scanner);
		}

		
		
		
		System.out.println(serverAddress);
		System.out.println(serverPort);
		
		// Création d'une connexion avec le serveur
		
		socket = new Socket(serverAddress, serverPort);
		System.out.format("The server is running %s:%d%n", serverAddress, serverPort);
		
		// Création d'un canal pour recevoir les messages d'un serveur
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		// Réception du message et impression
		
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		int he = Integer.valueOf(in.readUTF());
		for(int i =0; i < he; i++) {
			System.out.println(in.readUTF());
		}
		
		// Fermeture de la connexion avec le serveur
		
		socket.close();
	}

}
