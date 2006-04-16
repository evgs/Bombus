/*
 * PrivacyItem.java
 *
 * Created on 10 Сентябрь 2005 г., 21:30
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package PrivacyLists;
import images.RosterIcons;
import ui.*;
import com.alsutton.jabber.*;
import Client.*;

/**
 *
 * @author EvgS
 */
public class PrivacyItem extends IconTextElement{
    
    public final static String types[]={"jid", "group", "subscription", "ANY"};
    public final static int ITEM_JID=0;
    public final static int ITEM_GROUP=1;
    public final static int ITEM_SUBSCR=2;
    public final static int ITEM_ANY=3;

    public final static String actions[]={"allow", "deny"};
    public final static int ITEM_ALLOW=0;
    public final static int ITEM_BLOCK=1;

    public final static String stanzas[]={"message", "presence-in", "presence-out", "iq"};
    public final static int STANZA_MSG=0;
    public final static int STANZA_PRESENCE_IN=1;
    public final static int STANZA_PRESENCE_OUT=2;
    public final static int STANZA_IQ=3;
    
    public final static String subscrs[]={"none", "from", "to", "both"};
    
    int type;    //jid|group|subscription|ANY
    String value=new String();
    int action=1;
    int order;
    boolean stanzasSet[]=new boolean[4];
    
    public int getImageIndex(){
        return action+ RosterIcons.ICON_PRIVACY_ALLOW;
    }
    
    public int getColor() { return Colors.LIST_INK; }
    
    public String toString() { return (type==ITEM_ANY)?"ANY":value; }
    
    /** Creates a new instance of PrivacyItem */
    public PrivacyItem() {
        super(RosterIcons.getInstance());
    }
    
    public PrivacyItem(JabberDataBlock item) {
        this();
        String t=item.getTypeAttribute();
        if (t==null) type=ITEM_ANY;
        else 
            for (type=0; type<2; type++) 
                if (t.equals(types[type])) break;
        value=item.getAttribute("value");
        action=item.getAttribute("action").equals("allow")?0:1;
        order=Integer.parseInt(item.getAttribute("order"));
        int index;
        for (index=0; index<4; index++) {
            if (item.getChildBlock(stanzas[index])!=null) stanzasSet[index]=true;
        }
    }
    
    public static PrivacyItem itemIgnoreList(){
        PrivacyItem item=new PrivacyItem();
        item.type=ITEM_GROUP;
        item.value=Groups.IGNORE_GROUP;
        item.stanzasSet[STANZA_IQ]=true;
        item.stanzasSet[STANZA_PRESENCE_OUT]=true;
        return item;
    }
    
    public JabberDataBlock constructBlock() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        if (type!=ITEM_ANY) {
            item.setTypeAttribute(types[type]);
            item.setAttribute("value", value);
        }
        item.setAttribute("action", actions[action] );
        item.setAttribute("order", String.valueOf(order));
        int index;
        for (index=0; index<4; index++) {
            if (stanzasSet[index]) item.addChild(stanzas[index], null);
        }
        return item;
    }
}
