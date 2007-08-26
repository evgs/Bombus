/*
 * ArchiveList.java
 *
 * Created on 11.12.2005, 5:24
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package archive;

import Client.MessageEdit;
import Client.Msg;
import Client.Title;
import Messages.MessageList;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import locale.SR;
import ui.ComplexString;
import ui.YesNoAlert;

/**
 *
 * @author EvgS
 */
public class ArchiveList 
    extends MessageList 
    implements YesNoAlert.YesNoListener
{

    Command cmdDelete=new Command(SR.MS_DELETE /*"Delete"*/, Command.SCREEN, 9);
    Command cmdPaste=new Command(SR.MS_PASTE_BODY /*"Paste Body"*/, Command.SCREEN, 1);
    Command cmdSubj=new Command(SR.MS_PASTE_SUBJECT /*"Paste Subject"*/, Command.SCREEN, 3);
    Command cmdJid=new Command(SR.MS_PASTE_JID /*"Paste Jid"*/, Command.SCREEN, 2);
    //Command cmdNick=new Command("Paste Nickname", Command.SCREEN, 3);
    
    MessageArchive archive=new MessageArchive();
    MessageEdit target;
    
    private int caretPos;
    /** Creates a new instance of ArchiveList */
    public ArchiveList(Display display, MessageEdit target, int caretPos) {
	super ();
	this.target=target;
        this.caretPos=caretPos;
	setCommandListener(this);
	addCommand(cmdBack);
	addCommand(cmdDelete);
	
	if (target!=null) {
	    addCommand(cmdPaste);
	    addCommand(cmdJid);
            //TODO: re-enable item-specific dynamic commands)
            addCommand(cmdSubj);
	}
        
        attachDisplay(display);
        
        
        try {
            focusedItem(0);
        } catch (Exception e) {}
	
	Title title=new Title(SR.MS_ARCHIVE /*"Archive"*/);
	title.addElement(null);
	title.addRAlign();
	title.addElement(null);
	title.addElement(SR.MS_FREE /*"free "*/);
        setTitleItem(title);
        
    }

    protected void beginPaint() {
        getTitleItem().setElementAt(" ("+String.valueOf(getItemCount())+")",1);
	getTitleItem().setElementAt(String.valueOf(archive.freeSpace()),3);
    }
    
    public int getItemCount() {
	return archive.size();
    }
    
    public Msg getMessage(int index) {
	return archive.msg(index);
    }

    public void commandAction(Command c, Displayable d) {
        super.commandAction(c,d);
	if (c==cmdDelete) { deleteMessage(); }
	if (c==cmdPaste) { pasteData(0); }
	if (c==cmdSubj) { pasteData(1); }
	if (c==cmdJid) { pasteData(2); }
    }

    private void deleteMessage() {
        archive.delete(cursor);
        messages=new Vector();
        redraw();
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
	    data=m.quoteString();
	}
        if (data==null) return;
        target.insertText(data, caretPos);
	destroyView();
    }
    
    public void keyGreen() { pasteData(0); }
    
    public void userKeyPressed(int keyCode) {
        super.userKeyPressed(keyCode);
        if (keyCode==keyClear) {
            if (getItemCount()>0) new YesNoAlert(display, SR.MS_DELETE, SR.MS_SURE_DELETE, this);
        }
    }
    public void ActionConfirmed() {
        deleteMessage();
    }
    
    /*public void focusedItem(int index) {
	if (target==null) return;
	try {
	    if (getMessage(index).subject!=null) {
		addCommand(cmdSubj);
		return;
	    }
	} catch (Exception e) { }
	removeCommand(cmdSubj);
    }*/
    
    public void destroyView(){
	super.destroyView();
	archive.close();
    }

}
