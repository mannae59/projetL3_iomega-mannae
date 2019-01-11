package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import communication.DatabaseCommunication;

public class Test {

	public static void main(String[] args) {
//		try{              
//            Double.parseDouble("0");
//            System.out.println("ok");
//      }catch(NumberFormatException nfe){
//    	  System.out.println("pas ok");
//      }
		Date hoy =new Date(System.currentTimeMillis());
		Date hoy1 =new Date(System.currentTimeMillis());
		System.out.println(hoy);
		DatabaseCommunication d =new DatabaseCommunication();
		d.addNewSensor("capteur88", "EAU:U7:4:REZDECHAUSSE");
		d.setSensorValue("capteur88","300", hoy);
		String s = "2001-01-02 01:48:45";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date d1=null;
		try {
			d1 = sdf.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(" date = "+d1+" TEST firstdate "+d.getSensorWithDate("capteur1",d1,hoy1));
		/*System.out.println("allSensor" +d.getAllSensors());
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
