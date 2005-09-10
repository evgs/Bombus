/*
 * PrivacyItem.java
 *
 * Created on 10 Сентябрь 2005 г., 21:30
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package PrivacyLists;
import ui.*;
import com.alsutton.jabber.*;
import Client.*;

/**
 *
 * @author EvgS
 */
public class PrivacyItem extends IconTextElement{
    
    public final static int ITEM_JID=0;
    public final static int ITEM_GROUP=1;
    public final static int ITEM_SUBSCR=2;
    private final static String types[]={"jid", "group", "subscription"};
    
    private int type;    //jid|group|subscription
    private String value;
    private boolean action_allow;
    private int order;
    
    boolean message;
    boolean presenceIn;
    boolean presenceOut;
    boolean iq;
    
    public int getImageIndex(){
        return (action_allow)? 
            ImageList.ICON_PRIVACY_ALLOW:
            ImageList.ICON_PRIVACY_BLOCK;
    }
    
    public int getColor() { return 0; }
    public String toString() { return value; }
    
    /** Creates a new instance of PrivacyItem */
    public PrivacyItem() {
        super(StaticData.getInstance().rosterIcons);
    }
    
    public PrivacyItem(JabberDataBlock item) {
        super(StaticData.getInstance().rosterIcons);
        String t=item.getTypeAttribute();
        for (type=0; type<2; type++) if (t.equals(types[type])) break;
        value=item.getAttribute("value");
        action_allow=item.getAttribute("action").equals("allow");
        order=Integer.parseInt(item.getAttribute("order"));
        message=item.getChildBlock("message")!=null;
        presenceIn=item.getChildBlock("presence-in")!=null;
        presenceOut=item.getChildBlock("presence-out")!=null;
        iq=item.getChildBlock("iq")!=null;
    }
    
    public static PrivacyItem itemIgnoreList(){
        PrivacyItem item=new PrivacyItem();
        item.type=ITEM_GROUP;
        item.value=Roster.IGNORE_GROUP;
        item.iq=true;
        item.presenceOut=true;
        return item;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        item.setTypeAttribute(types[type]);
        item.setAttribute("value", value);
        item.setAttribute("action", (action_allow)? "allow":"deny" );
        item.setAttribute("order", String.valueOf(order));
        if (message) item.addChild("message", null);
        if (presenceIn) item.addChild("presence-in", null);
        if (presenceOut) item.addChild("presence-out", null);
        if (iq) item.addChild("iq", null);
        return item;
    }
}
