/*
 * RosterItemActions.java
 *
 * Created on 11 Декабрь 2005 г., 19:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import Conference.ConferenceGroup;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import ServiceDiscovery.ServiceDiscovery;
import com.alsutton.jabber.datablocks.IqVersionReply;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Hashtable;
import javax.microedition.lcdui.Display;
import ui.IconTextElement;
import ui.Menu;
import ui.MenuItem;
import ui.YesNoAlert;
import vcard.VCard;
import vcard.vCardForm;

/**
 *
 * @author EvgS
 */
public class RosterItemActions extends Menu{
    
    Object item;
    
    /** Creates a new instance of RosterItemActions */
    public RosterItemActions(Display display, Object item) {
	super(item.toString());
	this.item=item;
	
        if (item==null) return;
        boolean isContact=( item instanceof Contact );

	if (isContact) {
	    Contact contact=(Contact)item;
	    if (contact.group==Groups.TRANSP_INDEX) {
		addItem("Logon",5);
		addItem("Logoff",6);
		addItem("Resolve Nicknames", 7);
	    }
	    //if (contact.group==Groups.SELF_INDEX) addItem("Commands",30);
	    
	    addItem("vCard",1);
	    addItem("Client Info",0);
	    addItem("Commands",30);
	    
	    if (contact.group!=Groups.SELF_INDEX && contact.group!=Groups.SRC_RESULT_INDEX && contact.origin<Contact.ORIGIN_GROUPCHAT) {
		if (contact.group!=Groups.TRANSP_INDEX)
		    addItem("Edit",2);
		addItem("Subscription",3);
		addItem("Delete",4);
	    }
	    if (contact.realJid!=null) {
		addItem("Kick",8);
		addItem("Ban",9);
		//m.addItem(new MenuItem("Set Attiliation",15));
	    }
	} else {
	    Group group=(Group)item;
	    if (group.index==Groups.SRC_RESULT_INDEX)
		addItem("Discard Search",21);
	    if (group instanceof ConferenceGroup) {
		Contact self=((ConferenceGroup)group).getSelfContact();
		if (self.status==Presence.PRESENCE_OFFLINE)
		    addItem("Re-Enter Room",23);
		else {
		    addItem("Leave Room",22);
		    if (self.transport>0) { // гнустный хак
			addItem("Configure Room",10);
			addItem("Owners",11);
			addItem("Admins",12);
			addItem("Members",13);
			addItem("Outcasts (Ban)",14);
		    }
		}
	    }
	    //m.addItem(new MenuItem("Cleanup offlines"))
	}
	if (getItemCount()>0) attachDisplay(display);
	
    }
    
    public void eventOk(){
	final Roster roster=StaticData.getInstance().roster;
        boolean isContact=( item instanceof Contact );
	Contact c = null;
	Group g = null;
	if (isContact) c=(Contact)item; else g=(Group) item;
	
	MenuItem me=(MenuItem) getFocusedObject();
	if (me==null) {
	    destroyView(); return;
	}
	int index=me.index;
	String to=null;
	if (isContact) to=(index<3)? c.getJid() : c.getBareJid();
	destroyView();
	switch (index) {
	    case 0: // info
		roster.setQuerySign(true);
		roster.theStream.send(new IqVersionReply(to));
		break;
	    case 1: // vCard
		if (c.vcard!=null) {
		    new vCardForm(display, c.vcard, c.group==Groups.SELF_INDEX);
		    return;
		}
		VCard.request(c.getJid());
		break;
		
	    case 2:
		(new ContactEdit(display, c )).parentView=roster;
		return; //break;
		
	    case 3: //subscription
		new SubscriptionEdit(display, c);
		return; //break;
	    case 4:
		new YesNoAlert(display, roster, "Delete contact?", c.getNickJid()){
		    public void yes() {
			roster.deleteContact((Contact)item);
		    };
		};
		return;
		//new DeleteContact(display,c);
		//break;
	    case 6: // logoff
	    {
		//querysign=true; displayStatus();
		Presence presence = new Presence(
		Presence.PRESENCE_OFFLINE, -1, "");
		presence.setTo(c.getJid());
		roster.theStream.send( presence );
		break;
	    }
	    case 5: // logon
	    {
		//querysign=true; displayStatus();
		Presence presence = new Presence(roster.myStatus, 0, "");
		presence.setTo(c.getJid());
		roster.theStream.send( presence );
		break;
	    }
	    case 7: // Nick resolver
	    {
		roster.resolveNicknames(c.transport);
		break;
	    }
	    case 8: // kick
	    {
		Hashtable attrs=new Hashtable();
		attrs.put("role", "none");
		attrs.put("nick", c.jid.getResource().substring(1));
		roster.setMucMod(c, attrs);
		break;
	    }
	    case 9: // ban
	    {
		Hashtable attrs=new Hashtable();
		attrs.put("affiliation", "outcast");
		attrs.put("jid", c.realJid);
		roster.setMucMod(c, attrs);
		break;
	    }
	    case 10: // room config
	    {
		String roomJid=((ConferenceGroup)g).getConference().getJid();
		new QueryConfigForm(display, roomJid);
		break;
	    }
	    case 11: // owners
	    case 12: // admins
	    case 13: // members
	    case 14: // outcasts
	    {
		String roomJid=((ConferenceGroup)g).getConference().getJid();
		new Affiliations(display, roomJid, index-10);
		return;
	    }
		    /*case 15: // affiliation
		    {
			String roomJid=conferenceRoomContact(g.index).getJid();
			new AffiliationModify(display, roomJid, c.realJid, affiliation)(display, roomJid, index-10);
		    }
		     */
	    case 21:
	    {
		roster.cleanupSearch();
		break;
	    }
	    case 22:
	    {
		roster.leaveRoom( g.index );
		break;
	    }
	    case 23:
	    {
		roster.reEnterRoom( g );
		break;
	    }
	    case 30:
	    {
		new ServiceDiscovery(display, c.getJid(), "http://jabber.org/protocol/commands");
		return;
	    }
	}
	destroyView();
    }
}
