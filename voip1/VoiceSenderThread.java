package voip;

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
    static DatagramSocket2 sending_socket2;
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

        //IP ADDRESS to send to
        InetAddress clientIP = null;

        try {

            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine

        } catch (UnknownHostException e) {

            System.out.println("ERROR: TextSender: Could not find client IP");

            e.printStackTrace();

            System.exit(0);

        }

        int sendingChoice = 2;

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
            ;

        } else if (sendingChoice == 2) {

            try {

                sending_socket2 = new DatagramSocket2();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
                e.printStackTrace();
                System.exit(0);

            }

            boolean running = true;

            while (running) {

                try {

                    //Initialise AudioPlayer and AudioRecorder objects
                    AudioRecorder recorder = new AudioRecorder();

                    //Capture audio data and add to voiceVector
                    System.out.println("Recording and sending Audio...");

                    //gets the audio
                    byte[] buffer = recorder.getBlock();

                    ByteBuffer packetBuffer = ByteBuffer.allocate(524);

                    // DatagramPacket packet = new DatagramPacket(packetBuffer.array(), packetBuffer.capacity(), clientIP, PORT);
                    packetBuffer.put(buffer);
                    packetNumber++;
                    packetBuffer.putLong(getTimestamp());
                    packetBuffer.putInt(packetNumber);

                    System.out.println("Timestamp Sent: " + formatTime());

                    System.out.println("Packet Number sent: " + packetNumber);

                    //interleaving 
                    ArrayList<DatagramPacket> storage = new ArrayList<DatagramPacket>();
                    ArrayList<DatagramPacket> Interleavedstuff = new ArrayList<DatagramPacket>();

                    DatagramPacket packet = new DatagramPacket(packetBuffer.array(), 0, 524);

                    if (storage.size() < 4) {
                        storage.add(packet);

                    } else if (storage.size() == 4) {
                        //interleaving

                        int d = 4;

                        //for(int s=0;s<storage.size();s++){
                        //   System.out.println("storage: "+s);
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {

                                int index = 1 * d + j;
                                int indexInterlleaved = j * d + (d - 1 - i);

                                Interleavedstuff.add(index, storage.get(indexInterlleaved));
                                System.out.println("storage: " + storage);

                                //iterate through the arraylist for the packet
                            }

                        }
                        for (int a = 0; a < Interleavedstuff.size(); a++) {
                            sending_socket2.send(Interleavedstuff.get(a));

                        }

                    }

                    recorder.close();

                } catch (IOException e) {

                    System.out.println("ERROR: TextSender: An IO error occured!Whoops! ");

                    e.printStackTrace();

                } catch (LineUnavailableException ex) {

                    Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);

                }

            }

            //Close the socket
            sending_socket.close();

            //***************************************************
        } else if (sendingChoice == 3) {

            try {

                sending_socket = new DatagramSocket2();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

                e.printStackTrace();
                System.exit(0);

            }

        } else if (sendingChoice == 4) {
            try {

                sending_socket = new DatagramSocket2();

            } catch (SocketException e) {

                System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

                e.printStackTrace();
                System.exit(0);

            }

        }

    }

    public static long getTimestamp() {
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

        return currentTimestamp.getTime();

    }

    public static String formatTime() {

        String dateFormatted = String.valueOf(getTimestamp());

        return dateFormatted;
    }

    public static void interleave() {

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
}
