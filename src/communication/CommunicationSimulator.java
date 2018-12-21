package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import update.Observable;
import update.Observer;

public class CommunicationSimulator implements Runnable, Observable {
	private List<Observer> tabObserver;
	Socket socketAtraiter;
	   
	public void run() {
		try {
            System.out.println("Thread " + Thread.currentThread().getId() + ": Initializing...");
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
			try {
				boolean dataWillCome = true;
			      while (dataWillCome) {
			        try
			        {
			          String input = in.readLine();
			          if (input != null) {
			            System.out.println("Thread " + Thread.currentThread().getId() + ": " +  input);
			            if(input.startsWith("Deconnexion")) {
			            	dataWillCome = false;
			            }
			          }
			        }
			        catch (SocketException se)
			        {
			        	System.out.println("Thread " + Thread.currentThread().getId() + ": Connection closed.");
			        	dataWillCome = false;
			        }
			      }
			} finally {
				socketAtraiter.close();
			}
	}catch (IOException e) {
		e.printStackTrace();
	}
	}
	
	public void listeningAPI() {
		// TODO Complete this method
	}
	public void connectionSensor() {
		// TODO Complete this method
	}
	public void deconnectionSensor() {
		// TODO Complete this method
	}
	public void dataSensor() {
		// TODO Complete this method
	}
	public void addObserver(Observer o) {
		// TODO Complete this method
	}
	public void notifyObserver(Observer o) {
		// TODO Complete this method
	}
	public void deleteObserver(Observer o) {
		// TODO Complete this method
	}
	
	public static void main(String[] args) {
		ServerSocket socketserver; // Socket principal du serveur
		Socket socketThread; // Socket attribué au thread qui traitera un capteur
		try {
			// Declaration de la ressource
			InetAddress adresseServeur = InetAddress.getLocalHost();
			int port = 5678;
			System.out.println("Serveur en ligne : " + adresseServeur + ":" + port); 
			socketserver = new ServerSocket(port);
			try {
				// Utilisation de la ressource
				// Je n'ai pas encore trouve de bonne valeur pour arreter la boucle
				// Ici on suppose connaitre d'avance le nombre de capteurs
				for(int i = 0; i < 4;i++) {

					socketThread = socketserver.accept();
					CommunicationSimulator lecteur = new CommunicationSimulator();
					lecteur.socketAtraiter = socketThread;
					Thread t = new Thread(lecteur);
					t.start();
					// N.B : on pourrait gerer le timeout de accept() et
					// En deduire qu'il n'y a plus de nouveau capteur
				}
			}
			finally {	
				socketserver.close();
			}			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
