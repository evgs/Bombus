/*
 * Menu.java
 *
 * Created on 1 Май 2005 г., 20:48
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Evg_S
 */
public class Menu extends VirtualList implements CommandListener
{
    Vector menuitems;
    /** Creates a new instance of Menu */
    Command cmdBack=new Command("Back",Command.BACK,99);
    
    public Menu(String title) {
        super();
        createTitleItem(1, title, null);
        menuitems=new Vector();
        addCommand(cmdBack);
        setCommandListener(this);
    }
    
    public VirtualElement getItemRef(int index){ 
        return (VirtualElement)menuitems.elementAt(index); 
    }
    public int getItemCount() { return menuitems.size(); }
    
    public void addItem(MenuItem mi){
        menuitems.addElement(mi);
    }
    
    public void addItem(String label, int index){
        addItem(new MenuItem(label, index));
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdBack) destroyView();
    }
}
