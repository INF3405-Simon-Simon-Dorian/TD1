import java.net.*;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

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
	
	static String askImage(Scanner scan) {
		System.out.println("Veuillez rentrer le nom de l'image à traiter: \n");
		String imageName = scan.nextLine();
		return imageName;
	}
	
	static String askNewImageName(Scanner scan) {
		System.out.println("Quel nom voulez-vous donner à la nouvelle image ? \n");
		String newImageName = scan.nextLine();
		return newImageName;
	}
	
	static boolean checkConnexionMessage(String message) {
		if (message.equals("Connexion échouée"))
			return false;
		return true;
	}
	
	static void sendImage(DataOutputStream out, String imageName) throws IOException {
		try {
			BufferedImage image = ImageIO.read(new File(imageName + ".jpg"));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(image, "jpg", byteArrayOutputStream);
	        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	        out.write(size);
	        out.write(byteArrayOutputStream.toByteArray());
	        // out.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}
	
	static void receiveImage(DataInputStream in, String newImageName) throws IOException {
    	byte[] sizeAr = new byte[4];
        in.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        in.read(imageAr);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
        BufferedImage imageSobel = Sobel.process(image);

        ImageIO.write(imageSobel, "jpg", new File(newImageName + ".jpg"));
    }
	
	public static void main(String[] args) throws Exception
	{
		
		Scanner scanner = new Scanner(System.in);
		
		String serverAddress = "0.0.0.0";
		int serverPort = 5000;
		
		
		String username = "";
		String password = "";

		// On demande à l'utilisateur l'adresse IP et le port tant qu'ils ne sont pas corrects
		while(!IPGood) {
			serverAddress = askIP(scanner);
		}
		while(!portGood) {
			serverPort = askPort(scanner);
		}
		
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
		
		// Si le mot de passe est mauvais, on déconnecte le client
		
		if (!checkConnexionMessage(connexionMessage)) {
			socket.close();
			return;
		}
		
		// Sinon, on lui demande l'image à traiter
		
		String imageName = askImage(scanner);
		String newImageName = askNewImageName(scanner);
		
		sendImage(out, imageName);
		receiveImage(in, newImageName);
		
		// Fermeture de la connexion avec le serveur
		
		socket.close();
	}

}
