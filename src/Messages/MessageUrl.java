/*
 * MessageUrl.java
 *
 * Created on 22 Декабрь 2005 г., 3:01
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Messages;

import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Display;
import midlet.Bombus;
import ui.Menu;

/**
 *
 * @author EvgS
 */
public class MessageUrl extends Menu{
    
    private Vector urlList;
    /** Creates a new instance of MessageUrl */
    public MessageUrl(Display display, Vector urlList) {
	super("URLs");
	this.urlList=urlList;
	
	for (int i=0; i<urlList.size(); i++) {
	    addItem((String)urlList.elementAt(i), i);
	}
	/*if (m.getItemCount()>0)*/
	attachDisplay(display);
    }
    
    public void eventOk() {
	String url=(String)urlList.elementAt(cursor);
//#if !(MIDP1)
	try {
	    Bombus.getInstance().platformRequest(url);
	} catch (Exception e) { e.printStackTrace(); }
//#endif
	destroyView();
    }
}
