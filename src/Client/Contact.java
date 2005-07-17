/*
 * Contact.java
 *
 * Created on 6 Январь 2005 г., 19:16
 */

package Client;
import java.util.*;
import ui.IconTextElement;
import ui.ImageList;
import com.alsutton.jabber.datablocks.Presence;

/**
 * Контакт
 * @author Eugene Stahov
 */
public class Contact extends IconTextElement{
    
    private static int COLORS[]={
        0x000000,   // online
        0x39358b,   // chat
        0x008080,   // away
        //0x808080,   // xa        //0x39358b,   // xa
        0x535353,   // xa

        0x800000,   // dnd
        0x00,
        0x00,
        0x00
    };
    
    /** Creates a new instance of Contact */
    private Contact (){
        //lastReaded=0;
        super(StaticData.getInstance().rosterIcons);
        msgs=new Vector();
    }

    public String nick;
    public Jid jid;
    public String rosterJid;    // for roster/subscription manipulating
    public int status;
    public int priority;
    public int group;
    public int transport;
    
    public boolean accept_composing;
    public Integer incomingComposing;
    
    public String msgSuspended;
    
    private int jidHash;

    public int origin;  //0 - from roster, 1 - from roster, appended resource, 2 - from presence
    
    public String subscr;
    public int offline_type=Presence.PRESENCE_UNKNOWN;
    public boolean ask_subscribe;
    
    public Vector msgs;
    private int newMsgCnt=-1;
    public int unreadType;
    public int lastUnread;
    
    public int firstUnread(){
        int c=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread) break;
            c++;
        }
        return c;
    }

    public Contact(final String Nick, final String sJid, final int Status, String subscr) {
        this();
        nick=Nick; jid= new Jid(sJid); status=Status;
        rosterJid=sJid;
        this.subscr=subscr;
    
        jidHash=sortCode((Nick==null)?sJid:Nick);
        //msgs.removeAllElements();
        
        //calculating transport
        transport=Transport.getInstance().getTransportIndex(jid.getTransport());
    }
    
    public Contact clone(Jid newjid, final int status) {
        Contact c=new Contact();
        c.group=group; 
        c.jid=newjid; 
        c.nick=nick;
        c.jidHash=jidHash;
        c.subscr=subscr;
        c.offline_type=offline_type;
        c.origin=2; 
        c.status=status; 
        c.transport=transport;
        c.rosterJid=rosterJid;
        return c;
    }
    
    public int getImageIndex() {
        if (getNewMsgsCount()>0) 
            return 
                ImageList.ICON_MESSAGE_INDEX + unreadType - Msg.MESSAGE_TYPE_IN;
        int st=(status==Presence.PRESENCE_OFFLINE)?offline_type:status;
        if (st<8) st+=transport<<4; 
        return st;
    }
    public int getNewMsgsCount() {
        if (group==Roster.IGNORE_INDEX) return 0;
        //return msgs.size()-lastReaded;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        unreadType=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            Msg m=(Msg)e.nextElement();
            if (m.unread) nm++;
            if (m.messageType>unreadType) unreadType=m.messageType;
        }
        return newMsgCnt=nm;
    }
    
    public boolean needsCount(){ return (newMsgCnt<0);  }
    
    public void resetNewMsgCnt() { newMsgCnt=-1;}
    
    public void setComposing (boolean state) {
        incomingComposing=(state)? new Integer(ImageList.ICON_COMPOSING_INDEX):null;
        //System.out.println("Composing:"+state);
    }
    
    public int compare(Contact c){
        //1. status
        int cmp;
        if ((cmp=status-c.status) !=0) return cmp;
        if ((cmp=jidHash-c.jidHash) !=0) return cmp;
        if ((cmp=c.priority-priority) !=0) return cmp;
        return 0;
    };
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (m.isPresence()) 
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                    first_replace=true;
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--        Config cf=StaticData.getInstance().config;
//--
//--        if (cf.msgLog)
//--        {
//--            String histRecord=(nick==null)?getJidNR():nick;
//--            String fromName=StaticData.getInstance().account.getUserName();
//--            if (m.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=toString();
//--            if (m.messageType!=Msg.MESSAGE_TYPE_PRESENCE || cf.msgLogPresence)
//--                //if (!first_replace || !m.)
//--            {
//--                StringBuffer body=new StringBuffer(m.getDayTime());
//--                body.append(" <");
//--                body.append(fromName);
//--                body.append("> ");
//--                if (m.subject!=null) {
//--                    body.append(m.subject);
//--                    body.append("\r\n");
//--                }
//--                body.append(m.body);
//--                body.append("\r\n");
//--                NvStorage.appendFile("Log_"+histRecord, body.toString());
//--            }
//--        }
/*$USE_SIEMENS_FILES$*///</editor-fold>
        // если единственное сообщение - presence, то заменим его
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
    
  
    public int getColor() { return (status>7)?0:COLORS[status]; }
    // public int getColorBGnd() { return 0xffffff; }

    public int getFontIndex(){
        return (status<5)?1:0;
    }
    
    public String toString() { 
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
    //public void onSelect(){}

    public final String getJid() {
        return jid.getJidFull();
    }

    public final String getJidNR() {
        return rosterJid;
    }

    public final String getNickJid() {
        if (nick==null) return rosterJid;
        return nick+" <"+rosterJid+">";
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
    
    private int sortCode(String s){
        try {
            String ls=s.toLowerCase();
            return ls.charAt(1)+ (ls.charAt(0)<<16);
        } catch (Exception e) { return 0; }
    }
}
