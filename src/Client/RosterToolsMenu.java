/*
 * RosterToolsMenu.java
 *
 * Created on 11 Декабрь 2005 г., 20:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import PrivacyLists.PrivacySelect;
import ServiceDiscovery.ServiceDiscovery;
import javax.microedition.lcdui.Display;
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
	super("Jabber Tools");
	addItem("Service Discovery", 0);
	addItem("Privacy Lists", 1);
	addItem("My vCard", 2);
	/*if (m.getItemCount()>0)*/
	attachDisplay(display);
    }
    public void eventOk(){
	destroyView();
	MenuItem me=(MenuItem) getFocusedObject();
	if (me==null)  return;
	int index=me.index;
	switch (index) {
	    case 0: // Service Discovery
		new ServiceDiscovery(display, null, null);
		break;
	    case 1: // Privacy Lists
		new PrivacySelect(display);
		break;
	    case 2: {
		Contact c=StaticData.getInstance().roster.selfContact();
		if (c.vcard!=null) {
		    new vCardForm(display, c.vcard, c.group==Groups.SELF_INDEX);
		    return;
		}
		VCard.request(c.getJid());
	    }
	}
    }
}