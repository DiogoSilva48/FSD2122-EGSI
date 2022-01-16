package SI;

import java.io.File;
import java.net.ServerSocket;
import java.security.PrivateKey;

import Keys.KeyHandler;

public class Server {

    public static int SI_PORT = 4444;

    public static String ST_IP = "127.0.0.1";
    public static int ST_PORT = 6789;

    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(SI_PORT);
        PrivateKey myPrivateKey = KeyHandler.getPrivateKeyFromFile(new File("src/SI/si_privatekey.txt"));
      
        while(true)
        {
            Thread t = new Thread(new SIThread(server.accept(), ST_IP, ST_PORT, myPrivateKey));
            t.start();
        }

    }
}