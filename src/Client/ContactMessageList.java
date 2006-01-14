/*
 * MessageList.java
 *
 * Created on 19 Февраль 2005 г., 23:54
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Messages.MessageList;
import Messages.MessageParser;
import archive.MessageArchive;
import images.RosterIcons;
import images.SmilesIcons;
import vcard.VCard;
import ui.*;
import java.util.*;
import javax.microedition.lcdui.*;
/**
 *
 * @author Eugene Stahov
 */
public class ContactMessageList extends MessageList
implements CommandListener{
    
    Contact contact;
    Command cmdMessage=new Command("New Message",Command.SCREEN,2);
    Command cmdResume=new Command("Resume Message",Command.SCREEN,1);
    Command cmdQuote=new Command("Quote",Command.SCREEN,3);
    Command cmdArch=new Command("to Archive",Command.SCREEN,4);
    Command cmdPurge=new Command("Clear List", Command.SCREEN, 10);
    Command cmdContact=new Command("Contact >",Command.SCREEN,11);
    Command cmdActive=new Command("Active Contacts",Command.SCREEN,11);
    
    StaticData sd;
    
    /*public interface Element {
        int getColor1();
        String getMsgHeader();
        int getColor2();
        String toString();
        void onSelect();
    }
     */

    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd=StaticData.getInstance();
        
        title=new ComplexString(RosterIcons.getInstance());
        
        title.addElement(contact.toString());
        title.addRAlign();
        title.addElement(null);
        title.addElement(null);
        //setTitleLine(title);

        cursor=0;//activate
        
        addCommand(cmdMessage);
        addCommand(cmdPurge);
        addCommand(cmdContact);
	addCommand(cmdActive);
        if (getItemCount()>0) {
            addCommand(cmdQuote);
            addCommand(cmdArch);
	}
        setCommandListener(this);
        moveCursorTo(contact.firstUnread(), true);
    }
    
    public void showNotify(){
        super.showNotify();
        if (contact.msgSuspended==null) removeCommand(cmdResume);
        else addCommand(cmdResume);
    }
    
    protected void beginPaint(){ 
        title.setElementAt(sd.roster.messageIcon,2);
        title.setElementAt(contact.incomingComposing, 3);
    }
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
	Msg msg=getMessage(msgIndex);
        if (msg.unread) contact.resetNewMsgCnt();
        msg.unread=false;
        if (msgIndex<contact.lastUnread) return;
        if (contact.needsCount())
            sd.roster.countNewMsgs();
    }
    
    
    public int getItemCount(){ return contact.msgs.size(); }
    //public Element getItemRef(int Index){ return (Element) contact.msgs.elementAt(Index); }

    public Msg getMessage(int index) { 
	Msg msg=(Msg) contact.msgs.elementAt(index); 
	if (msg.unread) contact.resetNewMsgCnt();
	msg.unread=false;
	return msg;
    }
    
    public void focusedItem(int index){ markRead(index); }
        
    public void commandAction(Command c, Displayable d){
        if (c==cmdBack) {
            //contact.lastReaded=contact.msgs.size();
            //contact.resetNewMsgCnt();            
            destroyView();
            return;
        }
        if (c==cmdMessage) { 
            contact.msgSuspended=null; 
            keyGreen(); 
        }
        if (c==cmdResume) { keyGreen(); }
        if (c==cmdQuote) {
            new MessageEdit(display,contact,((Msg)getFocusedObject()).toString());
        }
        if (c==cmdArch) {
	    MessageArchive.store((Msg)getFocusedObject());
        }
        if (c==cmdPurge) {
            contact.purge();
            attachList(new Vector());
            System.gc();
            redraw();
        }
        if (c==cmdContact) {
            new RosterItemActions(display, contact);
        }
	
	if (c==cmdActive) {
	    new ActiveContacts(display, contact);
	}
    }
    public void keyGreen(){
        (new MessageEdit(display,contact,contact.msgSuspended)).setParentView(this);
        contact.msgSuspended=null;
    }
}
