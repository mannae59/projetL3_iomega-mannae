package communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

import display.AskPort;

public class Main implements Runnable {
	private int port = 0;
	private DatabaseCommunication db;
	public Main(DatabaseCommunication db) {
		this.db = db;
	}
	
	public void run() {
		try {
		    while(port == 0) {
		    	Thread.sleep(100);
		    }
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	    startServer(port,db);
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void startServer(int port, DatabaseCommunication db) {
		ServerSocket socketserver; // Socket principal du serveur
		Socket socketThread; // Socket attribué au thread qui traitera un capteur
		boolean dataWillCome = true;
		try {
			// Declaration de la ressource
			InetAddress adresseServeur = InetAddress.getLocalHost(); // Recuperation de l'adresse de la machine
			System.out.println("Serveur en ligne : " + adresseServeur + ":" + port);
			socketserver = new ServerSocket(port);
			try {
				// Utilisation de la ressource
				socketThread = socketserver.accept();
				SimulatorsCommunication simulatorStatus = new SimulatorsCommunication(true,socketThread,socketserver,db);
				Thread t = new Thread(simulatorStatus);
				t.start();
				while(dataWillCome){
					socketThread = socketserver.accept();
					SimulatorsCommunication lecteur = new SimulatorsCommunication(false,socketThread,socketserver,db);
					Thread t2 = new Thread(lecteur);
					t2.start();
					dataWillCome = t.isAlive();
					// If the first thread is dead, that means the simulator is
					// stopped and no more information will be received
					// In general this boolean won't turn False because the socket
					// is closed before this call so this thread throws an IOException
				}
			}
			finally {	
				socketserver.close();
			}			
		}
		catch (IOException e) {
			
		}
	}
	
	public static void main(String[] args) {		
		// Ouverture de la fenetre de demande du port
		// Initialisation de l'interface
		
    	DatabaseCommunication db = new DatabaseCommunication();
	    
	    Main main = new Main(db);
	    Thread t = new Thread(main);
	    t.start();
	    
//	    SwingUtilities.invokeLater(()->{
//	    	Display display = new Display(db,main);
//	    });
	    SwingUtilities.invokeLater(()->{
	    	AskPort askPort = new AskPort(db,main);
	    	askPort.setVisible(true);
	    });
	    
//		Display display = new Display(db);
//		display.setVisible(true);

	}
}
