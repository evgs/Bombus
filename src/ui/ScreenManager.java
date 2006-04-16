/*
 * Screens.java
 *
 * Created on 1 Ноябрь 2005 г., 0:39
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

/**
 *
 * @author EvgS
 */
public class ScreenManager {
    
    private Display display;
    private Vector displayStack;
    
    private static ScreenManager instance;
    
    public static ScreenManager getInstance() {
	if (instance==null) instance=new ScreenManager();
	return instance;
    }
    /** Creates a new instance of Screens */
    private ScreenManager() {
	displayStack=new Vector();
    }

    public void setDisplay(Display display) {
        this.display = display;
    }
    
    public void setCurrent(Displayable displayable) {
	if (!displayStack.contains(displayable)) {
	    displayStack.addElement(displayable);
	}
	display.setCurrent(displayable);
    }
    
    public void destroyView(Displayable displayable){
	displayStack.removeElement(displayable);
        show();
    }

    public void show() {
	try {
	    display.setCurrent((Displayable)displayStack.lastElement());
	} catch (Exception e) { System.out.println("nothing to show"); }
    }

    /**
     * SonyEricsson method for minimize midlet
     */
    public void hide() {
	display.setCurrent(null);
    }
}
