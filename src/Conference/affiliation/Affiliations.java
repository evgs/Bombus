/*
 * AffiliationList.java
 *
 * Created on 30.11.2005, 12:34
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
        
        setTitleItem(new Title(2, null, " "));
        getTitleItem().addElement(id);
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
        if (c==cmdNew) new AffiliationModify(display, room, null, "none", "");
        if (c==cmdModify) eventOk();
        if (c!=cmdCancel) return;
        stream.cancelBlockListener(this);
        destroyView();
    }
    
    public void eventOk(){
        try {
            AffiliationItem item=(AffiliationItem)getFocusedObject();
            new AffiliationModify(display, room, item.jid, 
                    AffiliationItem.getAffiliationName(
                        item.affiliation), 
                        (item.reason==null)? "":item.reason
                    );
        } catch (Exception e) { }
    }
    
    private void processIcon(boolean processing){
        String count=(items==null)? null: String.valueOf(items.size());
        getTitleItem().setElementAt((processing)?
            (Object)new Integer(RosterIcons.ICON_PROGRESS_INDEX): 
            (Object)count, 0);
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
                sort(items);
                this.items=items;
                
                if (display!=null) redraw();
                
                processIcon(false);
                return JabberBlockListener.NO_MORE_BLOCKS;
            }
        } catch (Exception e) { }
        return JabberBlockListener.BLOCK_REJECTED;
    }
    
    public void listRq(boolean set, JabberDataBlock child, String id) {
        
        JabberDataBlock request=new Iq(room, (set)? Iq.TYPE_SET: Iq.TYPE_GET, id);
        JabberDataBlock query=request.addChildNs("query", namespace);
        query.addChild(child);
        
        processIcon(true);
        //System.out.println(request.toString());
        stream.addBlockListener(this);
        stream.send(request);
    }
}
