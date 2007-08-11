/*
 * EntityCaps.java
 *
 * Created on 17 Июнь 2007 г., 2:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Info.Version;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;

/**
 *
 * @author Evg_S
 */
public class EntityCaps implements JabberBlockListener{
    
    /** Creates a new instance of EntityCaps */
    public EntityCaps() {}

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getTypeAttribute().equals("get")) return BLOCK_REJECTED;
        
        JabberDataBlock query=data.getChildBlock("query");
        if (query==null) return BLOCK_REJECTED;
        String node=query.getAttribute("node");
        if (!query.isJabberNameSpace("http://jabber.org/protocol/disco#info")) 
            return BLOCK_REJECTED;
        if (node!=null) 
            if (!node.startsWith(BOMBUS_NAMESPACE))
                return BLOCK_REJECTED;
        
        JabberDataBlock result=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        result.addChild(query);
        
        JabberDataBlock identity=query.addChild("identity", null);
        identity.setAttribute("category","client");
        identity.setAttribute("type","mobile");
        identity.setAttribute("name", "Bombus");

        for (int i=0; i<features.length; i++) {
            query.addChild("feature", null).setAttribute("var",features[i]);
        }
        
        StaticData.getInstance().roster.theStream.send(result);
        
        return BLOCK_PROCESSED;
    }

    public static JabberDataBlock presenceEntityCaps() {
        JabberDataBlock c=new JabberDataBlock("c", null, null);
        c.setAttribute("xmlns", "http://jabber.org/protocol/caps");
        c.setAttribute("node", BOMBUS_NAMESPACE);
        c.setAttribute("ver", Version.getVersionNumber());
        //c.setAttribute("ext", appVersion.c_str());
        
        return c;
    }
    
    private final static String BOMBUS_NAMESPACE="http://bombus-im.org/java";
    private final static String features[]={
        "jabber:iq:version",
        "jabber:x:data",
        "jabber:x:event",
        "http://jabber.org/protocol/disco#info",
        "http://jabber.org/protocol/muc",
	"http://jabber.org/protocol/si",
	"http://jabber.org/protocol/si/profile/file-transfer",
	"http://jabber.org/protocol/ibb"
};

}
