/*
 * RosterToolsMenu.java
 *
 * Created on 11 Декабрь 2005 г., 20:43
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import PrivacyLists.PrivacySelect;
import ServiceDiscovery.ServiceDiscovery;
import javax.microedition.lcdui.Display;
import locale.SR;
import ui.Menu;
import ui.MenuItem;
import vcard.VCard;
import vcard.vCardForm;

/**
 *
 * @author EvgS
 */
public class RosterToolsMenu 
    extends Menu
{
    
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
	super(SR.MS_JABBER_TOOLS);
	addItem(SR.MS_DISCO, 0);
	addItem(SR.MS_PRIVACY_LISTS, 1);
	addItem(SR.MS_MY_VCARD, 2);
        addItem(SR.MS_OPTIONS, 3);
	/*if (m.getItemCount()>0)*/
	attachDisplay(display);
    }
    public void eventOk(){
	destroyView();
        boolean connected= ( StaticData.getInstance().roster.theStream != null );
	MenuItem me=(MenuItem) getFocusedObject();
	if (me==null)  return;
	int index=me.index;
	switch (index) {
	    case 0: // Service Discovery
		if (connected) new ServiceDiscovery(display, null, null);
		break;
	    case 1: // Privacy Lists
		if (connected) new PrivacySelect(display);
		break;
	    case 2: {
                if (! connected) break;
		Contact c=StaticData.getInstance().roster.selfContact();
		if (c.vcard!=null) {
		    new vCardForm(display, c.vcard, true);
		    return;
		}
		VCard.request(c.getJid());
                return;
	    }
            case 3:
                new ConfigForm(display);
	}
    }
}