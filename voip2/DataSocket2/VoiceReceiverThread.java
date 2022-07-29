package voip2.DataSocket2;

import voip2.*;
import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class VoiceReceiverThread implements Runnable {

    static DatagramSocket2 receiving_socket;

    //static DatagramSocket recieving_socket;
    //change this number for if statement below to socket
    int recievingSocketNumber = 1;

///////Size of DxD block used in sender
    int d = 1;///////////////////////////
/////////////////////////////
    int prevAddeddPacketID = 0;
    int currentPacketID=0;
    byte[] prevAudio = new byte[512];

    public void start() {

        Thread thread = new Thread(this);

        thread.start();

    }

    InetAddress clientIP = null;

    @Override

    public void run() {

        //***************************************************
        //Port to open socket on has to be 55555 for uni systems!
        int PORT = 55555;
        int timeout = 32;

        //***************************************************
        //***************************************************
        //Open a socket to receive from on port PORT
        //DatagramSocket receiving_socket;
        try {

            System.out.println("Listening on Port: " + PORT);

            receiving_socket = new DatagramSocket2(PORT);
            receiving_socket.setSoTimeout(timeout);

            System.out.println("Listener connected...");

        } catch (SocketException e) {

            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");

            e.printStackTrace();

            System.exit(0);

        }

        //***************************************************
        //***************************************************
        //Main loop.
        AudioPlayer player;

        try {

            player = new AudioPlayer();

            boolean running = true;
            boolean playing = false;

            System.out.println("Receiving and playing audio");
            int prevPacketNumber = 0;

            ArrayList<DatagramPacket> storage = new ArrayList<DatagramPacket>();
            ArrayList<DatagramPacket> slidingWindow = new ArrayList<DatagramPacket>();
           // ArrayList<DatagramPacket> compare1 = new ArrayList<DatagramPacket>();
            //ArrayList<DatagramPacket> compare2 = new ArrayList<DatagramPacket>();
            ArrayList<AudioPacket> recievedpackets = new ArrayList<AudioPacket>();
            while (running) {

                try {

                    //Receive a DatagramPacket
                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);

                    DatagramPacket packet = new DatagramPacket(packetBuffer.array(), 0, 524);

                    receiving_socket.receive(packet);

                    AudioPacket ap = new AudioPacket();
                    ap.decoder(packetBuffer);
                    
                    recievedpackets.add(ap);
                    Collections.sort(recievedpackets);
                    player.playBlock(ap.getAudio());
                    storage.add(packet); //THIS NEEDS TO BE DONE ELSEWHERE FOR INTERLEAVING

                    if (recievingSocketNumber == 1) {

                        System.out.println("This is DatagramSocket (Normal Socket) \n");

                        long timestampRecieved;
                        int packetNumberRecieved;

                        byte[] audioData = new byte[512];

                        packetNumberRecieved = packetBuffer.getInt(); //packet number

                        timestampRecieved = packetBuffer.getLong(); //timestamp

                        packetBuffer.get(audioData, 12, 524); //audio data

                        long diff = getTimestamp() - Long.parseLong(formatTime());

                        System.out.println("Packet Number recieved\n" + packetNumberRecieved + "Timestamp received" + timestampRecieved);

                        System.out.println("Timetamp difference\n" + diff);
                        System.out.println("Now playing back normal audio.Smoooothhhhh...");
                        boolean playItBack = true;

                        while (playItBack == true) {

                            player.playBlock(audioData);

                        }
///////////////////////////////////////////////////////////////////////////////////////
                    } else if (recievingSocketNumber == 2) {

                        System.out.println("This is DatagramSocket2 (Interleaving) \n");
                        System.out.println("Storage Size: " + storage.size());

                        long timestampRecieved;

                        int packetNumberRecieved;

                        byte[] audioData = new byte[512];

                        //add sliding window here
                        /*
                         Sliding Window: Sliding window is another buffer that stores incoming packets
                        
                         Once the window is at a certain size (recommended same size as DxD block)
                         You sort the packets inside and then remove one which is now ready for playback. 
                        
                         The removed packet goes into storage
                         */
                        //add packets into slidingWindow
                        slidingWindow.add(packet);
                        
//                        DatagramPacket[] dp = new DatagramPacket[slidingWindow.size()];
                        if (slidingWindow.size() >= (d * d)) {
                            //DatagramPacket[] dp = new DatagramPacket[slidingWindow.size()];
//                            dp = (DatagramPacket[]) slidingWindow.toArray(dp);
//                            System.out.println("before sort\n"+Arrays.toString(dp));
//                            Arrays.sort(dp);
//                            System.out.println(Arrays.toString(dp));
                            for (int i = 0; i < slidingWindow.size(); i++) {
                                //Collections.sort(theArrayListofAudioPackets);
                                //arraylist =goten from packets in 

                               // Integer currentID = ((AudioPacket) obj1).getPacketID();
                              // Integer otherID = ((AudioPacket) obj2).getPacketID();
                                
//                                byte[] compare1 = storage.get(i).getData();
//                                byte[] compare2 = slidingWindow.get(i).getData();
//                                
//                                ByteBuffer compare1wrap=ByteBuffer.wrap(compare1);
//                                ByteBuffer compare2wrap=ByteBuffer.wrap(compare2);
//                                
//                                int choice=0;
//                                
//                                
//                                if (compare1wrap.getInt() > compare2wrap.getInt()) {
//                                    choice=1;
//                                } else if (compare1wrap.getInt()< compare2wrap.getInt()) {
//                                    choice=2;
//                                } else {
//                                    choice=3;
//                                }
//                                
//                                if(choice==1){
//                                    //store in storage
//                                    storage.set(compare1wrap.getInt(),packet);
//                                    Arrays.sort(storage.toArray());
//                                }else if(choice==2){
//                                    
//                                   
//                                    
//                                }else if(choice==3){
//                                  
//                                    
//                                    
//                                }else{
//                                    System.out.println("Error with the logic in compare");
//                                    System.exit(1);
//                            }

                           // MyQuickSort sorter = new MyQuickSort();
                            
                            AudioPacket Audiopacket = new AudioPacket();
                                    
                           // Audiopacket.compareTo(slidingWindow);
                            Audiopacket.compareTo(Audiopacket);
                              
                            DatagramPacket gotFromSlidingWindow = slidingWindow.get(i);

                            prevAddeddPacketID++;//iterates after each packet for number to set to
                            System.out.println("tempyNumber test for iterator:" + prevAddeddPacketID);
                            ByteBuffer pktinques = ByteBuffer.wrap(gotFromSlidingWindow.getData());

                            int packetNumYo = pktinques.getInt();

                            if (packetNumYo == prevAddeddPacketID + 1) {

                                //storage.add(slidingWindow.get(i));
                                storage.set(prevAddeddPacketID, slidingWindow.get(i));

                            } else if (packetNumYo == 0 || packetNumYo > prevAddeddPacketID) {

                                storage.set(i, slidingWindow.get(i - 1));
                                //repetition as it just gets the previous packet from the array and then sends it
                            }
                            // slidingWindow.clear();
                        }
                        slidingWindow.clear();
                    }

                        //       Collections.sort(AudioPacket, storage);
                    //if sliding window has enough packets, play one
                    if (storage.size() >= 4 || playing == true) {
                        playing = true;
                        System.out.println("Attempting to play");

//                            for (int h = 1; h < storage.size(); h++) {
//
//                                for (int d = 4; d == 1; d--) {
                        //ByteBuffer beforeTemp = ByteBuffer.wrap(storage.get(0).getData());
                        ByteBuffer currentPacket = ByteBuffer.wrap(storage.get(0).getData());

                        packetNumberRecieved = currentPacket.getInt(); //packet number

                        timestampRecieved = currentPacket.getLong(); //timestamp

                        //If this is the correct packet number
//                                    if (packetNumberRecieved == prevPacketNumber +1) { //DO NEED THESE FOR REPETITION
                        currentPacket.get(audioData, 0, 512);

                        player.playBlock(audioData);
                        System.out.println("Packet number recieved: " + packetNumberRecieved + "\nTimestamp recieved: " + getTimestamp());
                        storage.remove(0);

                            //  timestampRecieved = temp.getLong(); //timestamp
                        //packetBuffer.get(audioData, 12, 524); //get the audio data
                        Date receivedTime = new Date(timestampRecieved);
                        Date currentTime = new Date();
                        long diff = currentTime.getTime() - receivedTime.getTime();

                        // System.out.println("Packet number recieved: " + packetNumberRecieved + "\nTimestamp recieved: " +getTimestamp());
                        System.out.println("Timetamp difference(Delay)\n" + diff);

                        //System.out.println("sliding windw:\n" + Arrays.toString(dp));
                    }

                    //store it in the array to sort them by the packetnumber
/////////////////////////////////////////////////////////////////////////////////////////////////////////                  
                }else if (recievingSocketNumber == 3) {

                        System.out.println("This is DatagramSocket number 3");

//                        long timestampRecieved;
//
//                        int packetNumberRecieved;
//
//                        byte[] audioData = new byte[512];
//                        int tempyNumber = 0;
//
//                        ArrayList<ByteBuffer> numbersStore = new ArrayList<ByteBuffer>();
//
//                        
//                        long diff = getTimestamp() - Long.parseLong(formatTime());
//
//                        // System.out.println("Packet number recieved: " + packetNumberRecieved + "\nTimestamp recieved: " +getTimestamp());
//                        System.out.println("Timetamp difference(Delay)\n" + diff);
//////////////////////////////////////////////////////////////////////////////////////
                        //sort the input like in socket 2 
                    } else if (recievingSocketNumber == 4) {
                        receiving_socket.close();

                        System.out.println("This is DatagramSocket4 (Corruption!)");

                        System.out.println("1w");
                        long recievedChecksum;
                        int daPacketNumberYo;
                        long timestampRecieved;
                        System.out.println("2w");
                        byte[] audioData = new byte[512];
                        byte[] blankAudio = new byte[512];
                        System.out.println("3w");
                        ByteBuffer liamisatwat = ByteBuffer.allocate(532);
                        System.out.println("4w");
                        DatagramPacket packetfor4 = new DatagramPacket(liamisatwat.array(), 0, 532);
                        System.out.println("5w");
                        receiving_socket.receive(packetfor4);

                        ByteBuffer packetData = ByteBuffer.wrap(packetfor4.getData());

                        daPacketNumberYo = packetData.getInt();
                        recievedChecksum = packetData.getLong();
                        timestampRecieved = packetData.getLong();

                        packetData.get(audioData, 18, 532);

                        System.out.println("recieved checksum" + recievedChecksum);
                        System.out.println("packet number got" + daPacketNumberYo);
                        System.out.println("timestamp recieved" + timestampRecieved);

                        CRC32 recievedCRC = new CRC32();

                        recievedCRC.update(audioData, 0, audioData.length);

                        if (recievedCRC.getValue() != recievedChecksum) {
                            System.out.println("Corrupt!!!!Pannnniiicccc!!!");
                            player.playBlock(blankAudio);
                            System.out.println("blank audioplayed here");
                            //play half a packet as something?

                        } else {
                            System.out.println("Normal stuff");
                            player.playBlock(audioData);
                        }
                        
                        //put checksum into header (extra 8 bytes to yout buffer that you send)
                        //At the receiver extract the sound out of the packet do
                        //CRC32 crap2 = new CRC32();
                        //crap2.update(sound file here, 0, sound file here.length)
                        //extract the long checksum you added to the header which will be the extra 8 bytes you added
                        //crap2.getValue == long checksum from packet header
                        //if they are equal the packet is ok if not it is corrupt and you play empty sound or repeat previous
                        //if(crap2.getValue() == long checksum) { not corrupt play as normal}
                        //else{ corrupt }
                        //to do empty sound - byte [] emptysound = new byte[512];
                        //player.playBlock(emptySound);
//////////////////////////////////////////////////////////////////////////////////////                        
                    } else if (recievingSocketNumber > 4) {

                        System.out.println("Socket number error. Whoops! Now going to exit");
                        System.exit(0);
                    }

            }catch (SocketTimeoutException e) {

                }catch (IOException e) {

                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");

                    e.printStackTrace();

                }

        }

    }
    catch (LineUnavailableException ex

    
        ) {

            Logger.getLogger(VoiceReceiverThread.class.getName()).log(Level.SEVERE, null, ex);

    }

    //Close the socket
    receiving_socket.close ();

    //***************************************************
}

public static long getTimestamp() {
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

        return currentTimestamp.getTime();

    }

    public static String formatTime() {

        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");

        String dateFormatted = formatter.format(String.valueOf(getTimestamp()));

        return dateFormatted;
    }

    public static void interleave() {

    }

}
