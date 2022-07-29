/*
 * TextDuplex.java
 *
 * Created on 15 January 2003, 17:11
 */

/**
 *
 * @author  abj
 */
public class TextDuplex {
    
    public static void main (String[] args){
        
        TextReceiverThread receiver = new TextReceiverThread();
        TextSenderThread sender = new TextSenderThread();
        
        receiver.start();
        sender.start();
        
    }
    
}
