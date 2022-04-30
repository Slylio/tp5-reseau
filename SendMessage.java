import java.io.*;
import java.net.*;
public class SendMessage {
    private String address;
    private int socketNum;
    private String loginSrc;
    private String loginDest;
    private String message;

    public SendMessage(String address, int socketNum, String loginSrc, String loginDest){
        this.address=address;
        this.socketNum=socketNum;
        this.loginSrc=loginSrc;
        this.loginDest=loginDest;
    }

    private static void usage() {
        System.out.println("java SendMessage <adresse> <port> <loginSource> <loginDest>");
        System.out.println("Send a message to loginDest");
        System.exit(-1);
    }
    public static void main(String[] args) throws IOException {
        if (args.length!=4){
            //TESTS
            new SendMessage("localhost", 9632, "machin","bidule").sendMessage();
            usage();
            
        } else {
            new SendMessage(args[0],Integer.parseInt(args[1]),args[2],args[3]).sendMessage();
        }
    }

    public void sendMessage() throws IOException{
        BufferedReader entree = new BufferedReader
            (new InputStreamReader(System.in));
        while (!(entree.readLine()).equals(".")) {
            message += entree.readLine();
            message +="\n";
        }
        //écriture de la data dans un tableau de 4080 byte maximum
        //initialisation
        byte[] data = new byte[4080];
        
        //conversion
        data=message.getBytes();

        //calcul l
        short l;
        if (data.length%16==0){
            l=(short) (data.length/16);
        } else {
            l=(short) (data.length/16+1);
        }

        //le buffer du datagram
        byte[] buf = new byte[4116]; //maximum size of the datagram
        
        int offset=0;
        DataBufferizer.writeByte((byte) 4, buf, offset);
        offset+=1;

        //buffer loginSource et loginDest
        byte[] dataSource = new byte [16];
        byte[] dataTarget = new byte [16];

        dataSource=loginSrc.getBytes();
        dataTarget=loginDest.getBytes();

        int n=dataSource.length;
        int m=dataTarget.length;

        //réécriture sur le buffer général
        
        //écriture n
        DataBufferizer.writeByte((byte)n, buf, offset);
        offset+=1;
        
        //écriture dataSource
        DataBufferizer.writeByteArray(dataSource, buf, offset, n);
        offset+=16;
        
        //écriture m
        DataBufferizer.writeByte((byte)m, buf, offset);

        //écriture dataSource
        DataBufferizer.writeByteArray(dataTarget, buf, offset, m);
        offset+=16;

        //écriture l et data
        DataBufferizer.writeUnsignedByte(l, buf, offset);
        offset+=1;
        
        DataBufferizer.writeByteArray(data, buf, offset, data.length);

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, 
                                InetAddress.getByName(address), socketNum);
        socket.send(packet);
    }
}
