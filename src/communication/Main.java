package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import display.AskPort;
import display.Display;

public class Main  {
	private int port = 0;	
	private  DatabaseCommunication db ;
	
	private Display display = null;
	private SimulatorsCommunication simulatorStatus;
	public Main(int port) {
		
		this.port = port;
		instanciate();
		Server server = new Server();
		Thread t = new Thread(server);
		t.start();
	
		
	}
	private void instanciate() {
    	db = new DatabaseCommunication();
		display = new Display(db);
		db.addObserver(display);
		display.setVisible(true);
	}
	
	
	
	private class Server implements Runnable {
	
		public void run() {
		    startServer(port,db);
		}
		
		
		
		public void startServer(int port, DatabaseCommunication db) {
			ServerSocket socketserver; // Socket principal du serveur
			Socket socketThread; // Socket attribue au thread qui traitera un capteur
			boolean dataWillCome = true;
			try {
				// Declaration de la ressource
				socketserver = new ServerSocket(port);
				try {
					// Utilisation de la ressource
					socketThread = socketserver.accept();
					simulatorStatus = new SimulatorsCommunication(true,socketThread,socketserver,db);
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
	
	}
	
	public static void main(String[] args) {
		
		
		//fenetre de demande de port  
	    AskPort askPort = new AskPort();
    	askPort.setVisible(true);
    	int port = 0;
    	while((port = askPort.getPort()) == 0) {
	    	try {
	    		Thread.sleep(100); // Wait for 100 mlilliseconds
	    	}catch(InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    }

    	Main main = new Main(port);
//    	Thread t = new Thread(main);
// 	    t.start();
	}
}
