/*
 * AlertBox.java
 *
 * Created on 26 Ноябрь 2006 г., 14:38
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Evg_S
 */
public class AlertBox implements CommandListener{
    
    protected Display display;
    protected Displayable next;
    protected Alert alert;
    protected Command cmdOk=new Command("OK", Command.OK, 1);
    /** Creates a new instance of AlertBox */
    public AlertBox(String title, String text, Image image, Display display, Displayable nextDisplayable) {
        alert=new Alert(title, text, image, null);
        this.display=display;
        next=(nextDisplayable==null)? display.getCurrent() : nextDisplayable;
        
        alert.setTimeout(15000); //15 seconds
        alert.addCommand(cmdOk);
        alert.setCommandListener(this);
        display.setCurrent(alert);
    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(next);
    }
}
