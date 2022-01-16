package Keys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyHandler {

    public static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash); 
        StringBuilder hexString = new StringBuilder(number.toString(16)); 
        return hexString.toString(); 
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static PublicKey getPublicKeyFromFile(File f)
    {
        try {
            BufferedReader in = new BufferedReader(new BufferedReader(new FileReader(f)));
            String encodedkey = in.readLine();
            in.close();
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(hexStringToByteArray(encodedkey)));
        } catch (Exception e) {
            return null;
        }
    }

    public static PrivateKey getPrivateKeyFromFile(File f)
    {
        try {
            BufferedReader in = new BufferedReader(new BufferedReader(new FileReader(f)));
            String encodedkey = in.readLine();
            in.close();
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(hexStringToByteArray(encodedkey)));
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void main(String[] args) throws Exception{

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String secret_encod = toHexString(keyPair.getPrivate().getEncoded());
        String public_encod = toHexString(keyPair.getPublic().getEncoded());

        File file = new File("SI/si_privatekey.txt");
        PrintWriter out = new PrintWriter(file);

        out.println(secret_encod);
        out.flush();

        out.close();
        
        file = new File("ST/si_publickey.txt");
        out = new PrintWriter(file);

        out.println(public_encod);
        out.flush();
        
        out.close();       
    }
    
}
