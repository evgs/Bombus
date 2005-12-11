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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList 
    implements CommandListener
{

    Command cmdDelete=new Command("Delete", Command.SCREEN, 2);
    
    MessageArchive archive=new MessageArchive();
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display) {
	super (display);
	addCommand(cmdBack);
	setCommandListener(this);
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
    }
}
