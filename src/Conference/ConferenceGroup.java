/*
 * ConferenceGroup.java
 *
 * Created on 29 Ноябрь 2005 г., 23:11
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;

import Client.Contact;
import Client.Group;
import images.RosterIcons;
import java.util.*;
import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class ConferenceGroup extends Group{
    
    /** Creates a new instance of ConferenceGroup */
    public ConferenceGroup(String name, String label) {
	super(name);
	this.label=label;
	imageExpandedIndex=RosterIcons.ICON_GCJOIN_INDEX;
    }

    String label;
    
    private MucContact selfContact;
    public String password;
    private MucContact conference;
    public String toString(){ return title(label); }

    public MucContact getSelfContact() { return selfContact; }
    public void setSelfContact(MucContact selfContact) { this.selfContact=selfContact; }
    public Contact getConference() { return conference; }
    public void setConference(MucContact conference) { this.conference=conference; }
    // не считаем группу в числе участников
    public int getOnlines(){ return (onlines>0)? onlines-1:0; }
    public int getNContacts(){ return (nContacts>0)? nContacts-1:0; }
    
    public long conferenceJoinTime;
}
