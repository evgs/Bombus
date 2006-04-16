/*
 * ContactMessageList.java
 *
 * Created on 19 Февраль 2005 г., 23:54
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Messages.MessageList;
import Messages.MessageParser;
import archive.MessageArchive;
import images.RosterIcons;
import images.SmilesIcons;
import locale.SR;
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
    Command cmdMessage=new Command(SR.MS_NEW_MESSAGE,Command.SCREEN,2);
    Command cmdResume=new Command(SR.MS_RESUME,Command.SCREEN,1);
    Command cmdQuote=new Command(SR.MS_QUOTE,Command.SCREEN,3);
    Command cmdArch=new Command(SR.MS_ADD_ARCHIVE,Command.SCREEN,4);
    Command cmdPurge=new Command(SR.MS_CLEAR_LIST, Command.SCREEN, 10);
    Command cmdContact=new Command(SR.MS_CONTACT,Command.SCREEN,11);
    Command cmdActive=new Command(SR.MS_ACTIVE_CONTACTS,Command.SCREEN,11);
    
    StaticData sd;
    
    /** Creates a new instance of MessageList */
    public ContactMessageList(Contact contact, Display display) {
        super(display);
        this.contact=contact;
        sd=StaticData.getInstance();
        
        Title title=new Title(contact);
        setTitleItem(title);
        
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
        if (cmdResume==null) return;
        if (contact.msgSuspended==null) removeCommand(cmdResume);
        else addCommand(cmdResume);
    }
    
    protected void beginPaint(){
        getTitleItem().setElementAt(sd.roster.messageIcon,2);
        getTitleItem().setElementAt(contact.incomingComposing, 3);
    }
    
    public void markRead(int msgIndex) {
	if (msgIndex>=getItemCount()) return;
	//Msg msg=getMessage(msgIndex);
        //if (msg.unread) contact.resetNewMsgCnt();
        //msg.unread=false;
        if (msgIndex<contact.lastUnread) return;
        //if (contact.needsCount())
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
            if (contact!=null) new MessageEdit(display,contact,getMessage(cursor).toString());
        }
        if (c==cmdArch) {
	    MessageArchive.store(getMessage(cursor));
        }
        if (c==cmdPurge) {
            contact.purge();
            //attachList(new Vector());
	    messages=new Vector();
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
    
    public void keyRepeated(int keyCode) {
	if (keyCode==KEY_NUM3) new ActiveContacts(display, contact);
	else super.keyPressed(keyCode);
    }
}
