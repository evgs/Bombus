/*
 * DiscoFeatures.java
 *
 * Created on 6 Июль 2006 г., 23:30
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import locale.SR;

/**
 *
 * @author Eugene Stahov
 */
public class DiscoFeatures implements CommandListener{
    
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    
    List list;

    private Display display;
    private Displayable parentView;
    
    /** Creates a new instance of DiscoFeatures */
    public DiscoFeatures(Display display, String entity, Vector features) {
        if (features.isEmpty()) return;
        
        list=new List(entity, List.IMPLICIT);
        for (Enumeration i=features.elements(); i.hasMoreElements(); ) {
            String feature=(String) (i.nextElement());
            list.append(feature, null);
        }
        
        list.addCommand(cmdBack);
        list.addCommand(cmdOk);
        parentView=display.getCurrent();
        this.display=display;
        
        list.setCommandListener(this);
        display.setCurrent(list);
    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(parentView);
    }
}
