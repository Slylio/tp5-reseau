import java.util.*;
import java.io.IOException;
import java.net.*;

public class ProcessUserInfo{
    
    Map<String,byte[]> messages;
    private int port;
    private String address;
    private int periode;

    public ProcessUserInfo( String address,int port){
        messages = new HashMap<>();
        this.port=port;
        this.address=address;
        this.periode=5000;
    }

    public static void main(String[] args) throws IOException {
        try {

            new ProcessUserInfo(args[0],Integer.parseInt(args[1])).run();
        }
        catch (Exception e) {
            help();
        }
    }

    public static void help(){
        System.out.println("java ProcessUserInfo <adresse> <port>");
        System.out.println("    port : port sur lequel le programme se connecte pour la réception des datagrammes");
    }
    public void run(){
        Thread processThread = new Thread(){
            public void run(){
                //receive packet
                while (true){
                    byte[] buf = new byte[32];
                    InetAddress group = InetAddress.getByName(address); //création groupe
                    MulticastSocket multicastSocket = new MulticastSocket(port);    //création du multicast Socket
                    DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                    datagramPacket.joinGroup(group);    //on abonne le datagramsocket au groupe
                    datagramPacket.receive(new DatagramPacket(buf, buf.length));
                    datagramPacket.close();
                        
                    
                    //cas écriture
                    if (buf[0]==1){
                        //dans le cas d'un type 0 on doit juste avoir le login pour inscrire les données dans la hashmap
                        System.out.println("\nType 1RECV: " + DataBufferizer.bufferToString(buf) + " at " + port);
                        int n2 = 15;
                        final byte b2 = buf[n2];
                        ++n2;
                        final String login = new String(DataBufferizer.readByteArray(buf, n2, (int)b2));
                        messages.put(login, buf); // ajout d’une information dans la hashmap
                        System.out.println(messages.get(login));
                    
                    //cas lecture
                    }else if(buf[0]==2) {
        
                        //lecture du login pour chercher dans la hash map
                        int n2Request = 7;  //on se place en fonction du format 2
                        byte b2 = buf[n2Request];
                        ++n2Request;
                        String loginRequest = new String(DataBufferizer.readByteArray(buf, n2Request, (int)b2));  //lecture 
                        System.out.println("Recherche des données de "+loginRequest);
        
                        //on prend le buffer a partir du login dans la hashmap 
                        byte[] bufRequest = messages.get(loginRequest); // récupération du buffer dans la hashmap
                            
                        //Lecture du buffer de la hashmap et affichage
                        System.out.println("\nType 2 RECV: " + DataBufferizer.bufferToString(bufRequest) + " at " + port);
                        int n2 = 0;
                        ++n2;
                        Date date = new Date(DataBufferizer.readLong(bufRequest, n2));
                        n2 += 8;
                        InetAddress byAddress = InetAddress.getByAddress(DataBufferizer.readByteArray(bufRequest, n2, 4));
                        n2 += 4;
                        short short1 = DataBufferizer.readShort(bufRequest, n2);
                        n2 += 2;
                        byte b2Read = bufRequest[n2];
                        ++n2;
                        String str = new String(DataBufferizer.readByteArray(bufRequest, n2, (int)b2Read));
        
                        System.out.println("Type:    " + bufRequest[0]);
                        System.out.println("Date d'expiration:    " + date);
                        System.out.println("Address: " + byAddress);
                        System.out.println("Port:    " + short1);
                        System.out.println("Login:   " + str);
                    
                    //cas suppression 
                    } else if (buf[0]==3){
                        System.out.println("Type 3 RECV: " + DataBufferizer.bufferToString(buf) + " at " + port);
                        byte n = buf[1];
                        String login = new String(DataBufferizer.readByteArray(buf, 2,n));
                        messages.remove(login);
                        System.out.println("Supression du login dans la hashMap..");
                    }
                }
            }
        };
        processThread.start();
        Thread disconnectionThread = new Thread(){
            public void run(){
                //toujours tester les personnes dans la table
                while (true){
                    //on vérifie tout le monde
                    System.out.println("Utilisateurs connectés:");
                    for (Map.Entry<String,byte[]> entry:messages.entrySet()){

                        //on prend la date limite de chacun
                        Date dateEntry = new Date(DataBufferizer.readLong(entry.getValue(), 1));

                        //si cette date est supérieure a la date actuelle on supprime de la table
                        if (dateEntry.after(new Date())){
                            messages.remove(entry);
                        }
                        System.out.println(entry.getKey());
                    }
                    sleep(periode); //5 secondes d'attente
                }
            } 
        };
        disconnectionThread.start();
    }        
}
