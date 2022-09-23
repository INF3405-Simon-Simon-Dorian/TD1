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
		if(port >= 5000 && port <= 5050) {
			System.out.println(port);
			portGood = true;
			return port;
		}
		askPort(scan);
		return 0;
	}
	
	static String askUsername(Scanner scan) {
		System.out.println("Veuillez rentrer votre nom d'utilisateur: ");
		String username = scan.nextLine();
		return username;
	}
	
	static String askPassword(Scanner scan) {
		System.out.println("Veuillez rentrer votre mot de passe: \n");
		String password = scan.nextLine();
		return password;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		
		//Objet scanner
		Scanner scanner = new Scanner(System.in);
		
		// Adresse et port du serveur
		
		String serverAddress = "0.0.0.0";
		int serverPort = 5000;
		
		// username et pw
		
		String username = "";
		String password = "";

		
		// demander ux sont IP/port
		while(!IPGood) {
			serverAddress = askIP(scanner);
		}
		
		while(!portGood) {
			serverPort = askPort(scanner);
		}
		
		// demander username + pw
		
		username = askUsername(scanner);
		password = askPassword(scanner);
		
		// Création d'une connexion avec le serveur
		
		socket = new Socket(serverAddress, serverPort);
		System.out.format("The server is running %s:%d%n", serverAddress, serverPort);
		
		// Création d'un canal pour recevoir les messages d'un serveur
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		
		// Réception du message et impression
		
		String helloMessageFromServer = in.readUTF();

		out.writeUTF(username);
		out.writeUTF(password);
		
		System.out.println(helloMessageFromServer);
		
		String connexionMessage = in.readUTF();
		System.out.println(connexionMessage);
		
		
		// Fermeture de la connexion avec le serveur
		
		socket.close();
	}

}
