

package voip;

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
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class VoiceReceiverThread implements Runnable {

    static DatagramSocket2 receiving_socket;
    
    static DatagramSocket recieving_socket;

    //change this number for if statement below to socket
    int recievingSocketNumber = 2;

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

        //***************************************************
        //***************************************************
        //Open a socket to receive from on port PORT
        //DatagramSocket receiving_socket;
        try {

            System.out.println("Listening on Port: " + PORT);

            receiving_socket = new DatagramSocket2(PORT);

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

            System.out.println("Receiving and playing audio");

            //ArrayList<byte[]> fixed = new ArrayList<byte[]>();

            while (running) {

                try {

                    //Receive a DatagramPacket
                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);
                    
  
                    ArrayList<DatagramPacket> storage = new ArrayList<DatagramPacket>();
                  
                  DatagramPacket packet = new DatagramPacket(packetBuffer.array(), 0, 524);
                  
                  storage.add(packet);
                  
              
                  
                    receiving_socket.receive(packet);
                    
                   
                    if (recievingSocketNumber == 1) {

                        System.out.println("This is DatagramSocket (Normal Socket) \n");

                        long timestampRecieved;

                        int packetNumberRecieved;

                        byte[] audioData = new byte[512];

                    
                        packetNumberRecieved = buffer.getInt(); //packet number

                        timestampRecieved = buffer.getLong(); //timestamp

                        buffer.get(audioData, 10, 526); //audio data

                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");

                        String dateFormatted = formatter.format(timestampRecieved);

                        long diff = now.getTime() - Long.parseLong(dateFormatted);

                        System.out.println("Packet Number recieved\n" + packetNumberRecieved + "Timestamp received" + dateFormatted);

                        System.out.println("Timetamp difference\n" + diff);
                        System.out.println("Now playing back normal audio.Smoooothhhhh...");
                        boolean playItBack = true;

                        while (playItBack == true) {

                            player.playBlock(audioData);

                        }
///////////////////////////////////////////////////////////////////////////////////////
                    } else if (recievingSocketNumber == 2) {

                        System.out.println("This is DatagramSocket2 (Interleaving) \n");

             
                        long timestampRecieved;

                        int packetNumberRecieved;

                        byte[] audioData = new byte[512];
                  
                      
                      ArrayList<Integer> numbersStore = new ArrayList<Integer>(); 
                        
                         for(int d=0;d<storage.size();d++){
                          
                       ByteBuffer temp=ByteBuffer.wrap( storage.get(d).getData());
                      packetNumberRecieved = packetBuffer.getInt(); //packet number
                      
                           
                   timestampRecieved = packetBuffer.getLong(); //timestamp

                 packetBuffer.get(audioData, 12, 524); //get the audio data
                 
                 player.playBlock(audioData);
                 
                                            
                         }
  
                        long diff = getTimestamp() - Long.parseLong(formatTime());
                        
                        System.out.println("Packet number recieved: " + packetNumberRecieved + "\nTimestamp recieved: " + dateFormatted);

                        System.out.println("Timetamp difference\n" + diff);
                        
                        
                        
                        

                        ArrayList<ByteBuffer> fullArray = new ArrayList();

                        fullArray.add(buffer);
                        
                        
                        
                        boolean playItBack = true;
                        //store it in the array to sort them by the packetnumber
                        if (fullArray.size() == 4) {
                          
                           
                            for (int g = 0; g < fullArray.size(); g++) {

                                ByteBuffer b = fullArray.get(g);

                                b.get(audioData, 12, 526);
   
                               
                        while (playItBack == true) {

                            player.playBlock(audioData);

                        }
                                

                            }
                            
                            

                        }
                        


/////////////////////////////////////////////////////////////////////////////////////////////////////////                  
                    } else if (recievingSocketNumber == 3) {

                        System.out.println("This is DatagramSocket number 3");

                        long timestampRecieved;

                        int packetNumberRecieved;

                        byte[] audioData = new byte[512];

                        Timestamp now = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

                        packetNumberRecieved = buffer.getInt(); //packet number

                        timestampRecieved = buffer.getLong(); //timestamp

                        buffer.get(audioData, 10, 526); //get the audio data

                        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");

                        String dateFormatted = formatter.format(timestampRecieved);

                        long diff = now.getTime() - Long.parseLong(dateFormatted);

                        System.out.println("Packet number recieved: " + packetNumberRecieved + "\nTimestamp recieved: " + dateFormatted);

                        System.out.println("Timetamp difference to now:" + diff);

                        ArrayList<ByteBuffer> fullArray = new ArrayList();

                        fullArray.add(buffer);

//////////////////////////////////////////////////////////////////////////////////////
                        //sort the input like in socket 2 
                    } else if (recievingSocketNumber == 4) {

                        System.out.println("This is DatagramSocket4 (Corruption!)");

                        //checksum checking 
//////////////////////////////////////////////////////////////////////////////////////                        
                    } else if (recievingSocketNumber > 4) {

                        System.out.println("Socket number error. Whoops!");
                        System.exit(0);
                    }

                    //  player.playBlock(buffer);
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
    
  
    

    public static void interleave() {

    }


}


//                    byte[][] storage = new byte[16][512];
//                    int D = 4;
//
//                    for (int i = 0; i < 4; i++) {
//                        for (int j = 0; j < 4; j++) {
//
//                            int temp = (j * D) + (D - 1 - i);
//
//                            DatagramPacket packet = new DatagramPacket(storage[temp],
//                                    storage[temp].length, clientIP, PORT);


