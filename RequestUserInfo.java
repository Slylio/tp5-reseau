import java.io.IOException;
import java.net.*;
import java.util.Date;

public class RequestUserInfo {
    private int socketNumSender;
    private int socketNumReceiver;

    private String stringAddressSender;
    private String stringAddressReceiver;
    
    private String login;

    public RequestUserInfo(String stringAddressReceiver,int socketNumReceiver,String stringAddressSender,int socketNumSender, String login){
        this.stringAddressReceiver=stringAddressReceiver;
        this.socketNumReceiver=socketNumReceiver;
        this.stringAddressSender=stringAddressSender;
        this.socketNumSender=socketNumSender;
        this.login=login;
    }

    private static void usage() {
        System.out.println("java SendUserInfo <adresse1> <port1> <adresse2> <port2> <login>");
        System.out.println("Send an information packet");
        System.out.println("With:");
        System.out.println("   adresse1, port1:   coordonates of ProcessUserInfo");
        System.out.println("   adresse2,port2,login:   coordonates of sender");
        System.exit(-1);
    }
    public static void main(String[] args) throws IOException {
        if (args.length!=5){
            //TESTS
            //RequestUserInfo requestUserInfo = new RequestUserInfo("localhost", 9632, "localhost", 9632, "emilio");
            //requestUserInfo.sendPacket();
            usage();
            
        } else {
            RequestUserInfo requestUserInfo = new RequestUserInfo(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), args[4]);
            requestUserInfo.sendPacket();
        }
    }


    public void sendPacket() throws IOException {
        
        byte[] buf = new byte[24];
        int offset=0;
        // Write type
        DataBufferizer.writeByte((byte) 2, buf, offset); 
        offset+=1;

        //Write address of sender
        InetAddress addressInfo = InetAddress.getByName(stringAddressSender);
        DataBufferizer.writeByteArray(addressInfo.getAddress(), buf, 1, 4);
        offset+=4;

        //Write socket of sender
        DataBufferizer.writeShort((short) socketNumSender, buf, 5);
        offset+=2;

        //Write the login buffer
        byte[] bufLogin = new byte[16];       
        DataBufferizer.writeByteArray(login.getBytes(), bufLogin, 0,login.getBytes().length);

        //then add it to the global buffer
        DataBufferizer.writeByte((byte) login.getBytes().length, buf, offset);
        DataBufferizer.writeByteArray(bufLogin, buf, 8,login.getBytes().length);

        //send datagram
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(stringAddressReceiver);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, 
                                address, socketNumReceiver);
        socket.send(packet);
    }
}
