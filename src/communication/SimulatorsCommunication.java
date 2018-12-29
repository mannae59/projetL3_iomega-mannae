package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.awt.EventQueue;

import update.Observable;
import update.Observer;
import display.Display;

public class SimulatorsCommunication implements Runnable, Observable {
	private List<Observer> tabObserver = new ArrayList<>();
	Socket socketAtraiter; // Socket used by the simulator to send data for this sensor
	ServerSocket serverSocket; // Socket listened by the server (used to close the connection if the simulator leaves
	DatabaseCommunication database; // Interface with the database
	boolean firstCall; // Determines if it's the first thread or not
	boolean dataWillCome = true; // Boolean which tells if the sensor is connected or not
	String name = ""; // Sensor name
	
	public SimulatorsCommunication(boolean firstCall, Socket socketThread, ServerSocket serverSocket, DatabaseCommunication db) {
		this.firstCall = firstCall;
		this.socketAtraiter = socketThread;
		this.serverSocket = serverSocket;
		this.database = db;
	}
	
	public void run() {
		// Runs the API listener if it's the first Thread,
		// or the classic sensor reader if not
		if(firstCall) {
			listeningAPI();
		}else {
			readingSensorData();
		}
	}
	
	private void listeningAPI() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
			while (dataWillCome) {
			    try{
			      String input = in.readLine();
			      System.out.println(input);
			    }
			    catch (SocketException se)
			    {
			    	dataWillCome = false;
			    	if(serverSocket != null) {
			    		serverSocket.close();
			    	}
			    }
			  }
			  if(serverSocket != null) {
				  serverSocket.close();
			  }
		}catch (IOException e) {
		e.printStackTrace();
		}
	}
	// Returns the name from a raw String
	public String getNameByMessage(String data) {
		String[] splitted = data.split(" ");
		if(splitted.length != 3) {
			System.err.println("Error : " + data + " cannot be read correctly.");
		}
		return splitted[1];
	}
	// Returns the information from a raw String
	public String getInfoByMessage(String data) {
		String[] splitted = data.split(" ");
		if(splitted.length != 3) {
			System.err.println("Error : " + data + " cannot be read correctly.");
		}
		return splitted[2];
	}
	
	
	public void sensorConnected(String data) {
		// TODO Complete this method
		name = getNameByMessage(data);
		String sensorInfo = getInfoByMessage(data);
		database.addNewSensor(name,sensorInfo);
		for(Observer o : tabObserver) {
			notifyObserver(o);
		}
	}
	
	public void dataReceivedFromSensor(String data) {
		String value = getInfoByMessage(data);
		Date date = new Date();
		database.setSensorValue(name, value, date);
		for(Observer o : tabObserver) {
			notifyObserver(o);
		}
	}
	
	public void sensorDisconnected(String data) {
		// TODO Complete this method
		database.setSensorConnection(name,false);
	}
	private void readingSensorData() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
		      while (dataWillCome) {
		        try
		        {
		          String input = in.readLine();
		          if (input != null) {
		        	  // Debug
		            System.out.println("Thread " + Thread.currentThread().getId() + ": " +  input);
		            
		            if(input.startsWith("Connexion")) {
						sensorConnected(input);
		            }
		            else if(input.startsWith("Deconnexion")) {
						sensorDisconnected(input);
		            	dataWillCome = false;
		            }
		            else if(input.startsWith("Donnee")) {
		            	dataReceivedFromSensor(input);
		            }
		            else {
		            	System.err.println("Erreur : impossible de comprendre le message\n" + input);
		            }
		          }
		        }
		        catch (SocketException se)
		        {
		        	//System.out.println("Thread " + Thread.currentThread().getId() + ": Connection closed.");
		        	dataWillCome = false;
		        }
		      }
			socketAtraiter.close();
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
		DatabaseCommunication db = new DatabaseCommunication();
		
		EventQueue.invokeLater(() -> {
			Display display = new Display();
			display.setVisible(true);
		});
		
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
		catch (IOException e) {}
	}

}
