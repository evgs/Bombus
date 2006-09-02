/*
 * Groups.java
 *
 * Created on 8 Май 2005 пїЅ., 0:36
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import images.RosterIcons;
import java.util.*;
import locale.SR;
import ui.ImageList;

/**
 *
 * @author Evg_S
 */
public class Groups{
    
    Vector groups;
    
    public final static int TYPE_TRANSP=0;
    public final static String TRANSP_GROUP=SR.MS_TRANSPORTS;
    public final static int TYPE_SELF=1;
    public final static String SELF_GROUP=SR.MS_SELF_CONTACT;
    public final static int TYPE_SEARCH_RESULT=2;
    public final static String SRC_RESULT_GROUP=SR.MS_SEARCH_RESULTS;
    public final static int TYPE_NOT_IN_LIST=3;
    public final static String NIL_GROUP=SR.MS_NOT_IN_LIST;
    public final static int TYPE_IGNORE=4;
    public final static String IGNORE_GROUP=SR.MS_IGNORE_LIST;
    public final static int TYPE_COMMON=5;
    public final static String COMMON_GROUP=SR.MS_GENERAL;
    
    public Groups(){
        groups=new Vector();
        addGroup(Groups.TRANSP_GROUP, false);
        addGroup(Groups.SELF_GROUP, false);
        addGroup(Groups.SRC_RESULT_GROUP, false);
        addGroup(Groups.NIL_GROUP, false);
        addGroup(Groups.IGNORE_GROUP, false);
        addGroup(Groups.COMMON_GROUP, false);
    }

    private int rosterContacts;
    private int rosterOnline;
    
    public void resetCounters(){
        for (Enumeration e=groups.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
	    grp.startCount();
        }
	rosterContacts=rosterOnline=0;
    }
    
    public void addToVector(Vector d, int index){
        Group gr=getGroup(index);
        if (gr.contacts.size()>0){
            d.addElement(gr);
            if (!gr.collapsed) for (Enumeration e=gr.contacts.elements();e.hasMoreElements();){
                d.addElement(e.nextElement());
            }
        }
	gr.finishCount();
        
        if (index==Groups.TYPE_SEARCH_RESULT) return; ;//don't count this contacts
        if (index==Groups.TYPE_NOT_IN_LIST) return; ;//don't count this contacts
        
	rosterContacts+=gr.getNContacts();
	rosterOnline+=gr.getOnlines();
    }

    public Group getGroup(int Index) {
        return (Group)groups.elementAt(Index);
    }
    
    public Enumeration elements(){
        return groups.elements();
    }
    
    public Group getGroup(String name) {
        for (Enumeration e=groups.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
            if (name.equals(grp.name)) return grp;
        }
        return null;
    }
    public Group addGroup(String name, boolean sort) {
        Group ng=new Group(name);
        int index=TYPE_COMMON+1;
        if (!sort) index=groups.size();
        String lName=name.toLowerCase();
        
        while (index<groups.size()) {
            String grpname=((Group)(groups.elementAt(index))).getName();
            int cmp=lName.compareTo( grpname.toLowerCase() );
            if (cmp<0) {
                ng.index=index;
                groups.insertElementAt(ng, index);
                return ng;
            }
            index++;
        }
        
        ng.index=index;
        groups.addElement(ng);
        return ng;
    }
    
    public Group addGroup(Group group) {
	group.index=groups.size();
        groups.addElement(group);
        return group;
    }

    public Vector getRosterGroupNames(){
        Vector s=new Vector();
        for (int i=Groups.TYPE_COMMON; i<groups.size(); i++) {
	    Group grp=(Group) groups.elementAt(i);
	    if (grp.imageExpandedIndex==RosterIcons.ICON_EXPANDED_INDEX)
            s.addElement(grp.name);
        }
        s.addElement(Groups.IGNORE_GROUP);
        return s;
    }
    public int getCount() {return groups.size();}

    public int getRosterContacts() { return rosterContacts; }
    public int getRosterOnline() { return rosterOnline; }

    void removeGroup(Group g) {
        groups.removeElement(g);
    }
    
}
