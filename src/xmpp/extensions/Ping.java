/*
 * Ping.java
 *
 * Created on 11.05.2008, 19:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xmpp.extensions;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;

public class Ping implements JabberBlockListener{
    
    /** Creates a new instance of Ping */
    public Ping() {}

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        
        String from=data.getAttribute("from");
        String id=data.getAttribute("id");
        String type=data.getTypeAttribute();

        if (type.equals("result") || type.equals("error")) {
            if (!id.equals("ping")) return BLOCK_REJECTED;
            StaticData.getInstance().roster.theStream.pingSent=false; 
            return BLOCK_PROCESSED;
        }    
        
        if (type.equals("get")){
            if (data.findNamespace("ping", "urn:xmpp:ping")==null) return BLOCK_REJECTED;
            // xep-0199 ping
            Iq pong=new Iq(from, Iq.TYPE_RESULT, data.getAttribute("id"));
            StaticData.getInstance().roster.theStream.send(pong);
            return BLOCK_PROCESSED;
        }
        
        return BLOCK_REJECTED;
    }
}
