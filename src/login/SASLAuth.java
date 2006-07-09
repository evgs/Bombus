/*
 * SASLAuth.java
 *
 * Created on 8 Июль 2006 г., 23:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package login;

import Client.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.ssttr.crypto.MD5;

/**
 *
 * @author evgs
 */
public class SASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    private Account account;
    private JabberStream stream;
    private String sessionId;
    /** Creates a new instance of SASLAuth */
    public SASLAuth(Account account, String sessionId, LoginListener listener, JabberStream stream) {
        this.listener=listener;
        this.account=account;
        this.sessionId=sessionId;
        this.stream=stream;
        stream.addBlockListener(this);
    }

    public int blockArrived(JabberDataBlock data) {
        System.out.println(data.toString());
        if (data.getTagName().equals("stream:features")) {
            JabberDataBlock mech=data.getChildBlock("mechanisms");
            
            //...
            JabberDataBlock auth=new JabberDataBlock("auth", null,null);
            auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
            auth.setAttribute("mechanism", "DIGEST-MD5");
            
            System.out.println(auth.toString());
            
            stream.send(auth);
            
            return JabberBlockListener.BLOCK_PROCESSED;
        }

        if (data.getTagName().equals("challenge")) {
            System.out.println(decodeBase64(data.getText()));
        }
        
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    private String decodeBase64(String src)  {
        int len=0;
        int ibuf=1;
        StringBuffer out=new StringBuffer();
        
        for (int i=0; i<src.length(); i++) {
            int nextChar = src.charAt(i);
            int base64=-1;
            if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
            else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
            else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
            else if (nextChar=='+') base64=62;
            else if (nextChar=='/') base64=63;
            else if (nextChar=='=') {base64=0; len++;} else if (nextChar=='<') break;
            if (base64>=0) ibuf=(ibuf<<6)+base64;
            if (ibuf>=0x01000000){
                out.append( (char)((ibuf>>16) &0xff) );
                if (len==0) out.append( (char)((ibuf>>8) &0xff) );
                if (len<2) out.append( (char)(ibuf &0xff) );
                //len+=3;
                ibuf=1;
            }
        }
        return out.toString();
    }
}
