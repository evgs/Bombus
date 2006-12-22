/*
 * NonSASLAuth.java
 *
 * Created on 8 Июль 2006 г., 22:16
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
import com.ssttr.crypto.SHA1;
import locale.SR;
import util.strconv;

/**
 *
 * @author evgs
 */
public class NonSASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    
    private Account account;

    private JabberStream stream;

    private String sessionId;
    
    /** Creates a new instance of NonSASLAuth */
    public NonSASLAuth(Account account, String sessionId, LoginListener listener, JabberStream stream) {
        this.listener=listener;
        this.account=account;
        this.sessionId=sessionId;
        this.stream=stream;
        
        stream.addBlockListener(this);
        
        jabberIqAuth(AUTH_GET);
       
        listener.loginMessage(SR.MS_AUTH);
    }

    private final static int AUTH_GET=0;
    private final static int AUTH_PASSWORD=1;
    private final static int AUTH_DIGEST=2;
    
    private void jabberIqAuth(int authType) {
        int type=Iq.TYPE_GET;
        String id="auth-1";
        
        JabberDataBlock query = new JabberDataBlock("query", null, null);
        query.setNameSpace( "jabber:iq:auth" );
        query.addChild( "username", account.getUserName() );
        
        switch (authType) {
            case AUTH_DIGEST:
                SHA1 sha=new SHA1();
                sha.init();
                sha.updateASCII(sessionId);
                sha.updateASCII(strconv.unicodeToUTF(account.getPassword()) );
                sha.finish();
                query.addChild("digest", sha.getDigestHex() );

                query.addChild( "resource", account.getResource() );
                type=Iq.TYPE_SET;
                id="auth-s";
                break;
                
            case AUTH_PASSWORD:
                query.addChild("password", account.getPassword() );
                query.addChild( "resource", account.getResource() );
                type=Iq.TYPE_SET;
                id="auth-s";
                break;
        }

        
        Iq auth=new Iq(account.getServer(), type, id);
        auth.addChild(query);
        
        stream.send(auth);
    }
    public int blockArrived(JabberDataBlock data) {
        try {
            if( data instanceof Iq ) {
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                if ( id.equals("auth-s") ) {
                    if (type.equals( "error" )) {
                        // Authorization error
                        listener.loginFailed( data.getChildBlock("error").toString() );
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    } else if (type.equals( "result")) {
                        listener.loginSuccess();
                        return JabberBlockListener.NO_MORE_BLOCKS;
                    }
                }
                if (id.equals("auth-1")) {
                    try {
                        JabberDataBlock query=data.getChildBlock("query");
                        
                        if (query.getChildBlock("digest")!=null) {
                            jabberIqAuth(AUTH_DIGEST);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        } 
                        
                        if (query.getChildBlock("password")!=null) {
                            if (!account.getPlainAuth()) {
                                listener.loginFailed("Plain auth required");
                                return JabberBlockListener.NO_MORE_BLOCKS;
                            }
                            jabberIqAuth(AUTH_PASSWORD);
                            return JabberBlockListener.BLOCK_PROCESSED;
                        } 
                        
                        listener.loginFailed("Unknown mechanism");
                        
                    } catch (Exception e) { listener.loginFailed(e.toString()); }
                    return JabberBlockListener.NO_MORE_BLOCKS;
                }
            }
            
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;        
    }
    
}
