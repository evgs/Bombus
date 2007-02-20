/*
 * Screens.java
 *
 * Created on 1.11.2005, 0:39
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
