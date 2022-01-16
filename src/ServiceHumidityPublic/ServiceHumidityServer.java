package ServiceHumidityPublic;

import java.net.*;
import java.io.*;

public class ServiceHumidityServer {
	static int DEFAULT_PORT=2000;
	
	public static void main(String[] args) {
		int port=DEFAULT_PORT;
		Sources source = new Sources();
		source.loadData();
		ServerSocket servidor = null; 
	
		try	{ 
			servidor = new ServerSocket(port);
		} catch (Exception e) { 
			System.err.println("erro ao criar socket servidor...");
			e.printStackTrace();
			System.exit(-1);
		}
				
		while(true) {
			try {
				
				Socket ligacao = servidor.accept();
				
				GetServicesRequestHandler t = new GetServicesRequestHandler(ligacao, source);
				t.start();
				
			} catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
				System.exit(1);
			}
		}
	}
}
