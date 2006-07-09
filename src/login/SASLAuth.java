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
import com.sun.midp.ssl.MessageDigest;

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
            
            if (mech.getChildBlockByText("DIGEST-MD5")!=null) {
                JabberDataBlock auth=new JabberDataBlock("auth", null,null);
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                auth.setAttribute("mechanism", "DIGEST-MD5");
                
                System.out.println(auth.toString());
                
                stream.send(auth);
            }
            return JabberBlockListener.BLOCK_PROCESSED;
        } else if (data.getTagName().equals("challenge")) {
            String challenge=decodeBase64(data.getText());
            System.out.println(challenge);

            JabberDataBlock resp=new JabberDataBlock("response", null, null);
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
            
            int nonceIndex=challenge.indexOf("nonce=");
            if (nonceIndex>=0) {
                nonceIndex+=7;
                String nonce=challenge.substring(nonceIndex, challenge.indexOf('\"', nonceIndex));
                String cnonce="123456789abcd";
                
                resp.setText(responseMd5Digest(
                        account.getUserName(),
                        account.getPassword(),
                        account.getServer(),
                        "xmpp/"+account.getServer(),
                        nonce,
                        cnonce ));
                System.out.println(resp.toString());
            }
            //if (challenge.startsWith("rspauth")) {}
                
            stream.send(resp);
            return JabberBlockListener.BLOCK_PROCESSED;
            
        } else if ( data.getTagName().equals("failure")) {
            listener.loginFailed( data.getText() );  
        } else if ( data.getTagName().equals("success")) {
            return JabberBlockListener.BLOCK_PROCESSED;
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
                if (len<2) out.append( (char)((ibuf>>8) &0xff) );
                if (len==0) out.append( (char)(ibuf &0xff) );
                //len+=3;
                ibuf=1;
            }
        }
        return out.toString();
    }

    /**
     * This routine generates MD5-DIGEST response via SASL specification
     * @param user
     * @param pass
     * @param realm
     * @param digest_uri
     * @param nonce
     * @param cnonce
     * @return
     */
    private String responseMd5Digest(String user, String pass, String realm, String digestUri, String nonce, String cnonce) {

        MD5 hUserRealmPass=new MD5();
        hUserRealmPass.init();
        hUserRealmPass.updateASCII(user);
        hUserRealmPass.update((byte)':');
        hUserRealmPass.updateASCII(realm);
        hUserRealmPass.update((byte)':');
        hUserRealmPass.updateASCII(pass);
        hUserRealmPass.finish();
        
        MD5 hA1=new MD5();
        hA1.init();
        hA1.update(hUserRealmPass.getDigestBits());
        hA1.update((byte)':');
        hA1.updateASCII(nonce);
        hA1.update((byte)':');
        hA1.updateASCII(cnonce);
        hA1.finish();
        
        MD5 hA2=new MD5();
        hA2.init();
        hA2.updateASCII("AUTHENTICATE:");
        hA2.updateASCII(digestUri);
        hA2.finish();
        
        MD5 hResp=new MD5();
        hResp.init();
        hResp.updateASCII(hA1.getDigestHex());
        hResp.update((byte)':');
        hResp.updateASCII(nonce);
        hResp.updateASCII(":00000001:");
        hResp.updateASCII(cnonce);
        hResp.updateASCII(":auth:");
        hResp.updateASCII(hA2.getDigestHex());
        hResp.finish();
        
        String out = "username=\""+user+"\",realm=\""+realm+"\"," +
                "nonce=\""+nonce+"\",nc=00000001,cnonce=\""+cnonce+"\"," +
                "qop=auth,digest-uri=\""+digestUri+"\"," +
                "response=\""+hResp.getDigestHex()+"\",charset=utf-8";
        String resp = toBase64(out.getBytes());
        System.out.println(decodeBase64(resp));
        
        return resp;
    }

    public final static String toBase64( byte[] source) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        char[] out = new char[((source.length + 2) / 3) * 4];
        for (int i=0, index=0; i<source.length; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source[i])<<8;
            if ((i+1) < source.length) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < source.length) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }

}
