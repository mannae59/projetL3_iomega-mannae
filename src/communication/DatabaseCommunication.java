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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import update.Observable;
import update.Observer;

public class DatabaseCommunication implements Observable {
	
	private Connection connection =null ;
	

	
	private Fluid fluid = new Fluid();
	private Map<String,List<Integer>> defaultLimit = new TreeMap<>(); // Nicolas -- Changement de clé de Fluid vers String car l'enum n'en est plus un, pour pouvoir stocker les unites

	private Observer observer ;
	
	public DatabaseCommunication() {
		//CONNECTION A LA BDD
		try{		 
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://mysql-nicolasandre.alwaysdata.net:3306/nicolasandre_marieleontinelandmann_projets5?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B1" ,"130718_admin","nicolasde31560");  
			//connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/capteurBDD" ,"root",""); //ma bdd  		 
			
			System.out.println("Connection Established");		
			
			
				
			
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
			
			
			
		}catch(ClassNotFoundException | SQLException e){
			// JOptionPane.showMessageDialog(null,"Erreur : Impossible de communiquer avec la base de données."); // Suggestion (Nicolas) -> affiche une fenêtre avec un message, ça pourrait être intéressant :D
			System.err.println(e);
		}
		
	}
	
	public void addObserver(Observer o) {
		if (o!=null) {
			observer = o;
		}
		else {
			System.out.println("observer = = null \n");
		}
	}
	public void notifyObserver(int i) {
//		System.out.println("update appel \n");
		if (observer!=null)
			observer.update(i);
		else {
			System.out.println("observer = = null \n");
		}
		
	}
	public void deleteObserver(Observer o) {
		observer=null;
	}
	
	//////// Nicolas - 'Fluid type' changed to String type' to comply with the new spec
	public void addNewSensor(String sensorName, String sensorInfo) {
		String[] infos = sensorInfo.split(":");
		String type = fluid.valueOf(infos[0]); 
		List<Integer> seuil = new ArrayList<>( defaultLimit.get(type));
		Statement stmt =null;
		
		try {
			
			stmt = connection.createStatement();
			
			ResultSet exist =stmt.executeQuery("SELECT nom FROM Capteur where nom='"+sensorName+"'");
			if(!exist.next()) {//si il existe pas on l'ajoute 
				stmt.executeUpdate("INSERT INTO Capteur VALUES  ('"+sensorName+"','"+infos[0]+"','"+infos[1]+"','"+infos[2]+"','"+infos[3]+"',NULL,'"+seuil.get(0)+"','"+seuil.get(1)+"','1')");
				
				notifyObserver(2);
				
			}else {//sinon on le modifie
				setSensorConnection(sensorName, true);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		
	}
	
	public void setSensorConnection(String sensorName, boolean isConnected) {
		
		int connect = isConnected? 1: 0;
		Statement stmt =null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE Capteur SET connecte="+connect+" WHERE nom='"+sensorName+"'");
			if (connect==0) {//on met a null la valeur dans Donnee si on deconnecte le capteur
				Date date = new Date(System.currentTimeMillis());
				DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String d = df.format(date);
			
				stmt.executeUpdate("INSERT INTO Donnee (temps,valeur,nom_c) "
						+ "VALUES  ('"+d+  "',NULL,'"+sensorName +"' )" );
				//mise a jour dynamique 
//				System.out.println("*** notify decx ***");
				notifyObserver(3);
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	
	public void setSensorValue(String sensorName, String value, Date date) {
		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = df.format(date);
		Statement stmt =null ;
		try {
			stmt = connection.createStatement();
			
			stmt.executeUpdate("UPDATE Capteur SET valeur='"+value+"'  WHERE nom='"+sensorName+"'");
			stmt.executeUpdate("INSERT INTO Donnee (temps,valeur,nom_c) "
					+ "VALUES  ('"+d+"','"+value+"','"+sensorName +"' )" );
			notifyObserver(1);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}				
	}
	
	public void setSensorMinLimit(String sensorName, Double minLimit) {
		
		
		Statement stmt =null;
		try {
			stmt = connection.createStatement();
			//modifie la valeur du seuil
			stmt.executeUpdate("UPDATE Capteur SET seuil_min='"+minLimit+"' WHERE nom='"+sensorName+"'");
			notifyObserver(0);
			
		
		} catch (SQLException e) {	
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		
	}
	
	public void setSensorMaxLimit(String sensorName, Double maxLimit) {
		
		Statement stmt =null ;
		try {
			stmt = connection.createStatement();
			
			stmt.executeUpdate("UPDATE Capteur SET seuil_max='"+maxLimit+"' WHERE nom='"+sensorName+"'");
			
			notifyObserver(0);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		
		Statement stmt =null;
		try {
			stmt = connection.createStatement();
			
			
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
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return lCapteur;
	}
	
	public List<String> getSensorsWithFluid(String fluidType) {
		List<String> lCapteurFluid = new ArrayList<>();
		String nom;
		
	
		
		Statement stmt =null;
		try {
			stmt = connection.createStatement();
			
			
			ResultSet rs = stmt.executeQuery("SELECT nom FROM Capteur WHERE type_c='"+String.valueOf(fluidType)+"'");
			while (rs.next()) {
				  nom = rs.getString("nom");
				  
				  lCapteurFluid.add(nom);
				  
			}
			
		} catch (SQLException e) {
				
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return lCapteurFluid;
	}
	
	
	public List<String> getSensorWithDate(String sensorName, Date start, Date stop) {
		List<String> lCapteur = new ArrayList<>();

		DateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String deb = df.format(start);
		String fin = df.format(stop);
		System.out.println(deb +" : "+fin);
		Date d;
		Double val;
		Timestamp timestamp;
		String entree;
		
		Statement stmt=null ;
		try {
			stmt = connection.createStatement();
					
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
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		
		Statement stmt=null ;
		try {
			stmt = connection.createStatement();
			
			
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
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return lCapteur;
	}
	
	public Date getFirstDate() {
		Date d=null;
		Statement stmt =null ;
		try {
			stmt = connection.createStatement();			
			ResultSet rs = stmt.executeQuery("SELECT MIN(temps) from Donnee " );
			if (rs.next()) {
				Timestamp timestamp =rs.getTimestamp("temps");
				d = new Date(timestamp.getTime());	
			}	
			
		} catch (SQLException e) {	
			e.printStackTrace();
		}finally {
			if (stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return d;
	}
	
	public void close() {
		try {
			
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the treeGestionSensor
	 */
	public Map<String, Map<String, List<String>>> getTreeGestionSensor() {
		Map <String,Map<String,List<String>>> treeGestionSensor =new TreeMap<>() ;
		//on remplie la map treeGestionSensor avec les capteurs deja existant dans la bdd
		//entree capteurDejaLa=nom:bat:etage:lieu:type:sMin:sMax;
		List<String > capteurDejaLa =getAllSensors();
		for(String capteurAncien : capteurDejaLa) {
			Map<String,List<String>> mapEtage;
			List<String> listeInfo;
			String[] infos = capteurAncien.split(":");
			//info est du type Nom:lieu:typeFluide:seuilMin:seuilMax:bat:etage
			String info = infos[0]+":"+infos[3]+":"+infos[4]+":"+infos[5]+":"+infos[6]+":"+infos[1]+":"+infos[2];
			
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
		}
		return treeGestionSensor;
	}
	
}
