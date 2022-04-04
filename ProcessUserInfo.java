import java.util.*;
import java.io.IOException;
import java.net.*;

public class ProcessUserInfo{
    
    HashMap<String,byte[]> messages;
        
    public ProcessUserInfo(){
        messages = new HashMap<>();
    }

    public static void main(String[] args) throws IOException {
        if (args.length<=0 || args.length>1){
            help();
        }
        int intError=0;
        try {
            intError = Integer.parseInt(args[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            help();
        }
        new ProcessUserInfo().process(intError);

    }

    public static void help(){
        System.out.println("java ProcessUserInfo <port>");
        System.out.println("    port : port sur lequel le programme se connecte pour la réception des datagrammes");
    }
    public void process(int n) throws IOException{

        //receive packet
        while (true){
            final byte[] buf = new byte[32];
            final DatagramSocket datagramSocket = new DatagramSocket(n);
            datagramSocket.receive(new DatagramPacket(buf, buf.length));
            datagramSocket.close();
                
            if (buf[0]==0){

                //dans le cas d'un type 0 on doit juste avoi le login pour inscrire les données dans la hashmap
                System.out.println("\nRECV: " + DataBufferizer.bufferToString(buf) + " at " + n);
                int n2 = 15;
                final byte b2 = buf[n2];
                ++n2;
                final String login = new String(DataBufferizer.readByteArray(buf, n2, (int)b2));
                messages.put(login, buf); // ajout d’une information dans la hashmap

            }else if(buf[0]==1) {

                int n2Request = 8;  //on se place en fonction du format 2
                final byte b2 = buf[n2Request];
                ++n2Request;
                final String loginRequest = new String(DataBufferizer.readByteArray(buf, n2Request, (int)b2));  //lecture 
                System.out.println("Recherche des données de "+loginRequest);

                byte[] bufRequest = messages.get(loginRequest); // récupération du buffer dans la hashmap
                
                //Lecture du buffer dans la hashmap
                System.out.println("\nRECV: " + DataBufferizer.bufferToString(bufRequest) + " at " + n);
                int n2 = 0;
                ++n2;
                final Date obj = new Date(DataBufferizer.readLong(bufRequest, n2));
                n2 += 8;
                final InetAddress byAddress = InetAddress.getByAddress(DataBufferizer.readByteArray(bufRequest, n2, 4));
                n2 += 4;
                final short short1 = DataBufferizer.readShort(bufRequest, n2);
                n2 += 2;
                final byte b2Read = bufRequest[n2];
                ++n2;
                final String str = new String(DataBufferizer.readByteArray(bufRequest, n2, (int)b2Read));

                System.out.println("Type:    " + bufRequest[0]);
                System.out.println("Date:    " + obj);
                System.out.println("Address: " + byAddress);
                System.out.println("Port:    " + short1);
                System.out.println("Login:   " + str);

            }
        }
        
    }
}