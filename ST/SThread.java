package ST;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

import Keys.KeyHandler;

public class SThread implements Runnable{

    private Socket socket;

    private Map<String,ServicoRedeSockets> servicos_java_tcp;
    private Map<String,ServicoRedeRMI> servicos_java_rmi;

    private PublicKey siPublicKey;

    public SThread(Socket socket, Map<String,ServicoRedeSockets> servicos_java_tcp, Map<String,ServicoRedeRMI> servicos_java_rmi, PublicKey siPublicKey){
        this.socket = socket;
        this.servicos_java_tcp = servicos_java_tcp;
        this.servicos_java_rmi = servicos_java_rmi;
        this.siPublicKey = siPublicKey;
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
                                    
                                    out.println("great_success_tcp");
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

                                    out.println("great_success_rmi");
                                    out.flush();
                                }
                            }                     
                        }
                        break;    
                    case ("query_tcp"):
                        if(parts.length == 3){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                out.println("querytcp_authorized");
                                out.flush();  
                            }                     
                        }
                        break;
                    case ("query_rmi"):
                        if(parts.length == 3){
                            if(authorization_checker(parts[1], parts[2]))
                            {
                                out.println("queryrmi_authorized");
                                out.flush();   
                            }                     
                        }
                        break;    
                    default:
                        out.println("You have not entered a valid command");
                        out.flush();    
                }
            }

        }
    }
    
}
