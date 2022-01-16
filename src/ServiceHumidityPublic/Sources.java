package ServiceHumidityPublic;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files; 
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.ArrayList;
import java.util.List;
import java.time.*;




public class Sources {

	private static Hashtable<String, DataStructure> presentData = new Hashtable<String, DataStructure>();
	private static int  cont, orderActual=0;	
	private static String filename= "src/ServiceHumidityPublic/log_temp.csv";
	
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
					this.setOrderActual();
					line = br.readLine();
				}
			} catch (IOException ioe) { 
				ioe.printStackTrace();
			}

	}
	
	public float getHumidity(){
		float result = 0;
		for (Enumeration<DataStructure> e = presentData.elements(); e.hasMoreElements(); ) {
			DataStructure element = e.nextElement();
			if(element.getOrd()==getOrder()){
				result=element.getHum();
				setOrderActual();
			}
		}
		return result;
	}

	public int getStampValidation(String time){
		Instant timestamp = Instant.now();
		System.out.println("now: " + timestamp);
		try {
			Instant timeIn = Instant.parse(time);
			
			long res = timestamp.toEpochMilli() - timeIn.toEpochMilli();
			if (res > 180*1000){
				return 0;
			} 
		else return 1;
		} catch (java.time.format.DateTimeParseException dtpe) { 
				return 2;
		}
	}


	public int getOrder () {
		if (this.orderActual>=presentData.size()){/*Máximo de dados obter do ficheiro*/
			this.orderActual=1;
			return 1;
		}
		else{
			//setOrderActual();
			return this.orderActual;
		}
	}
	public void setOrderActual () {
			this.orderActual+=1;
	}
	
	private void getDtSet(String[] metadata) {

		synchronized(this) {
			if(metadata.length>3){
				if(!(metadata[2]=="Error") && !(metadata[3]=="Error")){
					String dat=metadata[0];
					float tmp=Float.parseFloat(metadata[2].substring(2));
					float hum=Float.parseFloat(metadata[3].substring(2));							
					DataStructure dataSt = new DataStructure(cont,dat, tmp, hum);
					presentData.put(String.valueOf(cont),dataSt);
					cont = cont+1;
				}
			}

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

	public float getHum () {
		return this.humidity;
	}
	public int 	getOrd () {
		return this.order;
	}

}



