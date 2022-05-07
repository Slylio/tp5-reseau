import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutionException;

import org.w3c.dom.UserDataHandler;

public class DeleteUserInfo {
    private String address;
    private int port;
    private String login;

    public DeleteUserInfo(String address,int port, String login){
        this.address=address;
        this.port=port;
        this.login=login;
    }

    public static void main(String[] args) {
        if (args.length!=3){
            usage();
        } else {
            try {
                new DeleteUserInfo(args[0],Integer.parseInt(args[1]),args[2]).sendUserInfoToDelete();
            } catch(Exception e){
                usage();
                System.err.println(e);
            }
        }
    }

    public static void usage(){
        System.out.println("DeleteUserInfo <adresse> <port> <login>");
        System.exit(-1);
    } 

    public void sendUserInfoToDelete(){
        byte[] buf = new byte[18];
        int offset=0;
        
        // Write type
        DataBufferizer.writeByte((byte) 3, buf, offset);
        offset+=1;

        //Write the login buffer
        byte[] bufLogin = new byte[16];       
        DataBufferizer.writeByteArray(login.getBytes(), bufLogin, 0,login.getBytes().length);
        //get length
        int n=login.getBytes().length;
        //then add it to the global buffer
        DataBufferizer.writeByte((byte) n, buf, offset);
        offset+=1;
        DataBufferizer.writeByteArray(bufLogin, buf, offset,n);

        //send datagram
        
        try (
            DatagramSocket socket = new DatagramSocket()) {
            InetAddress inetAddress = InetAddress.getByName(address);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, 
                                    inetAddress, port);
            socket.send(packet);
            System.out.println("Envoi datagram de suppression...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
