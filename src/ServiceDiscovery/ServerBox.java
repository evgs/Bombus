/*
 * ServerBox.java
 *
 * Created on 8 Èþëü 2005 ã., 1:09
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;

import javax.microedition.lcdui.*;
import ui.VirtualList;
/**
 *
 * @author EvgS
 */
public class ServerBox implements CommandListener {
    
    private Display display;
    private TextBox t;
    
    private ServiceDiscovery sd;
    
    private Command cmdCancel=new Command("Cancel",Command.BACK,99);
    private Command cmdSend=new Command("Discover",Command.OK /*Command.SCREEN*/,1);
    
    /** Creates a new instance of ServerBox */
    public ServerBox(Display display, String service, ServiceDiscovery sd) {
        this.display=display;
        this.sd=sd;
        
        t=new TextBox("Address",service,500,TextField.URL);
        t.addCommand(cmdSend);
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
        display.setCurrent(t);
    }
    
    public void commandAction(Command c, Displayable d){
        String server=t.getString();
        if (server.length()==0) server=null;
        
        if (c==cmdCancel) {
            /*destroyView(); return;*/
        }
        if (c==cmdSend && server!=null) { sd.browse(server, null); }
        
        display.setCurrent(sd);
        return;
    }
}

