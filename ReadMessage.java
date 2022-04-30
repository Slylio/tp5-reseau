import java.io.*;
import java.net.*;
import java.util.Date;

public class ReadMessage {
    
    private int socketNum;

    public ReadMessage(int socketNum){
        this.socketNum=socketNum;
    }

    private static void usage() {
        System.out.println("java ReadMessage <port>");
        System.out.println("Receive and display messages");
        System.exit(-1);
    }
    public static void main(String[] args) throws IOException {
        if (args.length!=1){
            //TESTS
            new ReadMessage(9632).receiveMessage();
            usage();

        } else {
            new ReadMessage(Integer.parseInt(args[0])).receiveMessage();
        }
    }

    public void receiveMessage() throws IOException{
        //receive packet
        while (true){
            byte[] buf = new byte[4080];
            DatagramSocket datagramSocket = new DatagramSocket(socketNum);
            datagramSocket.receive(new DatagramPacket(buf, buf.length));
            datagramSocket.close();

            int offset=1;

            //n
            int n = DataBufferizer.readInt(buf, offset);
            offset+=1;
            
            //source login
            String loginSource = DataBufferizer.readByteArray(buf, offset, n).toString();
            offset+=16;
            
            //m
            int m = DataBufferizer.readInt(buf, offset);
            offset+=1;
            
            //target login
            String loginTarget = DataBufferizer.readByteArray(buf, offset, m).toString();
            offset+=16;
            
            //l
            short l = DataBufferizer.readUnsignedByte(buf, offset);
            offset+=1;
            
            //data
            String message = DataBufferizer.readByteArray(buf, offset, l*16).toString();

            System.out.println("From: "+loginSource);
            System.out.println("To: "+loginTarget);
            System.out.println("Date: "+new Date());
            System.out.println("");
            System.out.println(message);
        }
    }
}
