package Client;

import ServiceTemperaturePublic.Server.ServicesInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Instant;

public class ClientCommandParser {
    
    private BufferedReader system_input;

    private Socket si;
    private PrintWriter si_out;
    private BufferedReader si_in;
    
    private String st_ip;
    private int st_port;

    private String unique_id;
    private String st_auth_hash;

    private Socket st;
    private PrintWriter st_out;
    private BufferedReader st_in;

    private boolean flag;
  
    public ClientCommandParser(BufferedReader system_in, Socket si, BufferedReader in, PrintWriter out){
        this.system_input = system_in;

        this.si = si;
        this.si_in = in;
        this.si_out = out;

        this.st_ip = null;
        this.st_port = -1;
        this.st_auth_hash = null;

        this.st = null;
        this.st_out = null;
        this.st_in = null;

        this.flag = false;
    }    

    public int handle(String read) throws Exception{
        int returnValue = 0;
        String[] strs = read.split(" ");
        //Connecting with SI
        if(!this.flag)
        {
            switch (strs[0]) {
                case "getST":
                
                    if(strs.length == 2){

                        this.unique_id = strs[1];

                        this.si_out.println("getST " + this.unique_id);
                        this.si_out.flush();

                        String response = this.si_in.readLine();
                        String[] parts = response.split("\\|");
                        if(parts.length == 4)
                        {
                            this.st_ip = parts[1];
                            this.st_port = Integer.parseInt(parts[2]);
                            this.st_auth_hash = parts[3];

                            st = new Socket(this.st_ip, this.st_port);
                            this.st_in = new BufferedReader(new InputStreamReader(st.getInputStream()));
                            this.st_out = new PrintWriter(st.getOutputStream());

                            this.si.shutdownInput();
                            this.si.shutdownOutput();
                            this.si.close();

                            this.flag = true;
                        }
                        System.out.println("ST_IP: " + this.st_ip + " | ST_PORT: " + this.st_port + " | ST_HASH: " + this.st_auth_hash);

                        break;
                    }                   
                    
                default:
                    System.out.println("Available Commands:\n   -> getST unique_user_identifier");

            }
        }
        //Connecting with ST
        else
        {
            switch (strs[0]) {
                case "query_tcp":

                    this.st_out.println("query_tcp " + this.unique_id + " " + this.st_auth_hash);
                    this.st_out.flush();

                    //System.out.println(this.st_in.readLine());
                    String input_TCP = this.st_in.readLine();
                    System.out.println(this.st_in.readLine());
                    String[] tcp_naming_parts = input_TCP.split(" ");
                    int nservices = Integer.parseInt(tcp_naming_parts[1]);
                    for(int i=0; i < nservices; i++){
                        System.out.println(this.st_in.readLine());
                    }
                    break;
                
                case "query_rmi":

                    this.st_out.println("query_rmi " + this.unique_id + " " + this.st_auth_hash);
                    this.st_out.flush();

                    String input_RMI = this.st_in.readLine();
                    System.out.println(this.st_in.readLine());
                    String[] rmi_naming_parts = input_RMI.split(" ");
                    int rservices = Integer.parseInt(rmi_naming_parts[1]);
                    for(int i=0; i < rservices; i++){
                        System.out.println(this.st_in.readLine());
                    }
                    break;    

                case "register_tcp_service":
                    
                    if(strs.length==2){
                        String[] tcp_service_parts = strs[1].split("\\|");
                        if(tcp_service_parts.length == 4){
                            StringBuilder sb = new StringBuilder();
                            sb.append("register_tcp_service ")
                              .append(this.unique_id).append(" ")
                              .append(this.st_auth_hash).append(" ")
                              .append(strs[1]);
                            
                            this.st_out.println(sb.toString());
                            this.st_out.flush();  

                            System.out.println(this.st_in.readLine());
                        }
                    }
                    
                    break;   
                
                case "register_rmi_service":

                    if(strs.length==2){
                        String[] tcp_service_parts = strs[1].split("\\|");
                        if(tcp_service_parts.length == 5){
                            StringBuilder sb = new StringBuilder();
                            sb.append("register_rmi_service ")
                              .append(this.unique_id).append(" ")
                              .append(this.st_auth_hash).append(" ")
                              .append(strs[1]);
                        
                            this.st_out.println(sb.toString());
                            this.st_out.flush();  

                            System.out.println(this.st_in.readLine());
                        }
                    }
                    break;

                case "access_tcp_service":

                    if(strs.length==2){
                        String tcpservkey = strs[1];
                        StringBuilder sb = new StringBuilder();
                        sb.append("access_tcp_service ")
                          .append(this.unique_id).append(" ")
                          .append(this.st_auth_hash).append(" ")
                          .append(strs[1]);

                        this.st_out.println(sb.toString());
                        this.st_out.flush();

                        String in = this.st_in.readLine();
                        String[] parts = in.split("\\|");

                        String tcpip = parts[0];
                        int tcpport = Integer.parseInt(parts[1]);
                        Instant i = Instant.parse(parts[2]);

                        Socket socket = new Socket(tcpip, tcpport);
                        BufferedReader s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter s_out = new PrintWriter(socket.getOutputStream());

                        //secalhar pode-se enviar logo o parts[2]
                        s_out.println("getHumidity " + i);
                        s_out.flush();

                        String resp = s_in.readLine();
                        switch (resp){
                            case "403 Forbidden":
                                String batata = s_in.readLine();
                                System.out.println("forbidden");
                                break;
                            case "200 OK":
                                System.out.println(s_in.readLine());
                                break;
                            case "400 Bad Request":
                                String batata2 = s_in.readLine();
                                System.out.println("badrequest");
                                break;
                        }

                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                    }
                    break;
                case "access_rmi_service":

                    if(strs.length==2){
                        String tcpservkey = strs[1];
                        StringBuilder sb = new StringBuilder();
                        sb.append("access_rmi_service ")
                                .append(this.unique_id).append(" ")
                                .append(this.st_auth_hash).append(" ")
                                .append(strs[1]);

                        this.st_out.println(sb.toString());
                        this.st_out.flush();

                        String in = this.st_in.readLine();
                        String[] parts = in.split("\\|");

                        String rmiip = parts[0];
                        int rmiport = Integer.parseInt(parts[1]);
                        String name = parts[2];
                        Instant i = Instant.parse(parts[3]);

                        try{
                            Registry r = LocateRegistry.getRegistry(rmiip,rmiport);
                            ServicesInterface tempserv = (ServicesInterface) r.lookup(name);

                            float a = tempserv.getTemp(i);
                            System.out.println(a);
                        }
                        catch (Exception e) {}

                    }
                    break;
                default:
                    System.out.println("Available Commands:\n   -> query_tcp\n   -> query_rmi\n   -> register_tcp_service description|ip|port|register_key\n   -> register_rmi_service description|ip|port|name|register_key\n   -> access_tcp_service service_key\n   -> access_rmi_service service_key");
            }
        }

        return returnValue;
    }
}