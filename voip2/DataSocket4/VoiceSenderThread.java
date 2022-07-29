package voip2.DataSocket4;

import voip2.*;
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
        int d = 1;

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
        try {

            sending_socket = new DatagramSocket4();

        } catch (SocketException e) {

            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");

            e.printStackTrace();
            System.exit(0);

        }
        boolean running = true;
                
        while (running) {
            int packetNumber = 1;
            long timestamp;

            byte[] buffer = new byte[512];
            AudioRecorder recorder = null;
            try {
                recorder = new AudioRecorder();
            } catch (LineUnavailableException ex) {
                System.out.println("kjghjkhjkghjkgjkgj");
                Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {

                buffer = recorder.getBlock();
            } catch (IOException ex) {
                Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            ByteBuffer packetBuffer = ByteBuffer.allocate(532);
            DatagramPacket packet = new DatagramPacket(packetBuffer.array(), packetBuffer.array().length, clientIP, PORT);

            //put all the shit in here for the packet
            //crc on sender 
            CRC32 crcCalc = new CRC32();

            long checksum = crcCalc.getValue();

            crcCalc.update(buffer, 0, buffer.length);

            packetBuffer.put(buffer);
            packetBuffer.putLong(checksum);
            packetBuffer.putLong(getTimestamp());
            packetBuffer.putInt(packetNumber);

            try {
                sending_socket.send(packet);
            } catch (IOException ex) {
                System.out.println("Sending on socket 4 error");
                Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
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

    public static void interleave() {

    }
}
