package voip2.DataSocket;

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
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author
 */
public class VoiceSenderThread implements Runnable {

    static DatagramSocket sending_socket;

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
            clientIP = InetAddress.getByName("CMPLEWIN-04");  //CHANGE localhost to IP or NAME of client machine
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.
        //DatagramSocket sending_socket;
        try {
            sending_socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Get a handle to the Standard Input (console) so we can read user input
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //***************************************************

        //***************************************************
        //Main loop.
        boolean running = true;

        while (running) {

            try {
                //Initialise AudioPlayer and AudioRecorder objects
                AudioRecorder recorder = new AudioRecorder();
                AudioPlayer player = new AudioPlayer();

                //Capture audio data and add to voiceVector
                System.out.println("Recording and sending Audio...");

                boolean getAudio = true;

                while (getAudio) {
                    byte[] buffer = recorder.getBlock();
//                    Vector<byte[]> voiceVector = new Vector<byte[]>();

                    player.playBlock(buffer);
//                    Iterator<byte[]> voiceItr = voiceVector.iterator();
//                    while (voiceItr.hasNext()) {
//                        player.playBlock(voiceItr.next());
//                    }

                    //Make a DatagramPacket from it, with client address and port number
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, PORT);

                    //Send it
                    sending_socket.send(packet);
                }

            } catch (IOException e) {
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(VoiceSenderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Close the socket
        sending_socket.close();
        //***************************************************
    }

}
