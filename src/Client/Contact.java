/*
 * Contact.java
 *
 * Created on 6 Январь 2005 г., 19:16
 */

package Client;
import java.util.*;
import ui.IconTextElement;
import ui.ImageList;

/**
 * Контакт
 * @author Eugene Stahov
 */
public class Contact extends IconTextElement{
    
    /** Creates a new instance of Contact */
    private Contact (){
        //lastReaded=0;
        super(StaticData.getInstance().rosterIcons);
        msgs=new Vector();
    }

    public String nick;
    public Jid jid;
    public int status;
    public int group;
    public int transport;
    public Vector msgs;
    private int newMsgCnt=-1;
    
    public int firstUnread(){
        int c=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements();) {
            if (((Msg)e.nextElement()).unread) break;
            c++;
        }
        return c;
    }
    public int origin;  //0 - from roster, 1 - from roster, appended resource, 2 - from presence

    public Contact(final String Nick, final String sJid, final int Status) {
        this();
        nick=Nick; jid= new Jid(sJid); status=Status;
        //msgs.removeAllElements();
        
        //calculating transport
        transport=Transport.getInstance().getTransportIndex(jid.getTransport());
    }
    
    public Contact clone(Jid newjid, final int status) {
        Contact c=new Contact();
        c.group=group; 
        c.jid=newjid; 
        c.nick=nick; 
        c.origin=2; 
        c.status=status; 
        c.transport=transport;
        return c;
    }
    
    public int getImageIndex() {
        if (status>7) return status;    // for errors
        return (getNewMsgsCount()>0)?
            ImageList.ICON_MESSAGE_INDEX
            :status+(transport<<4); 
    }
    public int getNewMsgsCount() {
        if (group==Roster.IGNORE_INDEX) return 0;
        //return msgs.size()-lastReaded;
        if (newMsgCnt>-1) return newMsgCnt;
        int nm=0;
        for (Enumeration e=msgs.elements(); e.hasMoreElements(); ) {
            if (((Msg)e.nextElement()).unread) nm++;
        }
        return newMsgCnt=nm;
    }
    public void resetNewMsgCnt() { newMsgCnt=-1;}
    
    public void addMessage(Msg m) {
        boolean first_replace=false;
        if (m.isPresence()) 
            if (msgs.size()==1) 
                if ( ((Msg)msgs.firstElement()).isPresence())
                    first_replace=true;
/*#M55,M55_Release#*///<editor-fold>
//--        Config cf=StaticData.getInstance().config;
//--
//--        if (cf.msgLog)
//--        {
//--            String histRecord=(nick==null)?getJidNR():nick;
//--            String fromName=StaticData.getInstance().account.getUserName();
//--            if (m.messageType!=Msg.MESSAGE_TYPE_OUT) fromName=toString();
//--            if (m.messageType!=Msg.MESSAGE_TYPE_PRESENCE || cf.msgLogPresence)
//--                //if (!first_replace || !m.)
//--            NvStorage.appendFile("Log_"+histRecord, m.getDayTime()+" <"+fromName+"> "+m.body+"\r\n");
//--        }
/*$M55,M55_Release$*///</editor-fold>
        // если единственное сообщение - presence, то заменим его
        if (first_replace) {
            msgs.setElementAt(m,0);
            return;
        } 
        msgs.addElement(m);
        if (m.unread) if (newMsgCnt>0) newMsgCnt++;
    }
    
  
    public int getColor() { return 0; }
    // public int getColorBGnd() { return 0xffffff; }
    
    public String toString() { 
        return (nick==null)?getJid():nick+jid.getResource(); 
    }
    //public void onSelect(){}

    public final String getJid() {
        return jid.getJidFull();
    }

    public final String getJidNR() {
        return jid.getJid();
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
}
