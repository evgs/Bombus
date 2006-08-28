/*
 * MessageList.java
 *
 * Created on 11 Декабрь 2005 г., 3:02
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Messages;

import Client.Config;
import Client.Msg;
//import Messages.MessageView;
import images.SmilesIcons;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.ComplexString;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public abstract class MessageList 
    extends VirtualList
    implements CommandListener
{
    
    protected Vector messages;
    
    protected Command cmdBack = new Command(SR.MS_BACK, Command.BACK, 99);
    protected Command cmdUrl = new Command(SR.MS_GOTO_URL, Command.SCREEN, 80);

    protected Command cmdSmiles = new Command(SR.MS_SMILES_TOGGLE, Command.SCREEN, 50);
    
    /** Creates a new instance of MessageList */
  
    public MessageList() {
        super();
	messages=new Vector();
        smiles=Config.getInstance().smiles;
        //sd.config.updateTime();
    
	enableListWrapping(false);
	
        cursor=0;//activate
        
        addCommand(cmdSmiles);
        addCommand(cmdBack);
        addCommand(cmdUrl);
    }

    public MessageList(Display display) {
        this();
        attachDisplay(display);
    }
    
    
    public abstract int getItemCount(); // из protected сделали public

    protected VirtualElement getItemRef(int index) {
	if (messages.size()<getItemCount()) messages.setSize(getItemCount());
	MessageItem mi=(MessageItem) messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this, smiles);
            mi.setEven( (index & 1) == 0);
	    messages.setElementAt(mi, index);
	}
	return mi;
    }
    
    public abstract Msg getMessage(int index);
	//public Element getItemRef(int Index){ return (Element) contact.msgs.elementAt(Index); }

    // overriden, т.к. элементы списка - строки.
    /*public Object getFocusedObject(){
        try {
            return getMessage(cursor);
        } catch (Exception e) {}
        return null;
    }*/
    
    public void markRead(int msgIndex) {}
    
    protected boolean smiles;

    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) destroyView();
        if (c==cmdUrl) {
            try {
                Vector urls=((MessageItem) getFocusedObject()).getUrlList();
                new MessageUrl(display, urls); //throws NullPointerException if no urls
            } catch (Exception e) {/* no urls found */}
        }
        if (c==cmdSmiles) {
            ((MessageItem)getFocusedObject()).toggleSmiles();
        }
    }

    protected void keyPressed(int keyCode) { // overriding this method to avoid autorepeat
        super.keyPressed(keyCode);
        if (keyCode=='*') ((MessageItem)getFocusedObject()).toggleSmiles();
    }

    public void keyGreen() { eventOk(); }
   
}
