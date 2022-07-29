package voip2.DataSocket2;

import voip2.*;

/*
 * TextDuplex.java
 *
 * Created on 15 January 2003, 17:11
 */

/**
 *
 * @author  abj
 */
public class VoiceDuplex {
    
    public static void main (String[] args){
        
        VoiceReceiverThread receiver = new VoiceReceiverThread();
        VoiceSenderThread sender = new VoiceSenderThread();
        
        receiver.start();
        sender.start();
        
    }
    
}
