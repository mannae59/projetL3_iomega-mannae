package communication;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import update.Observable;
import update.Observer;

public class DatabaseCommunication implements Observable {
	private List<Observer> tabObserver;
	Connection connection =null ;
	Statement stmt;
	Fluid fluid = new Fluid();
	Map<String,List<Integer>> defaultLimit = new TreeMap<>(); // Nicolas -- Changement de cl� de Fluid vers String car l'enum n'en est plus un, pour pouvoir stocker les unites
	
	   ////
	 /// ///
   ///  | ///
 ///    |  ///
//		*   ///
////////////////	
//PROBLEME L249 AVEC BDD NICOLAS (surement l'url ) avec getSensorDate
	
	
	public DatabaseCommunication() {
		//CONNECTION A LA BDD
		try{		 
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://mysql-nicolasandre.alwaysdata.net:3306/nicolasandre_projetl3?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC" ,"130718_admin","nicolasde31560");  
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/capteurBDD" ,"root",""); //ma bdd  		 
			stmt = connection.createStatement();
			System.out.println("Connection Established");		 
			 
		}catch(ClassNotFoundException | SQLException e){
			// JOptionPane.showMessageDialog(null,"Erreur : Impossible de communiquer avec la base de donn�es."); // Suggestion (Nicolas) -> affiche une fen�tre avec un message, �a pourrait �tre int�ressant :D
			System.err.println(e);
		}
			
		
		//INITIALISATION DE LA MAP AVEC LES VALEURS PAR DEFAULT
		List<Integer> eau = new ArrayList<>();
		List<Integer> elec = new ArrayList<>();
		List<Integer> air = new ArrayList<>();
		List<Integer> temp  = new ArrayList<>();
		eau.add(0);
		eau.add(10);
		elec.add(10);
		elec.add(500);
		air.add(0);
		air.add(5);
		temp.add(17);
		temp.add(22);
		defaultLimit.put(Fluid.EAU, eau);
		defaultLimit.put(Fluid.AIRCOMPRIME, air);
		defaultLimit.put(Fluid.ELECTRICITE, elec);
		defaultLimit.put(Fluid.TEMPERATURE, temp);
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
	public void addNewSensor(String sensorName, String sensorInfo) {
		String[] infos = sensorInfo.split(":");
		String type = fluid.valueOf(infos[0]); // Nicolas - 'Fluid type' changed to String type' to comply with the new spec
		List<Integer> seuil = new ArrayList<>( defaultLimit.get(type));
		
		try {
			stmt = connection.createStatement();
			
			ResultSet exist =stmt.executeQuery("SELECT nom FROM Capteur where nom='"+sensorName+"'");
			if(!exist.next()) {//si il existe pas on l'ajoute 
				stmt.executeUpdate("INSERT INTO Capteur VALUES  ('"+sensorName+"','"+infos[0]+"','"+infos[1]+"','"+infos[2]+"','"+infos[3]+"',NULL,'"+seuil.get(0)+"','"+seuil.get(1)+"','1')");
			}else {//sinon on le modifie
				setSensorConnection(sensorName, true);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
	}
	
	public void setSensorConnection(String sensorName, boolean isConnected) {
		
		int connect = isConnected==true ? 1: 0;
		try {
			
			stmt.executeUpdate("UPDATE Capteur SET connecte="+connect+" WHERE nom='"+sensorName+"'");
			if (connect==0) {//on met a null la valeur dans Donnee si on deconnecte le capteur
				Date date = new Date(System.currentTimeMillis());
				DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String d = df.format(date);
			
				stmt.executeUpdate("INSERT INTO Donnee (temps,valeur,nom_c) "
						+ "VALUES  ('"+d+  "',NULL,'"+sensorName +"' )" );
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	public void setSensorValue(String sensorName, String value, Date date) {
		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = df.format(date);
		
		try {
			
			stmt.executeUpdate("UPDATE Capteur SET valeur='"+value+"'  WHERE nom='"+sensorName+"'");
			stmt.executeUpdate("INSERT INTO Donnee (temps,valeur,nom_c) "
					+ "VALUES  ('"+d+"','"+value+"','"+sensorName +"' )" );
			
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}				
	}
	
	public void setSensorMinLimit(String sensorName, Double minLimit) {
		try {
			
			stmt.executeUpdate("UPDATE Capteur SET seuil_min='"+minLimit+"' WHERE nom='"+sensorName+"'");
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void setSensorMaxLimit(String sensorName, Double maxLimit) {
		try {
			
			stmt.executeUpdate("UPDATE Capteur SET seuil_max='"+maxLimit+"' WHERE nom='"+sensorName+"'");
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public List<String> getConnectedSensors() {
		List<String> lCapteur = new ArrayList<>();
		String nom;
		Double val;
		Double sMin;
		Double sMax;
		String type;
		String etage;
		String bat;
		String lieu;
		String alerteSeuil; //si 1 alors depassement de seuil sinon 0
		String entree;
		
		try {
			
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM Capteur WHERE connecte='1'and valeur IS NOT NULL");
			while (rs.next()) {
				  nom = rs.getString("nom");
				  val = rs.getBigDecimal("valeur").doubleValue();
				  sMin = rs.getBigDecimal("seuil_min").doubleValue();
				  sMax = rs.getBigDecimal("seuil_max").doubleValue();
				  type = rs.getString("type_c");
				  etage = rs.getString("etage");
				  bat = rs.getString("batiment");
				  lieu = rs.getString("lieu");
				  alerteSeuil= (val <sMax && val>sMin) ? "0" : "1";
				  entree=nom+":"+bat+":"+etage+":"+lieu+":"+val+":"+type+":"+sMin+":"+sMax+":"+alerteSeuil;
				  lCapteur.add(entree);
				  
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
		}
		
		return lCapteur;
	}
	
	public List<String> getSensorsWithFluid(String fluidType) {
		List<String> lCapteurFluid = new ArrayList<>();
		String nom;
		
	
		
		try {
			
			
			ResultSet rs = stmt.executeQuery("SELECT nom FROM Capteur WHERE type_c='"+String.valueOf(fluidType)+"'");
			while (rs.next()) {
				  nom = rs.getString("nom");
				  
				  lCapteurFluid.add(nom);
				  
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
		}
		
		return lCapteurFluid;
	}
	
	
	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		List<String> lCapteur = new ArrayList<>();
		String nom;
		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String deb = df.format(start);
		String fin = df.format(stop);
		System.out.println(deb +" : "+fin);
		Date d;
		Double val;
		Timestamp timestamp;
		String entree;
		
		try {
					
			ResultSet rs = stmt.executeQuery("SELECT temps,valeur FROM Donnee WHERE nom_c='"+sensorName+"' and temps<='"+fin+
					"' and temps>='"+deb+"'" );
			while (rs.next()) {
				  
				  val = rs.getBigDecimal("valeur").doubleValue();
				  
					
				  timestamp =rs.getTimestamp("temps");
				//PROBLEME : avance le temps de 1h pour la bdd de nicolas mais le temps est ok pour la mienne
				  //surement l'url mal gere je regarderais demain
				  d = new Date(timestamp.getTime());
				  entree=val+":"+df.format(d);
				  lCapteur.add(entree);
				  
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
		}
		
		return lCapteur;
	}
	
	public List<String> getAllSensors() {
		List<String> lCapteur = new ArrayList<>();
		String nom;
		Double sMin;
		Double sMax;
		String type;
		String etage;
		String bat;
		String lieu;
		String entree;
		
		try {
			
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM Capteur ");
			while (rs.next()) {
				  nom = rs.getString("nom");
				  sMin = rs.getBigDecimal("seuil_min").doubleValue();
				  sMax = rs.getBigDecimal("seuil_max").doubleValue();
				  type = rs.getString("type_c");
				  etage = rs.getString("etage");
				  bat = rs.getString("batiment");
				  lieu = rs.getString("lieu");
				  
				  entree=nom+":"+bat+":"+etage+":"+lieu+":"+type+":"+sMin+":"+sMax;
				  lCapteur.add(entree);
				  
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
		}
		
		return lCapteur;
	}
	
	public void close() {
		try {
			connection.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
