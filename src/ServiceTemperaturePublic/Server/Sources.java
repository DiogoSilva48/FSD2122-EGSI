package ServiceTemperaturePublic.Server;

import java.time.Instant;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; 
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList; import java.util.List;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;


public class Sources extends UnicastRemoteObject implements ServicesInterface {

	private static Hashtable<String, DataStructure> presentData = new Hashtable<String, DataStructure>();
	private static int  cont, orderActual=1;
	private static String filename= "src/ServiceTemperaturePublic/Server/log_temp.csv";
	
	public Sources() throws RemoteException {
		super();
		loadData();
	}
	
	public void loadData(){
			List<String> dtset = new ArrayList<>();
			Path pathToFile = Paths.get(filename); 
			try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
				// read the first line from the text file
				String line = br.readLine();
				// loop until all lines are read
				while (line != null) {
					// use string.split to load a string array with the values from // each line of // the file, using a comma as the delimiter
					String[] attributes = line.split(",");
					//Vector<DataStructure> dtrec = 
					getDtSet(attributes); 
					// adding data into HashTable PresentData
					// read next line before looping
					// if end of file reached, line would be null
					//this.setOrderActual();
					line = br.readLine();
				}
			} catch (IOException ioe) { 
				ioe.printStackTrace();
			}

	}
	
	public Float getTemp(Instant tsp){
		float result = 9999.9999f;		
		
		if (this.getStampValidation(tsp)==1) {
			for (Enumeration<DataStructure> e = presentData.elements(); e.hasMoreElements(); ) {
				DataStructure element = e.nextElement();

				if (element.getOrd() == next()) {
					result = element.getTemperatura();
					next();
				}
			}
		}
		return result;
	}

	public int getStampValidation(Instant timeIn){
		Instant timestamp = Instant.now();
		System.out.println("Now: " + timestamp);
		long res = timestamp.toEpochMilli() - timeIn.toEpochMilli();
		if (res > 180*1000) return 0;
		else return 1;
	}
	
	public int next () {
		if (orderActual+1>=presentData.size()){/*Máximo de dados obter do ficheiro*/
			orderActual=1;
			return 1;
		}
		else
		{
			return ++orderActual;
		}
	}
/*	public void setOrderActual () {
			this.orderActual+=1;
	}*/
	//Construir o objeto na implementação da Interface...
	public void getDtSet(String[] metadata)  throws RemoteException {
		cont = cont+1;
		synchronized(this) {
			if(metadata.length>3){
				if(!(metadata[2]=="Error") && !(metadata[3]=="Error")){
					String dat=metadata[0];
					float tmp=Float.parseFloat(metadata[2].substring(2));
					float hum=Float.parseFloat(metadata[3].substring(2));
					DataStructure dataSt = new DataStructure(cont,dat, tmp, hum);
					presentData.put(String.valueOf(cont),dataSt);
				}else{cont-=1;}
			}else{cont-=1;}

		}
	}
}

class DataStructure {
	
	private int order;
	private String dataCaptura;
	private float temperature;
	private float humidity;
	
	
	public DataStructure(int order, String dataCaptura, float Temperature, float Humidity) {
		this.order = order;
		this.dataCaptura = dataCaptura;
		this.temperature = Temperature;
		this.humidity = Humidity;	
	}

	public float getTemperatura () {
		return this.temperature;
	}
	public int 	getOrd () {
		return this.order;
	}

}



