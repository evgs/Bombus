/*
 * MessageList.java
 *
 * Created on 11 Декабрь 2005 г., 3:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Messages;

import Client.Config;
import Messages.MessageView;
import Client.Msg;
import images.SmilesIcons;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import ui.ComplexString;
import ui.ComplexStringList;

/**
 *
 * @author EvgS
 */
public abstract class MessageList 
    extends ComplexStringList
{
    
    protected Command cmdBack = new Command("Back", Command.BACK, 99);
    
    /** Creates a new instance of MessageList */
    public MessageList(Display display) {
        super(display);
        
        smiles=Config.getInstance().smiles;
        //sd.config.updateTime();
    
	enableListWrapping(false);
	
        attachList(new Vector());

        cursor=0;//activate
        
        addCommand(cmdBack);
    }

    public abstract int getItemCount(); // из protected сделали public
    
    public abstract Msg getMessage(int index);
	//public Element getItemRef(int Index){ return (Element) contact.msgs.elementAt(Index); }

    // overriden, т.к. элементы списка - строки.
    public Object getFocusedObject(){
        try {
            return getMessage(cursor);
        } catch (Exception e) {}
        return null;
    }
    
    public void markRead(int msgIndex) {}
    
    protected ComplexString cacheUpdate(int index) {
	ComplexString m;
	Msg msg = getMessage(index);

	m= (ComplexString)MessageParser.getInstance().
	parseMsg( msg, (smiles)? SmilesIcons.getInstance():null, getWidth()-6, true, null);
	m.setColor(msg.getColor());
	
	    /*if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) {
		m.addImage(ImageList.ICON_AUTHRQ_INDEX);
	    }*/
	//m.insertElementAt(new Integer(msg.getColor1()|0x1000000), 0); //color
	int sz=lines.size(); if (index>=sz) lines.setSize(index+1);
	lines.setElementAt(m, index);
	return m;
    }

    protected boolean smiles;
    
    public void keyGreen() { eventOk(); }

    public void eventOk(){
	Msg msg=(Msg)getFocusedObject();
	if (msg!=null) {
	    //if (contact.msgs.size()==cursor+1) refreshMsgCnt();
	    new MessageView(display, cursor, this);
	}
    }
    
}
