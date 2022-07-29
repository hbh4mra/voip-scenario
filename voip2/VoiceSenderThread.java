package voip2;

import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */
/**
 *
 *
 *
 * @author
 *
 */
public class VoiceSenderThread implements Runnable {

    static DatagramSocket sending_socket;
    static DatagramSocket sending_socket2;
    static DatagramSocket3 sending_socket3;
    static DatagramSocket4 sending_socket4;

    int packetNumber = 0;

    public void start() {

        Thread thread = new Thread(this);

        thread.start();

    }

    @Override

    public void run() {

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //Size of DxD block
        int d = 4;

        //IP ADDRESS to send to
        InetAddress clientIP = null;

        try {

            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine

        } catch (UnknownHostException e) {

            System.out.println("ERROR: TextSender: Could not find client IP");

            e.printStackTrace();

            System.exit(0);

        }

        int sendingChoice = 4;

        //***************************************************
        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.
        //DatagramSocket sending_socket;
        if (sendingChoice == 1) {

            try {

                sending_socket = new DatagramSocket();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

                e.printStackTrace();
                System.exit(0);

            }

        } else if (sendingChoice == 2) {

            try {

                sending_socket2 = new DatagramSocket2();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
                e.printStackTrace();
                System.exit(0);

            }

            boolean running = true;
            //interleaving 
            ArrayList<DatagramPacket> storage = new ArrayList<DatagramPacket>();
            ArrayList<DatagramPacket> Interleavedstuff = new ArrayList<DatagramPacket>();

            int i = 0;
            int j = 0;
            //Initialise AudioPlayer and AudioRecorder objects
            AudioRecorder recorder = null;
            try {
                recorder = new AudioRecorder();
            } catch (LineUnavailableException ex) {
                System.out.println("kjghjkhjkghjkgjkgj");
                Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (running) {

                try {

                    //Capture audio data and add to voiceVector
                    System.out.println("Recording and sending Audio...\n");

                    //gets the audio
                    byte[] buffer = recorder.getBlock();
                    System.out.println("buffer byte after get block test ");
                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);

                    DatagramPacket packet = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, clientIP, PORT);
                    packetBuffer.putInt(packetNumber);
                    packetBuffer.putLong(getTimestamp());
                    packetBuffer.put(buffer);

                    packetNumber++;

                    System.out.println("Timestamp retreived test: " + getTimestamp());

                    System.out.println("packet numberr  " + packetNumber);

                    System.out.println("Timestamp Sent: " + getTimestamp());

                    System.out.println("Packet Number sent: " + packetNumber);

                    System.out.println("after arraylists of intervlevaed stuff =");
//DatagramPacket packet = new DatagramPacket(packetBuffer.array(), Interleavedstuff.get(a), 524, clientIP, PORT);
                    System.out.println("datagram packet test before storage size");
                    //
                    //if (storage.size() < (d*d)) {
                    storage.add(packet);

                    //}
                    AudioPacket testPacket = new AudioPacket(1);
                    AudioPacket testPacket2 = new AudioPacket(1);
                    testPacket.compareTo(testPacket2);

                    //If storage ready to send
                    if (storage.size() == (d * d)) {
                        //Put in sending block
                        Interleavedstuff = storage;
                        //Clear storage
                        storage = new ArrayList();
                        //Reset i and j
                        i = 0;
                        j = 0;
                    }

                    //If there are packets to send
                    if (!Interleavedstuff.isEmpty()) {
                        //interleaving

                        //for(int s=0;s<storage.size();s++){
                        //   System.out.println("storage: "+s);
//                        for (int i = 0; i < 4; i++) {
//                            for (int j = 0; j < 4; j++) {
                        //int index = 1 * d + j;
                        int index = (j * d) + (d - 1 - i);

                        j++;
                        if (j == d) {
                            i++;
                            j = 0;
                        }

                        System.out.println("sending index " + index);
                        DatagramPacket temp = Interleavedstuff.get(index);

                        //Interleavedstuff.add(index, storage.get(indexInterlleaved));
                        //System.out.println("storage: " + storage);
                        //iterate through the arraylist for the packet
//                            }
                        //}
                        //for (int a = 0; a < Interleavedstuff.size(); a++) {
                        //sendign a packet so what the actual fuck is going on??!!!!
                        sending_socket2.send(temp);
                        //}

                    }

                } catch (IOException e) {

                    System.out.println("ERROR: TextSender: An IO error occured!Whoops! ");

                    e.printStackTrace();

                }

            }

        
            

            //Close the socket
            sending_socket.close();

            //***************************************************
        } else if (sendingChoice == 3) {

            try {

                sending_socket= new DatagramSocket3();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

                e.printStackTrace();
                System.exit(0);

            }
            
            
            
            
            

        } else if (sendingChoice == 4) {
            try {

                sending_socket4 = new DatagramSocket4();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

                e.printStackTrace();
                System.exit(0);

            }
                  AudioRecorder recorder =null;
              try {
                      recorder = new AudioRecorder();
                    } catch (LineUnavailableException ex) {
                        System.out.println("Recorder could not be intiated");
                        Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                  
            boolean running = true;
            
            while (running) {
                try {
                    int packetNumber = 1;
                    long timestamp;

                    byte[] buffer = new byte[512];
                
                            
                    ByteBuffer packetBuffer = ByteBuffer.allocate(532);
                    DatagramPacket packet = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, clientIP, PORT);

                    buffer= recorder.getBlock();
                     //put all the shit in here for the packet
                    //crc on sender 
                    CRC32 crcCalc = new CRC32();
                    
                    crcCalc.update(buffer, 0, buffer.length);

                    long checksum = crcCalc.getValue();

                    
                    packetBuffer.put(buffer);
                    
                    packetBuffer.putLong(checksum);
                   
                    packetBuffer.putLong(getTimestamp());
              
                    packetBuffer.putInt(packetNumber);
            
                    
                    //SEND PACKET HERE
                    sending_socket4.send(packet);
                    
                } catch (IOException ex) {
                    System.out.println("Sending on socket 4 error");
                    Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    public static long getTimestamp() {
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        System.out.println("getimestamp test");
        return currentTimestamp.getTime();

    }

    public static String formatTime() {
        System.out.println("formattime test");
        String dateFormatted = String.valueOf(getTimestamp());

        return dateFormatted;
    }

}
