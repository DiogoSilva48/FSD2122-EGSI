package Client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientCommandParser {
    
    private BufferedReader system_input;

    private Socket si;
    private PrintWriter si_out;
    private BufferedReader si_in;
    
    private String st_ip;
    private int st_port;
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
        //Connecting with SI
        if(!this.flag)
        {
            switch (read) {
                case "getST":

                    this.si_out.println("getST");
                    this.si_out.flush();

                    String response = this.si_in.readLine();
                    String[] parts = response.split("\\|");
                    if(parts.length == 4)
                    {
                        this.st_ip = parts[1];
                        this.st_port = Integer.parseInt(parts[2]);
                        this.st_auth_hash = parts[3];
                        this.flag = true;
                    }
                    System.out.println("ST_IP: " + this.st_ip + " | ST_PORT: " + this.st_port + " | ST_HASH: " + this.st_auth_hash);

                    break;
                default:
                    System.out.println("Available Commands:\n   -> getST");

            }
        }
        //Connecting with ST
        else
        {
            switch (read) {
                default:
                    System.out.println("Available Commands:\n   -> None just yet :(");
            }
        }

        return returnValue;
    }
}