import java.net.*;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class Server
{
	private static ServerSocket listener;
	
	static boolean portGood = false;
	static boolean IPGood = false;

	// Application Serveur
	
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
		// Compteur de connexion
		
		int clientNumber = 0;
		
		// Adresse et port du serveur
		
		String serverAddress = "10.200.42.170";
		int serverPort = 5000;
		
		// Création d'une connexion avec les clients
		
		Scanner scanner = new Scanner(System.in);

		while(!IPGood) {
			serverAddress = askIP(scanner);
		}

		while(!portGood) {
			serverPort = askPort(scanner);
		}
		
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
        HashMap<String, String> database = new HashMap<String, String>();
		
	    public ClientHandler(Socket socket, int clientNumber)
	    {
	        this.socket = socket;
	        this.clientNumber = clientNumber;
	        
	        System.out.println("New connection with client#" + clientNumber + " at" + socket);
	        
	    }
	    
	    public boolean checkUsername(String username, String password) {
	    	if(database.containsKey(username)) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public boolean checkPassword(String username, String password) {
			if(database.get(username).equals(password)) {
				return true;
			}
	    	return false;
	    }
	    
	    public BufferedImage processImage(DataInputStream in) throws IOException {
	    	socket.getOutputStream().flush();
	    	BufferedImage image = ImageIO.read(socket.getInputStream());
	        BufferedImage imageSobel = Sobel.process(image);
	        
	        return imageSobel;
	    }
	    
	    public void sendNewImage(DataOutputStream out, BufferedImage image) throws IOException {
			try {
		        ImageIO.write(image, "jpg", socket.getOutputStream());
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
		}
	    
	    public void run()
	    {
	        try
	        {
	            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	            DataInputStream in = new DataInputStream(socket.getInputStream());
	            
	            out.writeUTF("Hello from server - you are client#" + clientNumber);

	            File yourFile = new File("database.txt");
                yourFile.createNewFile(); // if file already exists will do nothing 

	            BufferedReader bf = new BufferedReader(new FileReader("database.txt"));
               
                // read entire line as string
                String line = bf.readLine();
               
                // checking for end of file
                while (line != null) {
                	String[] arr = line.split(",");
                	database.put(arr[0], arr[1]);
                    line = bf.readLine();
                }
                
	            bf.close();
	               
	            String username = in.readUTF();
	            String password = in.readUTF();
	            
	            if(checkUsername(username, password) && checkPassword(username, password))
	            	out.writeUTF("Connexion réussie");
	            else if (checkUsername(username, password) && !checkPassword(username, password)) {
	            	out.writeUTF("Connexion échouée");
	            	socket.close();
	            }
	            else {
	            	out.writeUTF("Tu es nouveau, nous allons te créer un comptes");
	            	// Open the file in append mode.
	                FileWriter fw = new FileWriter("database.txt",true);
	                PrintWriter sortie = new PrintWriter(fw);

	                sortie.println(username + "," + password);
	                fw.close();
	            }
	            
	            String imageName = in.readUTF();
	            String extensionImage = imageName.split("\\.")[1];
	            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
	            LocalDateTime now = LocalDateTime.now();
	            System.out.println("[" + username + " - " + socket.getLocalAddress()
	            		+ ":" + socket.getPort() + " - " + dtf.format(now) +"] : Image "
	            		+ imageName + " reçue pour traitement.");
	            BufferedImage imageToSend = processImage(in);
		        ImageIO.write(imageToSend, extensionImage, socket.getOutputStream());
	       
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