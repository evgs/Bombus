/*
 * AffiliationList.java
 *
 * Created on 30 Октябрь 2005 г., 12:34
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference.affiliation;

import Client.*;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import images.RosterIcons;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.ImageList;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author EvgS
 */
public class Affiliations 
        extends VirtualList 
        implements CommandListener,
        JabberBlockListener
{

    private Vector items;
    private String id="admin";
    private String namespace="http://jabber.org/protocol/muc#admin";
    private String room;

    private JabberStream stream=StaticData.getInstance().roster.theStream;
    
    private Command cmdCancel=new Command (SR.MS_BACK, Command.BACK, 99);
    private Command cmdModify=new Command (SR.MS_MODIFY, Command.SCREEN, 1);
    private Command cmdNew=new Command (SR.MS_NEW_JID, Command.SCREEN, 2);
 
    
    protected VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index); }
    protected int getItemCount() { return items.size(); }
    
    
    
    /** Creates a new instance of AffiliationList */
    public Affiliations(Display display, String room, int affiliationIndex) {
        super (display);
        this.room=room;
	
	//fix for old muc
	switch (affiliationIndex) {
	    case AffiliationItem.AFFILIATION_OWNER:
	    case AffiliationItem.AFFILIATION_ADMIN:
		if (!Config.getInstance().muc119) namespace="http://jabber.org/protocol/muc#owner";
	}
	
        this.id=AffiliationItem.getAffiliationName(affiliationIndex);
        
        setTitleItem(new Title(2, null, id));
        items=new Vector();
        
        addCommand(cmdCancel);
        addCommand(cmdModify);
        addCommand(cmdNew);
        
        setCommandListener(this);
        getList();
    }
    
    public void getList() {
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        item.setAttribute("affiliation", id);
        listRq(false, item, id);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdNew) new AffiliationModify(display, room, null, "none");
        if (c==cmdModify) eventOk();
        if (c!=cmdCancel) return;
        stream.cancelBlockListener(this);
        destroyView();
    }
    
    public void eventOk(){
        try {
            AffiliationItem item=(AffiliationItem)getFocusedObject();
            new AffiliationModify(display, room, item.jid, 
                    AffiliationItem.getAffiliationName(item.affiliation));
        } catch (Exception e) { }
    }
    
    private void processIcon(boolean processing){
        getTitleItem().setElementAt((processing)?(Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX):(Object)null, 0);
        redraw();
    }
    
    public int blockArrived(JabberDataBlock data) {
        try {
            ///System.out.println(data.toString());
            
            if (data.getAttribute("id").equals(id)) {
                JabberDataBlock query=data.findNamespace(namespace);
                Vector items=new Vector();
                try {
                    for (Enumeration e=query.getChildBlocks().elements(); e.hasMoreElements(); ){
                        items.addElement(new AffiliationItem((JabberDataBlock)e.nextElement()));
                    }
                } catch (Exception e) { /* no any items */}
                //StaticData.getInstance().roster.bookmarks=
                this.items=items;
                
                if (display!=null) redraw();
                
                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public void listRq(boolean set, JabberDataBlock child, String id) {
        
        JabberDataBlock request=new Iq(room, /*(set)?"set":"get"*/ set, id);
        JabberDataBlock query=request.addChild("query", null);
        query.setNameSpace(namespace);
        query.addChild(child);
        
        processIcon(true);
        //System.out.println(request.toString());
        stream.addBlockListener(this);
        stream.send(request);
    }
}
