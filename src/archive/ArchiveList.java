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

    Command cmdDelete=new Command("Delete", Command.SCREEN, 2);
    Command cmdPaste=new Command("Paste", Command.SCREEN, 1);
    
    MessageArchive archive=new MessageArchive();
    TextBox target;
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, TextBox target) {
	super (display);
	this.target=target;
	addCommand(cmdBack);
	addCommand(cmdDelete);
	
	if (target!=null) addCommand(cmdPaste);
	
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
	if (c==cmdPaste) { keyGreen(); }
    }
    public void keyGreen() {
	if (target==null) return;
	Msg m=getMessage(cursor);
	if (m==null) return;
	target.insert(m.body, target.size());
	destroyView();
    }
}
