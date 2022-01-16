package SI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.Signature;

import Keys.KeyHandler;

public class SIThread implements Runnable{

    private Socket socket;
    
    private String st_ip;
    private int st_port;

    private PrivateKey privateKey;

    public SIThread(Socket s, String st_ip, int st_port, PrivateKey privateKey)
    {
        this.socket = s;
        this.st_ip = st_ip;
        this.st_port = st_port;
        this.privateKey = privateKey;
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
                    case ("getST"):  
                        if(parts.length == 2)
                        {
                            String hash_auth = null;
                            try {
                                Signature signature = Signature.getInstance("SHA256withRSA");
                                signature.initSign(this.privateKey);
                                signature.update(parts[1].getBytes());
                                hash_auth = KeyHandler.toHexString(signature.sign());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(hash_auth != null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("ST").append("|").append(this.st_ip).append("|").append(this.st_port).append("|").append(hash_auth);
                                out.println(sb.toString());
                                out.flush();
                            }
                            else {
                                out.println("An error has occured generating the st authentication");
                                out.flush();
                            }
                            
                        }                  
                        
                        break;
                    case ("close"):
                        break;
                    default:
                        out.println("You have not entered a valid command");
                        out.flush();    
                }
            }

        }
    }
}
