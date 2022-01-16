package Client;

import ServiceTemperaturePublic.Server.ServicesInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Instant;

public class Client {

    public static String SI_IP = "127.0.0.1";
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(SI_IP, 4444);
        
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        ClientCommandParser ccp = new ClientCommandParser(input, socket, in, out);

        while(true)
        {
            int r = ccp.handle(input.readLine());
            if(r != 0) {
                break;
            }
        }

        //This closes can be placed inside the 'handle' function
        input.close();
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();

    }
}
