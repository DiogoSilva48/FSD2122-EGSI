package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

                    System.out.println(this.st_in.readLine());
                    break;
                
                case "query_rmi":

                    this.st_out.println("query_rmi " + this.unique_id + " " + this.st_auth_hash);
                    this.st_out.flush();

                    System.out.println(this.st_in.readLine());
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

                default:
                    System.out.println("Available Commands:\n   -> query_tcp\n   -> query_rmi\n   -> register_tcp_service description|ip|port|register_key\n   -> register_rmi_service description|ip|port|name|register_key\n");
            }
        }

        return returnValue;
    }
}