/*
 * MucContact.java
 *
 * Created on 2.05.2006, 14:05
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

package Conference;

import Client.Contact;
import Client.Msg;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.XmppError;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import locale.SR;

/**
 *
 * @author root
 */
public class MucContact extends Contact{
    
    public final static int AFFILIATION_OUTCAST=-1;
    public final static int AFFILIATION_NONE=0;
    public final static int AFFILIATION_MEMBER=1;
    public final static int AFFILIATION_ADMIN=2;
    public final static int AFFILIATION_OWNER=3;
    
    public final static int ROLE_VISITOR=-1;
    public final static int ROLE_PARTICIPANT=0;
    public final static int ROLE_MODERATOR=1;
    
    public String realJid;
    
    public String affiliation;
    public String role;
    
    public int roleCode;
    public int affiliationCode;

    public boolean commonPresence=true;

    public long lastMessageTime;

    /** Creates a new instance of MucContact */
    public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
    }
    
    private void appendL(StringBuffer sb, String append){
        sb.append((char)1);
        sb.append(append);
        sb.append((char)2);
    }
    
    public String processPresence(JabberDataBlock xmuc, Presence presence) {
        //System.out.println(presence);
        String from=jid.getJid();
        
        int presenceType=presence.getTypeIndex();
        
        if (presenceType==Presence.PRESENCE_ERROR) {
            XmppError xe=XmppError.findInStanza(presence);
            int errCode=xe.getCondition();

            ConferenceGroup grp=(ConferenceGroup)getGroup();
            if (status>=Presence.PRESENCE_OFFLINE) testMeOffline();
            
            if (errCode!=XmppError.CONFLICT || status>=Presence.PRESENCE_OFFLINE)
                setStatus(presenceType);
            
            String errText=xe.getText();
            if (errText!=null) return xe.toString(); // if error description is provided by server
            
            // legacy codes
            switch (errCode) {
                case XmppError.NOT_AUTHORIZED:        return "Password required";
                case XmppError.FORBIDDEN:             return "You are banned in this room";
                case XmppError.ITEM_NOT_FOUND:        return "Room does not exists";
                case XmppError.NOT_ALLOWED:           return "You can't create room on this server";
                case XmppError.NOT_ACCEPTABLE:        return "Reserved roomnick must be used";
                case XmppError.REGISTRATION_REQUIRED: return "This room is members-only";
                case XmppError.CONFLICT:              return "Nickname is already in use by another occupant";
                case XmppError.SERVICE_UNAVAILABLE:   return "Maximum number of users has been reached in this room";
                default: return xe.getName();
            }
        }
        
        JabberDataBlock item=xmuc.getChildBlock("item");   

        String role=item.getAttribute("role");
        if (role.equals("visitor")) roleCode=ROLE_VISITOR;
        if (role.equals("participant")) roleCode=ROLE_PARTICIPANT;
        if (role.equals("moderator")) roleCode=ROLE_MODERATOR;
        
        String affiliation=item.getAttribute("affiliation");
        if (affiliation.equals("owner")) affiliationCode=AFFILIATION_OWNER;
        if (affiliation.equals("admin")) affiliationCode=AFFILIATION_ADMIN;
        if (affiliation.equals("member")) affiliationCode=AFFILIATION_MEMBER;
        if (affiliation.equals("none")) affiliationCode=AFFILIATION_NONE;
        
        boolean roleChanged= !role.equals(this.role);
        boolean affiliationChanged= !affiliation.equals(this.affiliation);
        this.role=role;
        this.affiliation=affiliation;
        
        String chNick=item.getAttribute("nick");

        setSortKey(nick);
        
        switch (roleCode) {
            case ROLE_MODERATOR:
                transport=RosterIcons.ICON_MODERATOR_INDEX;
                key0=1;
                break;
            case ROLE_VISITOR:
                transport=RosterIcons.getInstance().getTransportIndex("muc#vis");
                key0=3;
                break;
            default:
                transport=0;
                key0=2;
        }

        int rp=from.indexOf('/');
        //String nick=from.substring(rp+1);
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { statusCode=0; }
        

        StringBuffer b=new StringBuffer();
        appendL(b,nick);

        String statusText=presence.getChildBlockText("status");
        
        if (presence.getTypeIndex()==Presence.PRESENCE_OFFLINE) {
            String reason=item.getChildBlockText("reason");
            switch (statusCode) {
                
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    appendL(b,chNick);
                    // исправим jid
                    String newJid=from.substring(0,rp+1)+chNick;
                    //System.out.println(newJid);
                    jid.setJid(newJid);
                    bareJid=newJid; // для запросов vCard используется bareJid
                    from=newJid;
                    nick=chNick;
                    break;
                    
                case 307: //kick
                case 301: //ban
                    b.append(
                            (statusCode==301)? SR.MS_WAS_BANNED : SR.MS_WAS_KICKED );
                    b.append("(");
                    b.append(reason);
                    b.append(")");
                    
                    if (realJid!=null) {
                        b.append(" - ");
                        appendL(b,realJid);
                    }
                    testMeOffline();
                    break;
            
                case 321:
                    b.append(SR.MS_HAS_BEEN_UNAFFILIATED_AND_KICKED_FROM_MEMBERS_ONLY_ROOM);
                    testMeOffline();
                    break;
                case 322:
                    b.append(SR.MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY);
                    testMeOffline();
                    break;
                    
                default:
                b.append(SR.MS_HAS_LEFT_CHANNEL);
                
                if (statusText.length()>0) {
                    b.append(" (");
                    b.append(statusText);
                    b.append(")");
                }
                    
                testMeOffline();
            } 
                
        } else {
            if (this.status==Presence.PRESENCE_OFFLINE) {
                String realJid=item.getAttribute("jid");
                if (realJid!=null) {
                    this.realJid=realJid;  //for moderating purposes
                    b.append(" (");
                    appendL(b, realJid);
                    b.append(')');
                }
                b.append(SR.MS_HAS_JOINED_THE_CHANNEL_AS);
                b.append(role);
                if (!affiliation.equals("none")) {
                    b.append(SR.MS_AND);
                    b.append(affiliation);
                }
                
                if (statusText.length()>0) {
                    b.append(" (");
                    b.append(statusText);
                    b.append(")");
                }
            } else {
                b.append(SR.MS_IS_NOW);
                if ( roleChanged ) b.append(role);
                if (affiliationChanged) {
                    if (roleChanged) b.append(SR.MS_AND);
                    
                    b.append(affiliation.equals("none")? "unaffiliated" : affiliation);
                }
                if (!roleChanged && !affiliationChanged)
                    b.append(presence.getPresenceTxt());
            }
//toon
        }
        
        setStatus(presenceType);
        return b.toString();
    }
    
    public String getTipString() {
        StringBuffer tip=new StringBuffer();
        int nm=getNewMsgsCount();
        
        if (nm!=0) tip.append(nm);
        
        if (realJid!=null) {
            if (tip.length()!=0)  tip.append(' ');
            tip.append(realJid);
        }
        
        return (tip.length()==0)? null:tip.toString();
    }
    
    void testMeOffline(){
        ConferenceGroup group=(ConferenceGroup)getGroup();
        if ( group.getSelfContact() == this ) 
            StaticData.getInstance().roster.roomOffline(group);
    }

    public void addMessage(Msg m) {
        super.addMessage(m);
        switch (m.messageType) {
            case Msg.MESSAGE_TYPE_IN: break;
            case Msg.MESSAGE_TYPE_OUT: break;
            case Msg.MESSAGE_TYPE_HISTORY: break;
            default: return;
        }
        lastMessageTime=m.dateGmt;
    }
}
