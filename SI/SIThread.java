package SI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

public class SIThread implements Runnable{

    private Socket socket;
    
    private String st_ip;
    private int st_port;
    private String st_auth_hash;

    public SIThread(Socket s, String st_ip, int st_port, String st_auth)
    {
        this.socket = s;
        this.st_ip = st_ip;
        this.st_port = st_port;
        this.st_auth_hash = st_auth;
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

            String[] part;
            if (read != null) {
                part = read.split(" ");
                String op = part[0];

                switch (op) {
                    case ("getST"):
                        StringBuilder sb = new StringBuilder();
                        sb.append("ST").append("|").append(this.st_ip).append("|").append(this.st_port).append("|").append(this.st_auth_hash);
                        out.println(sb.toString());
                        out.flush();
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
