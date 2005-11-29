/*
 * ConferenceGroup.java
 *
 * Created on 29 Ноябрь 2005 г., 23:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Conference;

import Client.Contact;
import Client.Group;
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
	imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
    }

    String label;
    
    private Contact selfContact;
    private Contact conference;
    public String toString(){ return title(label); }

    public Contact getSelfContact() { return selfContact; }
    public void setSelfContact(Contact selfContact) { this.selfContact=selfContact; }
    public Contact getConference() { return conference; }
    public void setConference(Contact conference) { this.conference=conference; }

}
