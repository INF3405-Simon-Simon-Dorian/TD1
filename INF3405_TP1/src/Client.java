import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class Client
{
	static boolean portGood = false;
	static boolean IPGood = false;
	static boolean nomImageGood = false;
	static boolean nomNewImageGood = false;
	
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
	
	static String askUsername(Scanner scan) {
		System.out.println("Veuillez rentrer votre nom d'utilisateur:");
		String username = scan.nextLine();
		return username;
	}
	
	static String askPassword(Scanner scan) {
		System.out.println("Veuillez rentrer votre mot de passe:");
		String password = scan.nextLine();
		return password;
	}
	
	static String askImage(Scanner scan) {
		System.out.println("Veuillez rentrer le nom de l'image à traiter:");
		String imageName = scan.nextLine();
		if (imageName.split("\\.").length == 2)
			nomImageGood = true;
		return imageName;
	}
	
	static String askNewImageName(Scanner scan) {
		System.out.println("Quel nom voulez-vous donner à la nouvelle image ?");
		String newImageName = scan.nextLine();
		if (newImageName.split("\\.").length == 2)
			nomNewImageGood = true;
		return newImageName;
	}
	
	static boolean checkConnexionMessage(String message) {
		if (message.equals("Connexion échouée"))
			return false;
		return true;
	}
	
	static void sendImage(DataOutputStream out, String imageName) throws IOException {
		try {
			String extensionImage = imageName.split("\\.")[1];
			BufferedImage image = ImageIO.read(new File(imageName));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(image, extensionImage, byteArrayOutputStream);
	        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	        out.write(size);
	        out.write(byteArrayOutputStream.toByteArray());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}
	
	static void receiveImage(DataInputStream in, String newImageName) throws IOException {
		String extensionImage = newImageName.split("\\.")[1];
    	byte[] sizeAr = new byte[4];
        in.read(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        in.read(imageAr);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
        BufferedImage imageSobel = Sobel.process(image);

        ImageIO.write(imageSobel, extensionImage, new File(newImageName));
    }
	
	static Path getImagePath(String imageName) {
		Path path = Paths.get(imageName);
		return path.toAbsolutePath();
	}
	
	public static void main(String[] args) throws Exception
	{
		
		Scanner scanner = new Scanner(System.in);
		
		String serverAddress = "0.0.0.0";
		int serverPort = 5000;
		
		
		String username = "";
		String password = "";
		String imageName = "";
		String newImageName = "";

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
		
		while(!nomImageGood) {
			imageName = askImage(scanner);
		}
		while(!nomNewImageGood) {
			newImageName = askNewImageName(scanner);
		}
		
		out.writeUTF(imageName);
		sendImage(out, imageName);
		System.out.println("L'image " + imageName + " a bien été envoyée pour le traitement.");
		receiveImage(in, newImageName);
		System.out.println("L'image traitée " + newImageName + " a bien été reçue.\nElle se trouve ici : " + getImagePath(newImageName));
		
		// Fermeture de la connexion avec le serveur
		
		socket.close();
	}

}
