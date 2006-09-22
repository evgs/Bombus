/*
 * InfoWindow.java
 *
 * Created on 6 Сентябрь 2005 г., 22:21
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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

    /** Creates a new instance of InfoWindow */
    public InfoWindow(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_ABOUT);
        form.addCommand(new Command(SR.MS_CLOSE, Command.BACK, 99));
        try {
            Image icon=Bombus.programIcon;
            if (icon==null) icon=Image.createImage("/_icon.png");
            form.append(icon);
        } catch (Exception e) { }
        form.append("Bombus v"+Version.version+"\nMobile Jabber client\n");
        form.append(Version.getOs());
        form.append("\nCopyright (c) 2005-2006, Eugene Stahov (evgs),\n");
        form.append (new StringItem(null, Version.url
//#if !(MIDP1)
                , Item.HYPERLINK
//#endif
                ));
        
        StringBuffer memInfo=new StringBuffer("\n\nMemory:\n");
        memInfo.append("Free=");
        //mem.append(Runtime.getRuntime().freeMemory()>>10);
        //mem.append("\nFree=");
        System.gc();
        memInfo.append(Runtime.getRuntime().freeMemory()>>10);
        memInfo.append("\nTotal=");
        memInfo.append(Runtime.getRuntime().totalMemory()>>10);
        form.append(memInfo.toString());
//#if ZLIB
        form.append("\n\n");
        String conn_stats;
        try {
            conn_stats=StaticData.getInstance().roster.theStream.getStreamStats();
        } catch (Exception e) {
            conn_stats="disconnected";
        }
        form.append(conn_stats);
//#endif
     
        form.setCommandListener(this);
        display.setCurrent(form);
    }
    
    public void commandAction(Command c, Displayable d) {
        display.setCurrent(parentView);
    }
}
