/*
 * EntityCaps.java
 *
 * Created on 17 Июнь 2007 г., 2:58
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package Client;

import Info.Version;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.ssttr.crypto.SHA1;
import java.util.Vector;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() { initCaps(); }

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;
        
        JabberDataBlock query=data.findNamespace("query", "http://jabber.org/protocol/disco#info");
        //System.out.println("caps"+data);
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");
        
        /*
         if (!query.isJabberNameSpace("http://jabber.org/protocol/disco#info")) 
            return BLOCK_REJECTED;
         */
        
        if (node!=null) {
            if (!node.equals(BOMBUS_NAMESPACE+"#"+calcVerHash()) )    return BLOCK_REJECTED;
            //TODO: should be another error returned, not cancel:feature not implemented.
            //(modify?)
        }
        
        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);
        
        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category", BOMBUS_ID_CATEGORY);
        identity.setAttribute("type", BOMBUS_ID_TYPE);
        identity.setAttribute("name", Version.getNameVersion());

        for (int i=0; i<features.size(); i++) {
            query.addChild("feature", null).setAttribute("var", (String) features.elementAt(i));
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }

   
    public static String ver=null;
    private static Vector features;
    
    public static String calcVerHash() {
        if (ver!=null) return ver;
        
        SHA1 sha1=new SHA1();
        sha1.init();
        
        //indentity
        sha1.update(BOMBUS_ID_CATEGORY+"/"+BOMBUS_ID_TYPE+"//");
        sha1.update(Version.getNameVersion());
        sha1.update("<");
        
        for (int i=0; i<features.size(); i++) {
            sha1.update((String) features.elementAt(i));
            sha1.update("<");
        }
        
        sha1.finish();
        ver=sha1.getDigestBase64();
        
        return ver;
    }
    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE); //+'#'+Version.getVersionNumber());
        c.setAttribute("ver", calcVerHash());
        c.setAttribute("hash", "sha-1"); 
        
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE="http://bombus-im.org/java";
    private final static String BOMBUS_ID_CATEGORY="client";
    private final static String BOMBUS_ID_TYPE="mobile";

    public static void initCaps() {
        ver=null;
        features=new Vector();
        
        //features MUST be sorted        
        if (Config.getInstance().eventComposing)
            features.addElement("http://jabber.org/protocol/chatstates"); //xep-0085
        
        features.addElement("http://jabber.org/protocol/disco#info");
//#ifdef FILE_TRANSFER        
        features.addElement("http://jabber.org/protocol/ibb");
//#endif        

//#ifdef PEP
        if (Config.getInstance().enablePep) {
            features.addElement("http://jabber.org/protocol/mood");
            features.addElement("http://jabber.org/protocol/mood+notify");
        }
//#endif        

        features.addElement("http://jabber.org/protocol/muc");

//#ifdef FILE_TRANSFER        
        features.addElement("http://jabber.org/protocol/si");
        features.addElement("http://jabber.org/protocol/si/profile/file-transfer");
//#endif        

//#ifdef PEP
        if (Config.getInstance().enablePep) {
            features.addElement("http://jabber.org/protocol/tune");
            features.addElement("http://jabber.org/protocol/tune+notify");
        }
//#endif        
        
        features.addElement("jabber:iq:time"); //DEPRECATED
        features.addElement("jabber:iq:version");
        features.addElement("jabber:x:data");
        //"jabber:x:event", //DEPRECATED
        features.addElement("urn:xmpp:ping");
        
        if (Config.getInstance().eventDelivery)
            features.addElement("urn:xmpp:receipts"); //xep-0184
        
        features.addElement("urn:xmpp:time");
    }

}
