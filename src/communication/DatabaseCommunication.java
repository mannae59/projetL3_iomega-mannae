package communication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import update.Observable;
import update.Observer;

public class DatabaseCommunication implements Observable {
	private List<Observer> tabObserver;
	Connection connection =null ;
	Statement stmt;
	Map <String,Map<String,List<String>>> treeGestionSensor ;
	Fluid fluid = new Fluid();
	Map<String,List<Integer>> defaultLimit = new TreeMap<>(); // Nicolas -- Changement de clé de Fluid vers String car l'enum n'en est plus un, pour pouvoir stocker les unites

	
	
	public DatabaseCommunication() {
		//CONNECTION A LA BDD
		try{		 
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://mysql-nicolasandre.alwaysdata.net:3306/nicolasandre_projetl3?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B1" ,"130718_admin","nicolasde31560");  
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/capteurBDD" ,"root",""); //ma bdd  		 
			stmt = connection.createStatement();
			System.out.println("Connection Established");		 
			 
		}catch(ClassNotFoundException | SQLException e){
			// JOptionPane.showMessageDialog(null,"Erreur : Impossible de communiquer avec la base de données."); // Suggestion (Nicolas) -> affiche une fenêtre avec un message, ça pourrait être intéressant :D
			System.err.println(e);
		}
		//INITIALISATION DE LA MAP DE MAP DE LISTE
		treeGestionSensor=new TreeMap<>();
			
		
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
		tabObserver.add(o);
	}
	public void notifyObserver() {
		for(Observer o : tabObserver) {
			o.update(this);
		}
	}
	public void deleteObserver(Observer o) {
		tabObserver.remove(o);
	}
	
	//////// Nicolas - 'Fluid type' changed to String type' to comply with the new spec
	public void addNewSensor(String sensorName, String sensorInfo) {
		String[] infos = sensorInfo.split(":");
		String type = fluid.valueOf(infos[0]); 
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
		
		//MISE A JOUR DE LA MAP treeGestionSensor
		
		//info est du type Nom:lieu:typeFluide:seuilMin:seuilMax
		//Map<String(batiment),Map<String (etage),List<String (informationCapteur)>>>
		
		String info =sensorName + ":" + infos[3]+ ":" +infos[0] +":" + seuil.get(0)+":"+seuil.get(1);
		
		Map<String,List<String>> mapEtage;
		List<String> listeInfo;
		//si le batiment existe deja
		if (treeGestionSensor.containsKey(infos[1])) {
			mapEtage = treeGestionSensor.get(infos[1]);
			//si l'etage existe 
			if (mapEtage.containsKey(infos[2])) {
				listeInfo=mapEtage.get(infos[2]);
			}
			//si l'etage existe pas
			else {
				listeInfo = new ArrayList<>();
				mapEtage.put(infos[2], listeInfo);
			}
			listeInfo.add(info);
		}
		//le batiment nexiste pas encore 
		else {
			//on cree  la map pour l'etage  et on rajoute le capteur dans la liste des capteurs
			mapEtage = new TreeMap<>();
			listeInfo = new ArrayList<>();
			listeInfo.add(info);
			mapEtage.put(infos[2], listeInfo);
			treeGestionSensor.put(infos[1], mapEtage);
		}
		
		//mise a jour dynamique 
		notifyObserver();
		
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
				//mise a jour dynamique 
				notifyObserver();
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
		String bat;
		String etage;
		
		try {
			//modifie la valeur du seuil
			stmt.executeUpdate("UPDATE Capteur SET seuil_min='"+minLimit+"' WHERE nom='"+sensorName+"'");
			
			//recupere le batiment et l'etage du capteur pour treeGestionSensor
			
			ResultSet rs = stmt.executeQuery("SELECT  batiment,etage FROM Capteur WHERE nom='"+sensorName+"'" );
			rs.next();
			bat=rs.getString("batiment");
			etage = rs.getString("etage");		
		
		
			//ON MET A JOUR treeGestionSensor
			boolean trouve =false;
			
			Map<String,List<String>> mapEtage =treeGestionSensor.get(bat);
			List<String> listeInfo = mapEtage.get(etage);
			String newInfo="";
			//recherche dans liste des capteur des l'etage
			//info est du type Nom:lieu:typeFluide:seuilMin:seuilMax
			for (Iterator<String> ite = listeInfo.iterator();ite.hasNext()&&!trouve;) {
				String info = ite.next();
				String[] lInfo = info.split(":");
				//on trouve le capteur a modifier
				
				if (lInfo[0].equals( sensorName )) {
					lInfo[3]= minLimit.toString();//onmodifie ,on concatene et on ajoute
				    newInfo = Arrays.stream(lInfo).collect(Collectors.joining(":"));
				    
				    ite.remove();			    
				    trouve=true;
				}
				
			}
			listeInfo.add(newInfo);
			
		
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		
		
	}
	
	public void setSensorMaxLimit(String sensorName, Double maxLimit) {
		String bat;
		String etage;
		
		try {
			
			stmt.executeUpdate("UPDATE Capteur SET seuil_max='"+maxLimit+"' WHERE nom='"+sensorName+"'");
			
			//recupere le batiment et l'etage du capteur pour treeGestionSensor
			ResultSet rs = stmt.executeQuery("SELECT  batiment,etage FROM Capteur WHERE nom='"+sensorName+"'" );
			rs.next();
			bat=rs.getString("batiment");
			etage = rs.getString("etage");		
		
		
			//ON MET A JOUR treeGestionSensor
			boolean trouve =false;
			
			Map<String,List<String>> mapEtage =treeGestionSensor.get(bat);
			List<String> listeInfo = mapEtage.get(etage);
			String newInfo="";
			//recherche dans liste des capteur des l'etage
			//info est du type Nom:lieu:typeFluide:seuilMin:seuilMax
			for (Iterator<String> ite = listeInfo.iterator();ite.hasNext()&&!trouve;) {
				String info = ite.next();
				String[] lInfo = info.split(":");
				//on trouve le capteur a modifier
				
				if (lInfo[0].equals( sensorName )) {
					lInfo[4]= maxLimit.toString();//onmodifie ,on concatene et on ajoute
				    newInfo = Arrays.stream(lInfo).collect(Collectors.joining(":"));
				    
				    ite.remove();			    
				    trouve=true;
				}
				
			}
			listeInfo.add(newInfo);
			
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
	
	public Date getFirstDate() {
		Date d=null;
		try {			
			ResultSet rs = stmt.executeQuery("SELECT MIN(temps) from Donnee " );
			if (rs.next()) {
				Timestamp timestamp =rs.getTimestamp("temps");
				d = new Date(timestamp.getTime());	
			}	
			
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		
		return d;
	}
	
	public void close() {
		try {
			connection.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the treeGestionSensor
	 */
	public Map<String, Map<String, List<String>>> getTreeGestionSensor() {
		return treeGestionSensor;
	}
	
}
