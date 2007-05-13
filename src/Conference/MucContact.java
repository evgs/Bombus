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
    
    public String processPresence(JabberDataBlock xmuc, Presence presence) {
        //System.out.println(presence);
        String from=jid.getJid();
        
        int presenceType=presence.getTypeIndex();
        
        if (presenceType==Presence.PRESENCE_ERROR) {
            JabberDataBlock error=presence.getChildBlock("error");
            String mucErrCode=error.getAttribute("code");
            int errCode=0;
            try {
                errCode=Integer.parseInt(mucErrCode);
            } catch (Exception e) { return "Unsupported MUC error"; }
            ConferenceGroup grp=(ConferenceGroup)getGroup();
            if (status>=Presence.PRESENCE_OFFLINE) testMeOffline();
            if (errCode!=409 || status>=Presence.PRESENCE_OFFLINE)
                setStatus(presenceType);
            
            String errText=error.getChildBlockText("text");
            if (errText.length()>0) return errText; // if error description is provided by server
            
            // legacy codes
            switch (errCode) {
                case 401: return "Password required";
                case 403: return "You are banned in this room";
                case 404: return "Room does not exists";
                case 405: return "You can't create room on this server";
                case 406: return "Reserved roomnick must be used";
                case 407: return "This room is members-only";
                case 409: return "Nickname is already in use by another occupant";
                case 503: return "Maximum number of users has been reached in this room";
                default: return error.toString();
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
        
        if (role.equals("moderator")) {
            transport=RosterIcons.ICON_MODERATOR_INDEX;
            key0=1;
            //jidHash &= 0x3fffffff;
        } else {
            transport=0;
            key0=2;
            //jidHash |= 0x40000000;
        }


        int rp=from.indexOf('/');
        //String nick=from.substring(rp+1);
        
        JabberDataBlock statusBlock=xmuc.getChildBlock("status");
        int statusCode;
        try { 
            statusCode=Integer.parseInt( statusBlock.getAttribute("code") ); 
        } catch (Exception e) { statusCode=0; }
        

        StringBuffer b=new StringBuffer(nick);

        String statusText=presence.getChildBlockText("status");
        
        if (presence.getTypeIndex()==Presence.PRESENCE_OFFLINE) {
            String reason=item.getChildBlockText("reason");
            switch (statusCode) {
                
                case 303:
                    b.append(SR.MS_IS_NOW_KNOWN_AS);
                    b.append(chNick);
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
                        b.append(realJid);
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
                    b.append(realJid);
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
                    if (roleChanged) b.append(" and ");
                    
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
