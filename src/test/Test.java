package test;

import java.util.Date;

import communication.DatabaseCommunication;

public class Test {

	public static void main(String[] args) {
		try{              
            Double.parseDouble("0");
            System.out.println("ok");
      }catch(NumberFormatException nfe){
    	  System.out.println("pas ok");
      }
		/*Date hoy =new Date(System.currentTimeMillis());
		System.out.println(hoy);
		DatabaseCommunication d =new DatabaseCommunication();
		System.out.println("TEST TREE "+d.getTreeGestionSensor());
		System.out.println("allSensor" +d.getAllSensors());
		System.out.println("connected sensor" +d.getConnectedSensors());
		d.addNewSensor("c8", "EAU:U8:4:206:");
		System.out.println(d.getTreeGestionSensor());*/
		/*System.out.println("TEST1 "+d.getTreeGestionSensor());
		
		//d.setSensorValue("capteur666", "111.1", hoy);
		d.setSensorConnection("capteur1", true);
		d.setSensorMaxLimit("capteur666",1.5 );
		d.setSensorMinLimit("capteur666",1.5 );
		System.out.println("TEST2 "+d.getTreeGestionSensor());*/
		
		
		//System.out.println(d.getConnectedSensors());
		

	}

}
