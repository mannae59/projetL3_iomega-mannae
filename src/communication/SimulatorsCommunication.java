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

public class SimulatorsCommunication implements Runnable, Observable {
	private List<Observer> tabObserver;
	Socket socketAtraiter;
	ServerSocket socketServer;
	boolean firstCall;
	boolean dataWillCome = true;
	
	public SimulatorsCommunication(boolean firstCall) {
		this.firstCall = firstCall;
	}
	
	public void run() {
		if(firstCall) {
			listeningAPI();
		}else {
			readingSensorData();
		}
	}
	
	private void listeningAPI() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
			try {
			      while (dataWillCome) {
			        try
			        {
			          String input = in.readLine();
			          if (input != null) {
			            if(input.startsWith("Deconnexion")) {
			            	dataWillCome = false;
			            }
			          }
			        }
			        catch (SocketException se)
			        {
			        	dataWillCome = false;
			        	if(socketServer != null) {
			        		socketServer.close();
			        	}
			        }
			      }
			} finally {
				socketAtraiter.close();
			}
	}catch (IOException e) {
		e.printStackTrace();
	}
	}
	public void sensorConnected() {
		// TODO Complete this method
	}
	public void sensorDisconnected() {
		// TODO Complete this method
	}
	private void readingSensorData() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
			try {
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
			        	//System.out.println("Thread " + Thread.currentThread().getId() + ": Connection closed.");
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
		boolean dataWillCome = true;
		try {
			// Declaration de la ressource
			InetAddress adresseServeur = InetAddress.getLocalHost(); // Recuperation de l'adresse de la machine
			int port = 5678; // Sera entré par l'utilisateur ensuite
			System.out.println("Serveur en ligne : " + adresseServeur + ":" + port); 
			socketserver = new ServerSocket(port);
			try {
				// Utilisation de la ressource
				socketThread = socketserver.accept();
				SimulatorsCommunication simulatorStatus = new SimulatorsCommunication(true);
				simulatorStatus.socketAtraiter = socketThread;
				simulatorStatus.socketServer = socketserver;
				Thread t = new Thread(simulatorStatus);
				t.start();
				while(dataWillCome){

					socketThread = socketserver.accept();
					SimulatorsCommunication lecteur = new SimulatorsCommunication(false);
					lecteur.socketAtraiter = socketThread;
					Thread t2 = new Thread(lecteur);
					t2.start();
					dataWillCome = t.isAlive();
					// If the first thread is dead, that means the simulator is
					// stopped and no more information will be received
				}
			}
			finally {	
				socketserver.close();
			}			
		}
		catch (IOException e) {}
	}

}
