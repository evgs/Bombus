/*
 * SASLAuth.java
 *
 * Created on 8 Июль 2006 г., 23:34
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package login;

import Client.Account;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.MD5;
import com.sun.midp.ssl.MessageDigest;
import java.io.IOException;
//#if SASL_XGOOGLETOKEN
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#endif

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
            if (mech!=null) {
                // first stream - step 1. selecting authentication mechanism
                //common body
                JabberDataBlock auth=new JabberDataBlock("auth", null,null);
                auth.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
                
                // DIGEST-MD5 mechanism
                if (mech.getChildBlockByText("DIGEST-MD5")!=null) {
                    auth.setAttribute("mechanism", "DIGEST-MD5");
                    
                    System.out.println(auth.toString());
                    
                    stream.send(auth);
                    return JabberBlockListener.BLOCK_PROCESSED;
                }
                
//#if SASL_XGOOGLETOKEN
                // X-GOOGLE-TOKEN mechanism
                if (mech.getChildBlockByText("X-GOOGLE-TOKEN")!=null) {
                    auth.setAttribute("mechanism", "X-GOOGLE-TOKEN");
                    String token=responseXGoogleToken(account.getUserName(), account.getServer(), account.getPassword());
                    auth.setText(token);
                    
                    System.out.println(auth.toString());
                    
                    stream.send(auth);
                    return JabberBlockListener.BLOCK_PROCESSED;
                    
                }
//#endif
                // no more method found
                listener.loginFailed("SASL: Unknown mechanisms");
                return JabberBlockListener.NO_MORE_BLOCKS;
                
            } 
            // second stream - step 1. binding resource
            else if (data.getChildBlock("bind")!=null) {
                JabberDataBlock bindIq=new Iq(null, Iq.TYPE_SET, "bind");
                JabberDataBlock bind=bindIq.addChild("bind",null);
                bind.setNameSpace("urn:ietf:params:xml:ns:xmpp-bind");
                bind.addChild("resource", account.getResource());
                stream.send(bindIq);
                return JabberBlockListener.BLOCK_PROCESSED;
            }
        } else if (data.getTagName().equals("challenge")) {
            // first stream - step 2,3. reaction to challenges
            
            String challenge=decodeBase64(data.getText());
            System.out.println(challenge);

            JabberDataBlock resp=new JabberDataBlock("response", null, null);
            resp.setNameSpace("urn:ietf:params:xml:ns:xmpp-sasl");
            
            int nonceIndex=challenge.indexOf("nonce=");
                // first stream - step 2. generating DIGEST-MD5 response due to challenge
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
                // first stream - step 3. sending second empty response due to second challenge
            //if (challenge.startsWith("rspauth")) {}
                
            stream.send(resp);
            return JabberBlockListener.BLOCK_PROCESSED;
            
        } else if ( data.getTagName().equals("failure")) {
            // first stream - step 4a. not authorized
            listener.loginFailed( data.getText() );  
        } else if ( data.getTagName().equals("success")) {
            // first stream - step 4b. success.
            try {
                stream.initiateStream(account.getServer(), true);
            } catch (IOException ex) { }
            return JabberBlockListener.NO_MORE_BLOCKS; // at first stream
        }

        if (data instanceof Iq) {
            if (data.getTypeAttribute().equals("result")) {
                // second stream - step 2. resource binded - opening session
                if (data.getAttribute("id").equals("bind")) {
                    //TODO: get assigned resource from result
                    JabberDataBlock session=new Iq(null, Iq.TYPE_SET, "sess");
                    session.addChild("session",null).setNameSpace("urn:ietf:params:xml:ns:xmpp-session");
                    stream.send(session);
                    return JabberBlockListener.BLOCK_PROCESSED;
                    
                // second stream - step 3. session opened - reporting success login
                } else if (data.getAttribute("id").equals("sess")) {
                    listener.loginSuccess();
                    return JabberBlockListener.NO_MORE_BLOCKS;
                    //return JabberBlockListener.BLOCK_PROCESSED;
                }
            }
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
        String resp = toBase64(out);
        System.out.println(decodeBase64(resp));
        
        return resp;
    }
    
    
//#if SASL_XGOOGLETOKEN
    private String readLine(InputStream is) {
        StringBuffer buf = new StringBuffer();
        try {
            while(true) {
                int ch = is.read();
                if (ch==-1 || ch == '\n') break;
                buf.append((char)ch);
            }
        } catch (Exception e) {}
        return buf.toString();
    }
    
    /**
     * Generates X-GOOGLE-TOKEN response by communication with http://www.google.com
     * (algorithm from MGTalk/NetworkThread.java)
     * @param userName
     * @param passwd
     * @return
     */
    private String responseXGoogleToken(String userName, String server, String passwd) {
        try {
            String firstUrl = "https://www.google.com:443/accounts/ClientAuth?Email="
                    + userName + "%40"+ server
                    + "&Passwd=" + passwd //TODO: escaping password
                    + "&PersistentCookie=false&source=googletalk";
            
            //log.addMessage("Connecting to www.google.com");
            HttpConnection c = (HttpConnection) Connector.open(firstUrl.toString());
            InputStream is = c.openInputStream();
            
            
            String sid = readLine(is);
            if(!sid.startsWith("SID=")) {
                listener.loginFailed(sid);
                return null;
            }
            
            String lsid = readLine(is);
            
            String secondUrl = "https://www.google.com:443/accounts/IssueAuthToken?"
                    + sid + "&" + lsid + "&service=mail&Session=true";
            is.close();
            c.close();
            //log.addMessage("Next www.google.com connection");
            c = (HttpConnection) Connector.open(secondUrl);
            is = c.openInputStream();
            //str = readLine(dis);
            String token = "\0"+userName+"\0"+readLine(is);
            is.close();
            c.close();
            return toBase64(token);
            
        } catch(Exception e) {
            listener.loginFailed("Google token error");
            e.printStackTrace();
        }
        return null;
    }
//#endif
    
    public final static String toBase64( String source) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        int len=source.length();
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<source.length(); i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source.charAt(i))<<8;
            if ((i+1) < len) {
                val |= (0xFF & source.charAt(i+1));
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source.charAt(i+2));
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
