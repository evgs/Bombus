/*
 * PrivacyModifyList.java
 *
 * Created on 11 Сентябрь 2005 г., 15:51
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package PrivacyLists;

import Client.Roster;
import Client.StaticData;
import Client.Title;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacyModifyList 
        extends VirtualList 
        implements CommandListener,
        JabberBlockListener
{
    private PrivacyList plist;
    
    private Command cmdCancel=new Command ("Back", Command.BACK, 99);
    private Command cmdAdd=new Command ("Add rule", Command.SCREEN, 10);
    private Command cmdDel=new Command ("Delete rule", Command.SCREEN, 11);
    private Command cmdEdit=new Command ("Edit rule", Command.SCREEN, 12);
    private Command cmdUp=new Command ("Move Up", Command.SCREEN, 13);
    private Command cmdDwn=new Command ("Move Down", Command.SCREEN, 14);
    private Command cmdSave=new Command ("Save list", Command.SCREEN, 16);
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacyModifyList(Display display, PrivacyList privacyList) {
        super(display);
        setTitleItem(new Title(2, null, "Privacy Lists" ));
        addCommand(cmdCancel);
        addCommand(cmdEdit);
        addCommand(cmdAdd);
        addCommand(cmdDel);
        addCommand(cmdUp);
        addCommand(cmdDwn);
        addCommand(cmdSave);
        
        setCommandListener(this);

        plist=privacyList;
        getList();
    }
    
    private void processIcon(boolean processing){
        getTitleItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }

    private void getList(){
        processIcon(true);
        stream.addBlockListener(this);
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", plist.name);
        PrivacyList.privacyListRq(false, list, "getlistitems");
    }
    
    protected int getItemCount() { return plist.rules.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) plist.rules.elementAt(index); }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            stream.cancelBlockListener(this);
            destroyView();
        }
        if (c==cmdAdd) {
            new PrivacyForm(display, new PrivacyItem(), plist);
        }
        if (c==cmdEdit) eventOk();
        if (c==cmdDel) {
            Object del=getFocusedObject();
            if (del!=null) plist.rules.removeElement(del);
        }
        if (c==cmdSave) {
            plist.generateList();
            stream.cancelBlockListener(this);
            PrivacyList.privacyListRq(false, null, "setplists");
            destroyView();
        }
        
        if (c==cmdUp) { move(-1); keyUp(); }
        if (c==cmdDwn) { move(+1); keyDwn(); }
        redraw();
    }
    
    public void move(int offset){
        try {
            int index=cursor;
            PrivacyItem p1=(PrivacyItem)plist.rules.elementAt(index);
            PrivacyItem p2=(PrivacyItem)plist.rules.elementAt(index+offset);
            
            plist.rules.setElementAt(p1, index+offset);
            plist.rules.setElementAt(p2, index);
            
            int tmpOrder=p1.order;
            p1.order=p2.order;
            p2.order=tmpOrder;
            
        } catch (Exception e) {/* IndexOutOfBounds */}
    }

    public void eventOk(){
        PrivacyItem pitem=(PrivacyItem) getFocusedObject();
        if (pitem!=null) {
            new PrivacyForm(display, pitem, null);
        }
    }
    
    public int blockArrived(JabberDataBlock data){
        if (data.getTypeAttribute().equals("result"))
            if (data.getAttribute("id").equals("getlistitems")) {
                data=data.findNamespace("jabber:iq:privacy");
                try {
                    data=data.getChildBlock("list");
                    plist.rules=new Vector();
                    for (Enumeration e=data.getChildBlocks().elements(); e.hasMoreElements();) {
                        JabberDataBlock item=(JabberDataBlock) e.nextElement();
                        plist.addRule(new PrivacyItem(item));
                    }
                } catch (Exception e) {}
                
                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            } //id, result
        return JabberBlockListener.BLOCK_REJECTED;
    }

}
