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

/**
 *
 * @author evgs
 */
public class NonSASLAuth implements JabberBlockListener{
    
    private LoginListener listener;
    
    /** Creates a new instance of NonSASLAuth */
    public NonSASLAuth(Account account, String sessionId, LoginListener listener, JabberStream stream) {
        this.listener=listener;
        stream.addBlockListener(this);
        
        Iq auth= new Iq(account.getServer(), Iq.TYPE_SET, "auth-s" );
        
        JabberDataBlock queryBlock = auth.addChild("query", null );
        queryBlock.setNameSpace( "jabber:iq:auth" );
        
        queryBlock.addChild( "username", account.getUserName() );
        
        if (account.getPlainAuth()) {
            // plain text
            queryBlock.addChild("password", account.getPassword() );
        } else {
            //digest
            SHA1 sha=new SHA1();
                sha.init();
                sha.updateASCII(sessionId);
                sha.updateASCII(account.getPassword());
                sha.finish();
            queryBlock.addChild("digest", sha.getDigestHex() );
        }
        
        queryBlock.addChild( "resource", account.getResource() );
        
        listener.loginMessage(SR.MS_AUTH);
        
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
            }
            
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;        
    }
    
}
