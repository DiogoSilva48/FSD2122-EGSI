package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;


import java.security.*;

public class Client {

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
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

        //These closes can be placed inside the 'handle' function
        input.close();
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();


        /*
        // Codigo para assinar e validar a assinatura
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());

        String info = "192.168.33.36 6789";

        signature.update(info.getBytes());
        byte[] holy = signature.sign();


        String s = toHexString(holy);
        byte[] guacamole = hexStringToByteArray(s);

        for(int i = 0;i<holy.length;i++)
        {
            if(holy[i] != guacamole[i])
            {
                System.out.println("ALERTA CM");
            }
        }
        System.out.println(s);

        Signature abc = Signature.getInstance("SHA256withRSA");
        abc.initVerify(keyPair.getPublic());
        abc.update(info.getBytes());

        System.out.println("The signature is " + abc.verify(guacamole));*/

    }
}
