package ST;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Map;

import Keys.KeyHandler;

public class SThread implements Runnable{

    private Socket socket;

    private Map<String,ServicoRedeSockets> servicos_java_tcp;
    private Map<String,ServicoRedeRMI> servicos_java_rmi;

    private PublicKey siPublicKey;
    private Instant timestamp;

    public SThread(Socket socket, Map<String,ServicoRedeSockets> servicos_java_tcp, Map<String,ServicoRedeRMI> servicos_java_rmi, PublicKey siPublicKey){
        this.socket = socket;
        this.servicos_java_tcp = servicos_java_tcp;
        this.servicos_java_rmi = servicos_java_rmi;
        servicos_java_tcp.put("HUM", new ServicoRedeSockets("Humidity Service","127.0.0.1",2000,"HUM"));
        servicos_java_rmi.put("TEMP", new ServicoRedeRMI("Temperature Service","127.0.0.1",1099,"/TemperatureService","TEMP"));
        this.siPublicKey = siPublicKey;
        this.timestamp = Instant.now();
    }

    public synchronized void new_tcp_service(ServicoRedeSockets servicoRedeSockets){
        this.servicos_java_tcp.put(servicoRedeSockets.getRegister_key(),servicoRedeSockets);
    }

    public synchronized void new_rmi_service(ServicoRedeRMI servicoRedeRMI){
        this.servicos_java_rmi.put(servicoRedeRMI.getRegister_key(),servicoRedeRMI);
    }

    public boolean authorization_checker(String uniqueid, String auth_hash)
    {
        boolean ret = true;

        try{
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(this.siPublicKey);
            signature.update(uniqueid.getBytes());
            ret = signature.verify(KeyHandler.hexStringToByteArray(auth_hash));
        }
        catch(Exception e)
        {
            ret = false;
        }

        return ret; 
    }

    public void run()
    {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream());
        } catch (IOException i) {
            System.out.println("An error occurred connecting to server :(");
            try {
                this.socket.shutdownOutput();
                this.socket.shutdownInput();
                this.socket.close();
            } catch (IOException ie) {
                System.out.println(ie.getMessage());
            }
        }

        String read = "";
        while (read != null && !read.equals("close")) {

            try {
                read = in.readLine();
            } catch (IOException ie) {
                out.println("Error reading message!");
                out.flush();
            }

            String[] parts;
            if (read != null) {
                parts = read.split(" ");
                String op = parts[0];
                switch (op) {
                    case ("register_tcp_service"):  
                        if(parts.length == 4){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                String[] second_parts = parts[3].split("\\|");
                                if(second_parts.length == 4) {
                                    String description = second_parts[0];
                                    String ip = second_parts[1];
                                    int port = Integer.parseInt(second_parts[2]);
                                    String register_key = second_parts[3];

                                    new_tcp_service(new ServicoRedeSockets(description, ip, port, register_key));
                                    
                                    out.println("great_success_inserting_tcp_service");
                                    out.flush();
                                }
                            }                         
                        }
                        break;
                    case ("register_rmi_service"):  
                        if(parts.length == 4){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                String[] second_parts = parts[3].split("\\|");
                                if(second_parts.length == 5) {
                                    String description = second_parts[0];
                                    String ip = second_parts[1];
                                    int port = Integer.parseInt(second_parts[2]);
                                    String name = second_parts[3];
                                    String register_key = second_parts[4];

                                    new_rmi_service(new ServicoRedeRMI(description, ip, port, name, register_key));

                                    out.println("great_success_inserting_rmi_service");
                                    out.flush();
                                }
                            }                     
                        }
                        break;    
                    case ("query_tcp"):
                        if(parts.length == 3){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                out.println("tcp_services " + this.servicos_java_tcp.size());
                                out.flush();
                                out.println("description|ip|port|key");
                                out.flush();
                                for(ServicoRedeSockets s : this.servicos_java_tcp.values()) {
                                    out.println("   -> " + s.getDescription() + "|" + s.getIp() + "|" + s.getPort() + "|" + s.getRegister_key());
                                }   
                                out.flush();

                                
                            }                     
                        }
                        break;
                    case ("query_rmi"):
                        if(parts.length == 3){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                out.println("rmi_services " + this.servicos_java_rmi.size());
                                out.flush();
                                out.println("description|ip|port|name|key");
                                out.flush();
                                for(ServicoRedeRMI s : this.servicos_java_rmi.values()) {
                                    out.println("   -> " + s.getDescription() + "|" + s.getIp() + "|" + s.getPort() + "|" + s.getName() + "|" + s.getRegister_key());
                                }   
                                out.flush();   
                            }                     
                        }
                        break;
                    case ("access_tcp_service"):
                        if(parts.length == 4){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                ServicoRedeSockets s = this.servicos_java_tcp.get(parts[3]);
                                out.println(s.getIp() + "|" + s.getPort() + "|" + this.timestamp);
                                out.flush();
                            }
                        }
                        break;
                    case ("access_rmi_service"):
                        if(parts.length == 4){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                ServicoRedeRMI s = this.servicos_java_rmi.get(parts[3]);
                                out.println(s.getIp() + "|" + s.getPort() + "|" + s.getName() + "|" + this.timestamp);
                                out.flush();
                            }
                        }
                        break;
                    default:
                        String st = "Available Commands: -> query_tcp unique_id authentication_hash -> query_rmi unique_id authentication_hash -> register_tcp_service unique_id authentication_hash description|ip|port|register_key -> register_rmi_service unique_id authentication_hash description|ip|port|name|register_key -> access_tcp_service unique_id authentication_hash service_key -> access_rmi_service unique_id authentication_hash service_key";
                        out.println(st);
                        out.flush();    
                }
            }

        }
    }
    
}
