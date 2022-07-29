/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voip2;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author khr12yfu
 */
public class AudioPacket implements Comparable<AudioPacket> {

    ByteBuffer bf = ByteBuffer.allocate(524);
    //4id-8checksum-8timestamp-512data
    private int packetID;
    private long timestamp;
    private byte[] audio;

    public AudioPacket(int packetID) {
        this.packetID = packetID;
    }

    public AudioPacket(int id, long timestamp, byte[] audio) {
        this.packetID = id;
        this.timestamp = timestamp;
        this.audio = audio;
        bf.putInt(id);
        bf.putLong(timestamp);
        bf.put(audio);
    }

    public AudioPacket(ByteBuffer b) {

        //set all variables from existing packet          
    }

    public byte[] getAudio() {
        return this.audio;
    }

    public void decoder(ByteBuffer bf) {
        bf.rewind();
        this.packetID = bf.getInt();
        this.setTimestamp(bf.getLong());
        byte[] buf = new byte[512];
        bf.get(buf, 0, 512);
        this.audio = (byte[]) buf.clone();
    }

    public AudioPacket() {

    }
    
//    public byte[] getData(){
//        this.
//    }

    public int getPacketID() {
        return packetID;
    }

    public void setPacketID() {
        this.packetID = packetID;
    }

    public void sort(ArrayList<DatagramPacket> list) {
        //Collections.sort(list);
    }

//    public int compare(Object obj1, Object obj2
//    ) {
//        Integer currentID = ((AudioPacket) obj1).getPacketID();
//        Integer otherID = ((AudioPacket) obj2).getPacketID();
//
//        if (packetNumber > otherID) {
//            return 1;
//        } else if (packetNumber < otherID) {
//            return -1;
//        } else {
//            return 0;
//        }
//    }
//}
    @Override
    public int compareTo(AudioPacket t) {
        return ((Comparable) this.getPacketID()).compareTo(t.getPacketID());
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
