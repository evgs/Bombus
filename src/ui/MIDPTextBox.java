/*
 * MIDPTextBox.java
 *
 * Created on 26 Март 2005 г., 20:56
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author  Eugene Stahov
 * @version
 */
public class MIDPTextBox implements CommandListener {
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    protected Command cmdOK=new Command(SR.MS_OK, Command.OK /*Command.SCREEN*/, 1);
    
    private TextBox t;
    
    private TextBoxNotify tbn;

    /**
     * constructor
     */
    public interface TextBoxNotify {
        void OkNotify(String text_return);
    }
   
    public MIDPTextBox(Display display, String title, String text, TextBoxNotify tbn , int constraints) {
        t=new TextBox(title, text, 50, constraints);
        
        this.display=display;
        this.tbn=tbn;
        
        t.addCommand(cmdOK);
        t.addCommand(cmdCancel);
        
        t.setCommandListener(this);
            

        parentView=display.getCurrent();
        display.setCurrent(t);
    }
    
    /**
     * Called when action should be handled
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdCancel) { destroyView(); return;}
        if (command==cmdOK) { destroyView(); tbn.OkNotify(t.getString()); return;}
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
}
