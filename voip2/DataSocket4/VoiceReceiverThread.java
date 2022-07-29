package voip2.DataSocket4;

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

import uk.ac.uea.cmp.voip.DatagramSocket4;

public class VoiceReceiverThread implements Runnable {

    static DatagramSocket4 receiving_socket;

    //static DatagramSocket recieving_socket;
    //change this number for if statement below to socket
    int recievingSocketNumber = 4;

///////Size of DxD block used in sender
    int d = 1;///////////////////////////
/////////////////////////////

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

            ArrayList<DatagramPacket> storage = new ArrayList<DatagramPacket>();

            while (running) {

                try {

                    //Receive a DatagramPacket
//                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);
//
//                    DatagramPacket packet = new DatagramPacket(packetBuffer.array(), 0, 524);
//
//                    //receiving_socket.receive(packet);
//                    //storage.add(packet); //THIS NEEDS TO BE DONE ELSEWHERE FOR INTERLEAVING
//                    //receiving_socket.close();
                    //System.out.println("This is DatagramSocket4 (Corruption!)");

                    long recievedChecksum;
                    int daPacketNumberYo;
                    long timestampRecieved;

                    byte[] audioData = new byte[512];
                    byte[] blankAudio = new byte[512];

                    ByteBuffer newbuff = ByteBuffer.allocate(532);

                    DatagramPacket packetfor4 = new DatagramPacket(newbuff.array(), 0, 532);

                    receiving_socket.receive(packetfor4);
                    storage.add(packetfor4);

                    ByteBuffer packetData = ByteBuffer.wrap(packetfor4.getData());

                    daPacketNumberYo = packetData.getInt();
                    recievedChecksum = packetData.getLong();
                    timestampRecieved = packetData.getLong();

                    packetData.get(audioData, 0, 512);

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
//////////
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

    public static void interleave() {

    }
}
