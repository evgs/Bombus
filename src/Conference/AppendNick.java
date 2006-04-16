/*
 * AppendNick.java
 *
 * Created on 14 Сентябрь 2005 г., 23:32
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;

import locale.SR;
import ui.*;
import Client.*;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class AppendNick         
        extends VirtualList 
        implements CommandListener{

    Vector nicknames;
    
    Command cmdSelect=new Command(SR.MS_APPEND, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    
    /** Creates a new instance of AccountPicker */
    public AppendNick(Display display, Contact to) {
        super(display);
        //this.display=display;
        
        setTitleItem(new Title(SR.MS_SELECT_NICKNAME));
        
        nicknames=new Vector();
        for (Enumeration e=StaticData.getInstance().roster.getHContacts().elements(); e.hasMoreElements(); ) {
            Contact c=(Contact)e.nextElement();
            if (c.inGroup(to.getGroup()) && c.origin>Contact.ORIGIN_GROUPCHAT)
                nicknames.addElement(c);
        }

        addCommand(cmdSelect);
        addCommand(cmdCancel);
        
        setCommandListener(this);
    }
    
    public VirtualElement getItemRef(int Index) { return (VirtualElement)nicknames.elementAt(Index); }
    protected int getItemCount() { return nicknames.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
            //Account.launchAccount();
            //StaticData.getInstance().account_index=0;
        }
        if (c==cmdSelect) eventOk();
        
    }
    public void eventOk(){
        TextBox t=(TextBox)parentView;

        try {
            String nick=((Contact)getFocusedObject()).getJid();
            int rp=nick.indexOf('/');
            StringBuffer b=new StringBuffer(nick.substring(rp+1));
            
            if (t.size()>0) {
                b.insert(0, (char)0x20);
                b.insert(0, t.getString());
            } else {
                b.append(": ");
            }
            t.setString(b.toString());
        } catch (Exception e) {}
        
        destroyView();
    }

}
