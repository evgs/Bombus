/*
 * Group.java
 *
 * Created on 8 пїЅпїЅпїЅ 2005 пїЅ., 0:36
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
    
    // пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ
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
