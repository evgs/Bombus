/*
 * InfoWindow.java
 *
 * Created on 6.09.2005, 22:21
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

package Info;
import Client.StaticData;
import javax.microedition.lcdui.*;
import locale.SR;
import midlet.Bombus;

/**
 *
 * @author EvgS
 */
public class InfoWindow implements CommandListener{

    private Display display;
    private Displayable parentView;
    
    private Form form;

    private Command cmdBack=new Command(SR.MS_CLOSE, Command.BACK, 99);
    private Command cmdUpgrade=new Command(SR.MS_VERSIONS, Command.SCREEN, 1);
    /** Creates a new instance of InfoWindow */
    public InfoWindow(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_ABOUT);
        form.addCommand(cmdBack);
        form.addCommand(cmdUpgrade);
        try {
            Image icon=Bombus.programIcon;
            if (icon==null) icon=Image.createImage("/_icon.png");
            form.append(icon);
        } catch (Exception e) { }
        form.append(Version.getNameVersion()+"\nMobile Jabber client\n");
        form.append(Version.getOs());
        form.append("\nCopyright (c) 2005-2008, Eugene Stahov (evgs),\n");
        form.append (new StringItem(null, Version.BOMBUS_SITE_URL
//#if !(MIDP1)
                , Item.HYPERLINK
//#endif
                ));
        
        StringBuffer memInfo=new StringBuffer("\n\n");
        memInfo.append(SR.MS_MEMORY); 
        memInfo.append("\n");
        memInfo.append(SR.MS_MEMORY_FREE);
        //mem.append(Runtime.getRuntime().freeMemory()>>10);
        //mem.append("\nFree=");
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10);
        memInfo.append("\n");
        memInfo.append(SR.MS_MEMORY_TOTAL);
        memInfo.append(Runtime.getRuntime().totalMemory()>>10);
        form.append(memInfo.toString());
//#if ZLIB
        form.append("\n\n");
        String conn_stats;
        try {
            conn_stats=StaticData.getInstance().roster.theStream.getStreamStats();
        } catch (Exception e) {
            conn_stats=SR.MS_DISCONNECTED;
        }
        form.append(conn_stats);
//#endif
     
        form.setCommandListener(this);
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) display.setCurrent(parentView);
        if (c==cmdUpgrade) new Upgrade(display);
    }
}
