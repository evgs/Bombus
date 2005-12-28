/*
 * ArchiveList.java
 *
 * Created on 11 Декабрь 2005 г., 5:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package archive;

import Client.Msg;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import ui.ComplexString;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList 
    implements CommandListener
{

    Command cmdDelete=new Command("Delete", Command.SCREEN, 9);
    Command cmdPaste=new Command("Paste Body", Command.SCREEN, 1);
    Command cmdSubj=new Command("Paste Subject", Command.SCREEN, 3);
    Command cmdJid=new Command("Paste Jid", Command.SCREEN, 2);
    //Command cmdNick=new Command("Paste Nickname", Command.SCREEN, 3);
    
    MessageArchive archive=new MessageArchive();
    TextBox target;
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, TextBox target) {
	super (display);
	this.target=target;
	addCommand(cmdBack);
	addCommand(cmdDelete);
	
	if (target!=null) {
	    addCommand(cmdPaste);
	    addCommand(cmdJid);
	}
	focusedItem(0);
	
	setCommandListener(this);
	
	title=new ComplexString(null);
	title.addElement("Archive");
	title.addRAlign();
	title.addElement(null);
	title.addElement("free ");
    }

    protected void beginPaint() {
	title.setElementAt(String.valueOf(archive.freeSpace()),2);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
	if (c==cmdBack) {
	    destroyView();
	    return;
	}
	if (c==cmdDelete) {
	    archive.delete(cursor);
	    attachList(new Vector());
	    redraw();
	}
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
    }
    
    private void pasteData(int field) {
	if (target==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	String data;
	switch (field) {
	case 1: 
	    data=m.subject;
	    break;
	case 2: 
	    data=m.from;
	    break;
	default:
	    data=m.body;
	}
	try {
	    int paste=target.getMaxSize()-target.size();
	    if (paste>data.length()) paste=data.length();
	    target.insert(data.substring(0,paste), target.size());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void focusedItem(int index) {
	if (target==null) return;
	try {
	    if (getMessage(index).subject!=null) {
		addCommand(cmdSubj);
		return;
	    }
	} catch (Exception e) { }
	removeCommand(cmdSubj);
    }
    
    public void destroyView(){
	super.destroyView();
	archive.close();
    }
}
