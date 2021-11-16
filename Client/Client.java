package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket(InetAddress.getLocalHost(), 4444);
        
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
