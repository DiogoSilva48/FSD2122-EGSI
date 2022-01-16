package ServiceHumidityPublic;

import java.net.*;
import java.io.*;
import java.util.*;


public class GetServicesRequestHandler extends Thread {
	Socket ligacao;
	Sources source;
	BufferedReader in;
	PrintWriter out;

	public GetServicesRequestHandler(Socket ligacao, Sources source) {
		this.ligacao = ligacao;
		this.source = source;

		try
		{	
			this.in = new BufferedReader (new InputStreamReader(ligacao.getInputStream()));
			
			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
	
	public void run() {                
		try {
			String response;
			String msg = in.readLine();
			
			StringTokenizer tokens = new StringTokenizer(msg);
			String metodo = tokens.nextToken();
			
			if (metodo.equals("getHumidity")) {
				String tsp = tokens.nextToken();
				String Humidity = Float.toString(source.getHumidity());
				String ordem = String.valueOf(source.getOrder());
				int respostaTimeStamp = source.getStampValidation(tsp);
				switch (respostaTimeStamp) {
					case 0: response = "403 Forbidden";
							System.out.println(response);
							out.println(response);
							out.println("");
							break;
					case 1: response = "200 OK";
							System.out.println(response);
							out.println(response);
							response = Humidity;
							System.out.println(response);
							out.println(response);
							break;
					case 2: response = "400 Bad Request";
							System.out.println(response);
							out.println(response);
							out.println("");
							break;
				}
			}
			else 
			{	response = "404 method not allowed";
				System.out.println(response);
				out.println(response);
				out.println("");
			}
			out.flush();
			in.close();
			out.close();
			ligacao.close();
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}
}
