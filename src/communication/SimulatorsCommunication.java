package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import update.Observer;

public class SimulatorsCommunication implements Runnable {
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
	
	// First thread : only here to listen the information send by the API itself
	// i.e the API closes the connection, this function will be the first to know it
	private void listeningAPI() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
			while (dataWillCome) {
			    try{
			      String input = in.readLine();
			      if (input!=null)
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
	
	private void errorMessage(String data) {
		System.err.println("Error : " + data + " cannot be read correctly.");
	}
	
	// Returns the name from a raw String
	private String getNameByMessage(String data) {
		String[] splitted = data.split(" ");
		if(splitted.length < 3) errorMessage(data);
		return splitted[1];
	}
	// Returns the information from a raw String
	private String getInfoByMessage(String data) {
		String[] splitted = data.split(" ");
		if(splitted.length < 3) errorMessage(data);
		return splitted[2];
	}
	
	
	
	public void sensorConnected(String data) {
		// TODO Complete this method
		
		name = getNameByMessage(data);
		String sensorInfo = getInfoByMessage(data);
		

		
		// Ajouter l'entree a la base de donnees
		database.addNewSensor(name,sensorInfo);
		
//		notifyObserver();
	}
	
	public void dataReceivedFromSensor(String data) {
		String value = getInfoByMessage(data);
		Date date = new Date();
		database.setSensorValue(name, value, date);
//		notifyObserver();
	}
	
	public void sensorDisconnected(String data) {
		// TODO Complete this method
		name = data.split(" ")[1];
		database.setSensorConnection(name,false);
//		notifyObserver();
	}
	private void readingSensorData() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socketAtraiter.getInputStream()));
		      while (dataWillCome) {
		        try
		        {
		          String input = in.readLine();
		          if (input != null) {
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
		        	// Connection closed by the simulator
		        	dataWillCome = false;
		        }
		      }
			socketAtraiter.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
