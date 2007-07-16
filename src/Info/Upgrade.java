/*
 * Upgrade.java
 *
 * Created on 17.07.2007, 0:57
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

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import midlet.Bombus;

/**
 *
 * @author evgs
 */
public class Upgrade implements Runnable, CommandListener{
   
    private Command cmdBack=new Command("Back", Command.BACK, 99);
    private Command cmdInstall=new Command("Install", Command.ITEM, 1);
    private final static String VERSION_URL=Version.BOMBUS_SITE_URL+"/version.php";
    List list;
    Vector versions[];
    
    private Display display;
    private Displayable parentView;
    
    /** Creates a new instance of Upgrade */
    public Upgrade(Display display) {
        parentView=display.getCurrent();
        list=new List("Available versions", List.IMPLICIT);
        list.setCommandListener(this);
        list.addCommand(cmdBack);
        display.setCurrent(list);
        new Thread(this).start();
    }

    public void run() {
        try {
            HttpConnection c = (HttpConnection) Connector.open(VERSION_URL);
            InputStream is = c.openInputStream();
            
            versions=new util.StringLoader().stringLoader(is, 3);
            
            for (int i=0; i<versions[0].size(); i++) {
                if (versions[0].elementAt(i)==null) continue;
                String name=(String)versions[0].elementAt(i)+" "+(String)versions[1].elementAt(i);
                list.append(name, null);
            }
            
            is.close();
            c.close();
            list.addCommand(cmdInstall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) destroyView();
        if (command==cmdInstall) {
            int index=list.getSelectedIndex();
            
            try {
                boolean result=Bombus.getInstance().platformRequest((String) versions[2].elementAt(index));
                if (result) System.exit(0);
            } catch (Exception e) { e.printStackTrace(); }
            
        }
    }

    private void destroyView() {
        display.setCurrent(parentView);
    }
}
