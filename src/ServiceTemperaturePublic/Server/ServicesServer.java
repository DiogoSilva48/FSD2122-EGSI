package ServiceTemperaturePublic.Server;

import java.lang.SecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ServicesServer {
	
	String SERVICE_NAME="/TemperatureService";

	private void bindRMI(Sources sources) throws RemoteException {

		System.getProperties().put( "java.security.policy", "src/ServiceTemperaturePublic/Server/server.policy");

		if( System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try { 
			LocateRegistry.createRegistry(1099);
		} catch( RemoteException e) {
			
		}
		try {
		  LocateRegistry.getRegistry("127.0.0.1",1099).rebind(SERVICE_NAME, sources);
		  } catch( RemoteException e) {
		  	System.out.println("Registry not found");
		  }
	}

	public ServicesServer() {
		super();
	}
	
	public void createServices() {
		
		Sources sources = null;
		try {
			sources = new Sources();
		} catch (RemoteException e1) {
			System.err.println("unexpected error...");
			e1.printStackTrace();
		}
		
		try {
			bindRMI(sources);
		} catch (RemoteException e1) {
			System.err.println("erro ao registar o stub...");
			e1.printStackTrace();
		}
		
	}
}
