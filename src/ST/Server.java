package ST;

import Keys.KeyHandler;

import java.io.File;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static int ST_PORT = 6789;

    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(ST_PORT);
        Map<String,ServicoRedeSockets> servicos_java_tcp = new HashMap<>();;
        Map<String,ServicoRedeRMI> servicos_java_rmi = new HashMap<>();
        PublicKey pk = KeyHandler.getPublicKeyFromFile(new File("src/ST/si_publickey.txt"));
        while(true)
        {
            Thread t = new Thread(new SThread(server.accept(),servicos_java_tcp,servicos_java_rmi,pk));
            t.start();
        }
    }
    
}
