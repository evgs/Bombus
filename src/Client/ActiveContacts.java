/*
 * ActiveContacts.java
 *
 * Created on 20 ������� 2005 �., 21:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */
 
package Client;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class ActiveContacts 
    extends VirtualList
    implements CommandListener
{
    
    Vector activeContacts;
    
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    private Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 1);
    /** Creates a new instance of ActiveContacts */
    public ActiveContacts(Display display, Contact current) {
	super();
	activeContacts=new Vector();
	for (Enumeration r=StaticData.getInstance().roster.getHContacts().elements(); 
	    r.hasMoreElements(); ) 
	{
	    Contact c=(Contact)r.nextElement();
	    if (c.active()) activeContacts.addElement(c);
	}
	// �� ������ ���, ���� ��� �������� ���������
	if (getItemCount()==0) return;
	
        setTitleItem(new Title(2, String.valueOf(getItemCount()), SR.MS_ACTIVE_CONTACTS));

	addCommand(cmdSelect);
	addCommand(cmdCancel);
	setCommandListener(this);
	
	int focus=activeContacts.indexOf(current);
	if (focus!=-1) moveCursorTo(focus, true);
	//if (current!=null) mov
	
	attachDisplay(display);
    }

    protected int getItemCount() { return activeContacts.size(); }
    protected VirtualElement getItemRef(int index) { 
	return (VirtualElement) activeContacts.elementAt(index);
    }

    public void eventOk() {
	Contact c=(Contact)getFocusedObject();
	new ContactMessageList((Contact)c,display).setParentView(StaticData.getInstance().roster);
    }
    
    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) destroyView();
	if (c==cmdSelect) eventOk();
    }
    
    public void keyPressed(int keyCode) {
	if (keyCode==KEY_NUM3) destroyView();
	else super.keyPressed(keyCode);
    }
}
