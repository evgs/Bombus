/*
 * ServerBox.java
 *
 * Created on 8 Июль 2005 г., 1:09
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;

import javax.microedition.lcdui.*;
import ui.VirtualList;
import ui.controls.TextFieldCombo;
/**
 *
 * @author EvgS
 */
public class ServerBox implements CommandListener {
    
    private Display display;
    private Form f;
    private TextField t;
    
    private ServiceDiscovery sd;
    
    private Command cmdCancel=new Command("Cancel",Command.BACK,99);
    private Command cmdSend=new Command("Discover",Command.OK /*Command.SCREEN*/,1);
    
    /** Creates a new instance of ServerBox */
    public ServerBox(Display display, String service, ServiceDiscovery sd) {
        this.display=display;
        this.sd=sd;
        
        f=new Form("Service Discovery");
        f.append("Enter Jabber server address here");
        t=new TextFieldCombo("Address",service,500,TextField.URL, "disco", display);
        f.append(t);
        f.addCommand(cmdSend);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);
        
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d){
        String server=t.getString();
        if (server.length()==0) server=null;
        
        /*if (c==cmdCancel) {
            destroyView(); return;
        }*/
        if (c==cmdSend && server!=null) { sd.browse(server, null); }
        
        display.setCurrent(sd);
        return;
    }
}

