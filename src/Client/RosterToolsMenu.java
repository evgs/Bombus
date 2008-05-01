/*
 * RosterToolsMenu.java
 *
 * Created on 11.12.2005, 20:43
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
 */

package Client;

//#ifdef PEP
import Mood.MoodList;
//#endif
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
        extends Menu {
    
    /** Creates a new instance of RosterToolsMenu */
    public RosterToolsMenu(Display display) {
        super(SR.MS_TOOLS);
//#ifdef PEP        
        addItem(SR.MS_USERMOOD, 6);
//#endif        
        addItem(SR.MS_DISCO, 0);
        addItem(SR.MS_PRIVACY_LISTS, 1);
        addItem(SR.MS_MY_VCARD, 2);
        addItem(SR.MS_OPTIONS, 3);
        /*if (m.getItemCount()>0)*/
        
//#if (FILE_IO && FILE_TRANSFER)
        addItem(SR.MS_ROOT, 4);
        addItem(SR.MS_FILE_TRANSFERS, 5);
//#endif
        
        
        /*addItem("Break connection", 10);*/
        
        
        attachDisplay(display);
    }
    public void eventOk(){
        destroyView();
        boolean connected= ( StaticData.getInstance().roster.isLoggedIn() );
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
                VCard.request(c.getBareJid(), c.getJid());
                return;
            }
            case 3:
                new ConfigForm(display);
                return;
//#if (FILE_IO)
            case 4:
                new io.file.browse.Browser(null, display, null, false);
                return;
            case 5:
                new io.file.transfer.TransferManager(display);
                return;
//#endif
            
//#ifdef PEP
            case 6:
                new MoodList(display);
                return;
//#endif                
            //case 10:
            //    StaticData.getInstance().roster.connectionTerminated(new Exception("Simulated break"));
            //    return;
            
        }
    }
}
