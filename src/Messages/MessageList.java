/*
 * MessageList.java
 *
 * Created on 11 ������� 2005 �., 3:02
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Messages;

import Client.Config;
//import Messages.MessageView;
import Client.Msg;
import images.SmilesIcons;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
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
{
    
    protected Vector messages;
    
    protected Command cmdBack = new Command(SR.MS_BACK, Command.BACK, 99);
    
    /** Creates a new instance of MessageList */
    public MessageList(Display display) {
        super(display);
        
	messages=new Vector();
        smiles=Config.getInstance().smiles;
        //sd.config.updateTime();
    
	enableListWrapping(false);
	
        cursor=0;//activate
        
        addCommand(cmdBack);
    }

    public abstract int getItemCount(); // �� protected ������� public

    protected VirtualElement getItemRef(int index) {
	if (messages.size()<getItemCount()) messages.setSize(getItemCount());
	MessageItem mi=(MessageItem) messages.elementAt(index);
	if (mi==null) {
	    mi=new MessageItem(getMessage(index), this);
            mi.setEven( (index & 1) == 0);
	    messages.setElementAt(mi, index);
	}
	return mi;
    }
    
    public abstract Msg getMessage(int index);
	//public Element getItemRef(int Index){ return (Element) contact.msgs.elementAt(Index); }

    // overriden, �.�. �������� ������ - ������.
    /*public Object getFocusedObject(){
        try {
            return getMessage(cursor);
        } catch (Exception e) {}
        return null;
    }*/
    
    public void markRead(int msgIndex) {}
    

    protected boolean smiles;
    
    public void keyGreen() { eventOk(); }
   
}
