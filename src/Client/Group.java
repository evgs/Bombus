/*
 * Group.java
 *
 * Created on 8.05.2005, 0:36
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

package Client;
import images.RosterIcons;
import java.util.*;
import ui.*;


/**
 *
 * @author Evg_S
 */
public class Group extends IconTextElement {
    String name;
    int index; // group index
    protected int nContacts;
    protected int onlines;
    public int imageExpandedIndex=RosterIcons.ICON_EXPANDED_INDEX;
        
    public Vector contacts;
    
    private Vector tcontacts;
    public int tonlines;
    private int tncontacts;
    public int unreadMessages=0;
    
    boolean collapsed;
    
    public Group(String name /*, String label*/) {
        super(RosterIcons.getInstance());
        this.name=name;
        /*this.label=label;*/
        
    }
    public int getColor(){ return Colors.GROUP_INK; }
    public int getImageIndex() {
        return collapsed?
            RosterIcons.ICON_COLLAPSED_INDEX
            :imageExpandedIndex;
    }
    
    public String getName() { return name; }
    protected String title(String titleStart) {
	return titleStart+" ("+getOnlines()+'/'+getNContacts()+')';
    }
    public String toString(){ return title(name);  }

    public void onSelect(){
        collapsed=!collapsed;
    }

    public void setIndex(int index) {
	this.index = index;
        if (index==Groups.TYPE_SEARCH_RESULT) 
            imageExpandedIndex=RosterIcons.ICON_SEARCH_INDEX;
    }

    public void startCount(){
	//int size=(contacts==null)?10:contacts.size();
	tonlines=tncontacts=unreadMessages=0;
	//tcontacts=new Vector(size);
	contacts=new Vector();
    }

    public void addContact(Contact c) {
	tncontacts++;
	boolean online=c.status<5;
	if (online) {
	    tonlines++;
	}
	//int gindex=c.getGroupIndex();
	// hide offlines whithout new messages
        unreadMessages+=c.getNewMsgsCount();
        
	if (
	online
	|| Config.getInstance().showOfflineContacts
	|| c.getNewMsgsCount()>0
	//|| gindex==Groups.NIL_INDEX
	//|| gindex==Groups.TRANSP_INDEX
	|| index==Groups.TYPE_NOT_IN_LIST
	|| index==Groups.TYPE_TRANSP
	|| c.origin==Contact.ORIGIN_GROUPCHAT
	)
	    contacts.addElement(c);
	//grp.addContact(c);
    }
    void finishCount() {
	//contacts=tcontacts;
        onlines=tonlines;
        nContacts=tncontacts;
        tcontacts=null;
    }

    public int getNContacts() {
        return nContacts;
    }

    public int getOnlines() {
        return onlines;
    }

}
