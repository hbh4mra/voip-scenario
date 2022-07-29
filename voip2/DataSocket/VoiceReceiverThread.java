package voip2.DataSocket;

import CMPC3M06.AudioPlayer;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket2;

public class VoiceReceiverThread implements Runnable {

    static DatagramSocket receiving_socket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT
        //DatagramSocket receiving_socket;
        try {
            System.out.println("Listening on Port: " + PORT);
            receiving_socket = new DatagramSocket(PORT);
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

            while (running) {

                try {
                    //Receive a DatagramPacket
                    byte[] buffer = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buffer, 0, 512);

                    receiving_socket.receive(packet);

                    player.playBlock(buffer);
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
}
