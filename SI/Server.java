package SI;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Server {

    public static int SI_PORT = 4444;

    public static String ST_IP = "192.168.33.36";
    public static int ST_PORT = 6789;
    public static String ST_HASH_GENERATOR = "diogosilvathicc";

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation 
        BigInteger number = new BigInteger(1, hash); 
  
        // Convert message digest into hex value 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 
  
        return hexString.toString(); 
    }

    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(SI_PORT);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String encodedhash = toHexString(digest.digest(ST_HASH_GENERATOR.getBytes(StandardCharsets.UTF_8)));
      
        while(true)
        {
            Thread t = new Thread(new SIThread(server.accept(), ST_IP, ST_PORT, encodedhash));
            t.start();
        }
    }
}