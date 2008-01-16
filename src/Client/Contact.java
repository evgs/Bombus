/*
 * Contact.java
 *
 * Created on 6.01.2005, 19:16
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
 */

package Client;
import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import ui.Colors;
import vcard.VCard;
import java.util.*;
import ui.IconTextElement;
import ui.ImageList;
import com.alsutton.jabber.datablocks.Presence;

/**
 * Contact
 * @author Eugene Stahov
 */
public class Contact extends IconTextElement{
    
    public final static byte ORIGIN_ROSTER=0;
    public final static byte ORIGIN_ROSTERRES=1;
    public final static byte ORIGIN_CLONE=2;
    public final static byte ORIGIN_PRESENCE=3;
    public final static byte ORIGIN_GROUPCHAT=4;
    public final static byte ORIGIN_GC_MEMBER=5;
    public final static byte ORIGIN_GC_MYSELF=6;
    

    /*public final static String XEP184_NS="http://www.xmpp.org/extensions/xep-0184.html#ns";
    public final static int DELIVERY_NONE=0;
    public final static int DELIVERY_HANDSHAKE=1;
    public final static int DELIVERY_XEP184=2;
    public final static int DELIVERY_XEP22=3;*/

   
    /** Creates a new instance of Contact */
    protected Contact (){
        //lastReaded=0;
        super(RosterIcons.getInstance());
        msgs=new Vector();
        key1="";
    }

    public String nick;
    public Jid jid;
    public String bareJid;    // for roster/subscription manipulating
    protected int status;
    public int priority;
    private Group group;
    public int transport;
    
    public boolean acceptComposing;
    public Integer incomingComposing;
    public int deliveryType;
    
    public String msgSuspended;
    
    //public int key1;
    protected int key0;
    protected String key1;

    public byte origin;
    //public boolean gcMyself;
    
    public String subscr;
    public int offline_type=Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;
    
    public Vector msgs;
    private int newMsgCnt=-1;
    public int unreadType;
    public int lastUnread;
    
    public VCard vcard;
    
    //public long conferenceJoinTime;
    
    public int firstUnread(){
        int unreadIndex=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread) break;
            unreadIndex++;
        }
        return unreadIndex;
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick; jid= new Jid(sJid); status=Status;
        bareJid=sJid;
        this.subscr=subscr;
    
        setSortKey((Nick==null)?sJid:Nick);
        //msgs.removeAllElements();
        
        //calculating transport
        transport=RosterIcons.getInstance().getTransportIndex(jid.getTransport());
    }
    
    public Contact clone(Jid newjid, final int status) {
        Contact clone=new Contact();
        clone.group=group; 
        clone.jid=newjid; 
        clone.nick=nick;
        clone.key1=key1;
        clone.subscr=subscr;
        clone.offline_type=offline_type;
        clone.origin=ORIGIN_CLONE; 
        clone.status=status; 
        clone.transport=RosterIcons.getInstance().getTransportIndex(newjid.getTransport()); //<<<<

        clone.bareJid=bareJid;
        return clone;
    }
    
    public int getImageIndex() {
        if (getNewMsgsCount()>0) 
            switch (unreadType) {
                case Msg.MESSAGE_TYPE_AUTH: return RosterIcons.ICON_AUTHRQ_INDEX;
                default: return RosterIcons.ICON_MESSAGE_INDEX;
            }
        if (incomingComposing!=null) return RosterIcons.ICON_COMPOSING_INDEX;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport; 
        return st;
    }
    public int getNewMsgsCount() {
        if (getGroupType()==Groups.TYPE_IGNORE) return 0;
        //return msgs.size()-lastReaded;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        unreadType=Msg.MESSAGE_TYPE_IN;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread) { 
                nm++;
                if (m.messageType==Msg.MESSAGE_TYPE_AUTH) unreadType=m.messageType;
            }
        }
        return newMsgCnt=nm;
    }
    
    //public boolean needsCount(){ return (newMsgCnt<0);  }
    
    public boolean active(){
	if (msgs.size()>1) return true;
	if (msgs.size()==0) return false;
	return (((Msg)msgs.elementAt(0)).messageType!=Msg.MESSAGE_TYPE_PRESENCE);
    }
    
    public void resetNewMsgCnt() { newMsgCnt=-1;}
    
    public void setComposing (boolean state) {
        incomingComposing=(state)? new Integer(RosterIcons.ICON_COMPOSING_INDEX):null;
        //System.out.println("Composing:"+state);
    }
    
    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        //1. status
        int cmp;
        //if (origin>=ORIGIN_GROUPCHAT && c.origin>=ORIGIN_GROUPCHAT) {
        //    if ((cmp=origin-c.origin) !=0) return cmp;
        //} else {
        //    if ((cmp=status-c.status) !=0) return cmp;
        //}
        if ((cmp=key0-c.key0) !=0) return cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=key1.compareTo(c.key1)) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return c.transport-transport;
        //return 0;
    };
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (m.isPresence()) 
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                    if (origin!=ORIGIN_GROUPCHAT) first_replace=true;
//#if USE_SIEMENS_FILES
//#         Config cf=Config.getInstance();
//# 
//#         if (cf.msgLog && group.index!=Groups.TYPE_TRANSP && group.index!=Groups.TYPE_SEARCH_RESULT)
//#         {
//#             String histRecord=(nick==null)?getBareJid():nick;
//#             String fromName=StaticData.getInstance().account.getUserName();
//#             if (m.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=toString();
//#             boolean allowLog=false;
//#             switch (m.messageType) {
//#                 case Msg.MESSAGE_TYPE_PRESENCE:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConfPresence) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT && cf.msgLogPresence) allowLog=true;
//#                     break;
//#                 default:
//#                     if (origin>=ORIGIN_GROUPCHAT && cf.msgLogConf) allowLog=true;
//#                     if (origin<ORIGIN_GROUPCHAT) allowLog=true;
//#             }
//#             if (allowLog)
//#                 //if (!first_replace || !m.)
//#             {
//#                 StringBuffer body=new StringBuffer(m.getDayTime());
//#                 body.append(" <");
//#                 body.append(fromName);
//#                 body.append("> ");
//#                 if (m.subject!=null) {
//#                     body.append(m.subject);
//#                     body.append("\r\n");
//#                 }
//#                 body.append(m.getBody());
//#                 body.append("\r\n");
//#                 io.NvStorage.appendFile("Log_"+histRecord, body.toString());
//#             }
//#         }
//#endif
        if (first_replace) {
            msgs.setElementAt(m,0);
            return;
        } 
        msgs.addElement(m);
        if (m.unread) {
            lastUnread=msgs.size()-1;
            if (m.messageType>unreadType) unreadType=m.messageType;
            if (newMsgCnt>=0) newMsgCnt++;
        }
    }
    
  
    public int getColor() { 
        switch (status) {
            case Presence.PRESENCE_CHAT: return Colors.CONTACT_CHAT;
            case Presence.PRESENCE_AWAY: return Colors.CONTACT_AWAY;
            case Presence.PRESENCE_XA: return Colors.CONTACT_XA;
            case Presence.PRESENCE_DND: return Colors.CONTACT_DND;
        }
        return Colors.CONTACT_DEFAULT;
    };

    public int getFontIndex(){
        return (status<5)?1:0;
    }
    
    public String toString() { 
        if (origin>ORIGIN_GROUPCHAT) return nick;
        if (origin==ORIGIN_GROUPCHAT) return getJid();
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
    
    public final String getName(){ return (nick==null)?getBareJid():nick; }
    //public void onSelect(){}

    public final String getJid() {
        return jid.getJid();
    }

    public final String getBareJid() {
        return bareJid;
    }

    public String getNickJid() {
        if (nick==null) return bareJid;
        return nick+" <"+bareJid+">";
    }
    
    /**
     * Splits string like "name@jabber.ru/resource" to vector 
     * containing 2 substrings
     * @return Vector.elementAt(0)="name@jabber.ru"
     * Vector.elementAt(1)="resource"
     */
    /*
     public static final Vector SplitJid(final String jid) {
        Vector result=new Vector();
        int i=jid.lastIndexOf('/');
        if (i==-1){
            result.addElement(jid);
            result.addElement(null);
        } else {
            result.addElement(jid.substring(0,i));
            result.addElement(jid.substring(i+1));
        }
        return result;
    }
     */
    public final void purge() {
        msgs=new Vector();
        vcard=null;
        resetNewMsgCnt();
    }
    
    public final void setSortKey(String sortKey){
        key1=(sortKey==null)? "": sortKey.toLowerCase();
    }

    public String getTipString() {
        int nm=getNewMsgsCount();
        if (nm!=0) return String.valueOf(nm);
        if (nick!=null) return bareJid;
        return null;
    }

    public Group getGroup() { return group; }
    public int getGroupType() {  
        if (group==null) return 0; 
        return group.type;  
    }
    public boolean inGroup(Group ingroup) {  return group==ingroup;  }

    /*public void setGroupIndex(int groupIndex) {
        this.group = groupIndex;
    }*/
    public void setGroup(Group group) { this.group = group; }

    public void setStatus(int status) {
        setComposing(false);
        this.status = status;
        if (status>=Presence.PRESENCE_OFFLINE) acceptComposing=false;
    }

    public int getStatus() {
        return status;
    }

    void markDelivered(String id) {
        if (id==null) return;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            Msg m=(Msg)e.nextElement();
            if (m.id!=null)
                if (m.id.equals(id)) m.delivered=true;
        }
    }
}
