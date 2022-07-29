package voip2;

import CMPC3M06.AudioPlayer;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;

import java.util.Calendar;
import java.util.Collections;

import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class VoiceReceiverThread implements Runnable {

    static DatagramSocket4 receiving_socket;

    //static DatagramSocket recieving_socket;
    //change this number for if statement below to socket
    int recievingSocketNumber = 4;

///////Size of DxD block used in sender
    int d = 5;///////////////////////////
/////////////////////////////
    int prevAddeddPacketID = 0;
    int currentPacketID = 0;
    int expectedNumber = 0;
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

            receiving_socket = new DatagramSocket4(PORT);
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

            ArrayList<AudioPacket> storage = new ArrayList();
            ArrayList<AudioPacket> slidingWindow = new ArrayList();
            // ArrayList<DatagramPacket> compare1 = new ArrayList<DatagramPacket>();
            //ArrayList<DatagramPacket> compare2 = new ArrayList<DatagramPacket>();
            ArrayList<AudioPacket> recievedPackets = new ArrayList<AudioPacket>();

            while (running) {

                try {

                    //Receive a DatagramPacket
                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);

                    ByteBuffer packetData = ByteBuffer.allocate(532);
                    ByteBuffer liamisatwat = ByteBuffer.allocate(532);

                    DatagramPacket packetfor4 = new DatagramPacket(liamisatwat.array(), liamisatwat.array().length);
                    System.out.println("5w");
                    receiving_socket.receive(packetfor4);

                    //Creates a packet
                    //DatagramPacket packet = new DatagramPacket(packetBuffer.array(), 0, 524);
                    // receiving_socket.receive(packet);
                    //  receiving_socket.receive(packetfor4);
                    ArrayList<ByteBuffer> packetStore = new ArrayList();

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
                        AudioPacket ap = new AudioPacket();
                        ap.decoder(packetBuffer);
                        slidingWindow.add(ap);
                        expectedNumber++;
                        //add sliding window here
                        /*
                         Sliding Window: Sliding window is another buffer that stores incoming packets
                        
                         Once the window is at a certain size (recommended same size as DxD block)
                         You sort the packets inside and then remove one which is now ready for playback. 
                        
                         The removed packet goes into storage
                         */
                        //add packets into slidingWindow
                        if (slidingWindow.size() == (d * d)) {
                            Collections.sort(slidingWindow);
                            AudioPacket temp = slidingWindow.remove(0);
                            //player.playBlock(temp.getAudio());
                            storage.add(temp);
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
                            AudioPacket currentPacket = storage.get(0);

                            packetNumberRecieved = currentPacket.getPacketID(); //packet number

                            timestampRecieved = currentPacket.getTimestamp(); //timestamp

                            //If this is the correct packet number
                            if (packetNumberRecieved != (prevPacketNumber + 1)) {
                                System.out.println("R: " + packetNumberRecieved);
                                System.out.println("P: " + prevPacketNumber);
                                int packetsLost = (packetNumberRecieved - prevPacketNumber);
                                System.out.println("Burst Length: " + packetsLost);
                                //this is where you implement repetition

                                //adding to a temporary cache of size four to repeat packets if they are lost
                                recievedPackets.add(currentPacket);
                                System.out.println("After recieved packets");

                                player.playBlock(currentPacket.getAudio());
                                //copy for the replay of storage
                                byte[] copyForReplay = storage.get(2).getAudio();
                                byte[] blankAudio = new byte[512];
//                                byte[] packetContents= new byte[524];
//                                ByteBuffer testingPC=ByteBuffer.allocate(d);
                                //storage and packets lost 
                                if (packetsLost > 4 && storage.size() > packetsLost) {
                                    System.out.println("packets lost and storage can accomodate");

                                    for (int f = 0; f < packetsLost; f++) //testing if i can get the packet data and then the packet number here.wrapping in BB for ease of use
                                    //NEED TO FIX THIS AS IT DOESNT WORK PROPERLY FOR SOME REASON :/
                                    {
                                        player.playBlock(storage.get(f).getAudio());
                                    }

                                } else if (packetsLost > 4 && storage.size() < packetsLost) {
                                    System.out.println("Storage too low:backup iniated");
                                    int leftToPlay = packetsLost - storage.size();
                                    System.out.println("Left to play number: " + leftToPlay);
                                    for (int g = 0; g < storage.size(); g++) {
                                        player.playBlock(storage.get(g).getAudio());
                                        System.out.println("Played repeated audio");

                                    }
//                                    for (int l = 0; l < leftToPlay; l++) {
//                                        
//                                        player.playBlock(recievedPackets.get(l).getAudio());
//                                        
                                }   ///DONT UNCOMMENT THIS BRACKET!!!!
//
//                                }else{
//                                       player.playBlock(blankAudio);
//                                       System.out.println("Played some Blank stuff");
//                                    }

                                //clear the cache once the old stuff hasnt been used
//                                if (recievedPackets.size() >= 4) {
////                                 
//                                    System.out.println("About to clear recieved packets");
//                                    recievedPackets.clear();
//                                    
//                                }
                            }

                            //set the packet number to the one expected so the sorter doesnt know what is going on 
                            //base the repetition on the burst length
                            //add another buffer for the packets to sit and call from 
                            //if 4 packets missing then replace with packets from that buffer 
                            //mess around with the interleaver 
                            //play the sound ASAP
                            //Once that is sorted, tidy up code
                            //spit all the infomation out
                            audioData = currentPacket.getAudio();

                            player.playBlock(audioData);
                            prevPacketNumber = packetNumberRecieved;
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
//                     } catch (IOException ex) {
//                    Logger.getLogger(VoiceReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
//                }
                    } else if (recievingSocketNumber == 3) {

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

                        System.out.println("This is DatagramSocket4 (Corruption!)");

//                        ByteBuffer packetData=ByteBuffer.allocate(532);
//                        ByteBuffer liamisatwat = ByteBuffer.allocate(532);
//                        
//                        DatagramPacket packetfor4 = new DatagramPacket(liamisatwat.array(),liamisatwat.array().length);
                        // receiving_socket.receive(packetfor4);
                        packetData = ByteBuffer.wrap(packetfor4.getData());

                        long recievedChecksum;
                        int daPacketNumberYo;
                        long timestampRecieved;

                        byte[] audioData = new byte[512];

                        //DatagramPacket packetfor4 = new DatagramPacket(liamisatwat.array(),liamisatwat.array().length);
                        //System.out.println("5w");
                        //receiving_socket.receive(packetfor4);
                        packetData = ByteBuffer.wrap(packetfor4.getData());
                        timestampRecieved = packetData.getLong(520);//8
                        recievedChecksum = packetData.getLong(512);//8
                        daPacketNumberYo = packetData.getInt(528);//4
                        packetData.get(audioData, 0, 512); //out of bounds error here?

                        System.out.println("PacketNumber recieved" + daPacketNumberYo);

                        System.out.println("Recieved checksum" + recievedChecksum);

                        System.out.println("timestamp recieved" + timestampRecieved);

                        CRC32 recievedCRC = new CRC32();

                        recievedCRC.update(audioData, 0, audioData.length);

                        System.out.println("Checksum value on receiver side" + recievedCRC.getValue());
                        packetStore.add(packetData);

                        if (recievedCRC.getValue() != recievedChecksum) {

                            // BitSet.valueOf(audioData).flip(0,audioData.length/2);
                            System.out.println("-----Corrupt!");

//                              if(packetStore.size()>1){
//                            ByteBuffer temp= packetStore.get(0);
//                            temp.get(prevAudio, 0, 512);
//                           // BitSet.valueOf(prevAudio).flip(0, prevAudio.length/2);
//                            player.playBlock(audioData);
//                                
//                            }else{
//                                System.out.println("Blank audio here as packet store empty");
//                                player.playBlock(blankAudio);
//                                
//                            }
                            //repetition
                            //player.playBlock(prevAudio);

                            //play silence
                            byte[] blankAudio = new byte[512];
                            player.playBlock(blankAudio);
                            System.out.println("blank audioplayed here");

                        } else {

                            System.out.println("----No corruprted");

                            player.playBlock(audioData);
                            prevAudio = audioData;

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

                } catch (SocketTimeoutException e) {

                } catch (IOException e) {

                    System.out.println("ERROR: TextReceiver: Some random IO error occured!");

                    e.printStackTrace();

                }

            }

        } catch (LineUnavailableException ex) {

            Logger.getLogger(VoiceReceiverThread.class.getName()).log(Level.SEVERE, null, ex);

        }

        //Close the socket
        receiving_socket.close();

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

}
