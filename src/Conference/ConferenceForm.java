/*
 * ConferenceForm.java
 *
 * Created on 24.06.2005, 18:32
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
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Presence;
import locale.SR;
import ui.*;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;
import ui.controls.TextFieldCombo;

/**
 *
 * @author EvgS
 */
public class ConferenceForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    Command cmdJoin=new Command(SR.MS_JOIN, Command.SCREEN, 1);
    //Command cmdBookmarks=new Command(SR.MS_BOOKMARKS, Command.SCREEN, 2);
    Command cmdAdd=new Command(SR.MS_ADD_BOOKMARK, Command.SCREEN, 5);
    Command cmdAddAuto=new Command(SR.MS_ADD_AUTOJ, Command.SCREEN, 6);
    Command cmdCancel=new Command (SR.MS_CANCEL, Command.BACK, 99);
    
    TextField roomField;
    TextField hostField;
    TextField nickField;
    TextField passField;
    NumberField msgLimitField;
    
    StaticData sd=StaticData.getInstance();
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display) { 
        String room=Config.getInstance().defGcRoom;
        String server=null;
        // trying to split string like room@server
        int roomE=room.indexOf('@');
        if (roomE>0) {
            server=room.substring(roomE+1);
            room=room.substring(0, roomE);
        }
        // default server
        if (server==null) server="conference."+sd.account.getServer();
        createForm(display, room, server, null, null); 
    }
    /** Creates a new instance of GroupChatForm */
    
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String confJid, String password) {
        int roomEnd=confJid.indexOf('@');
        String room="";
        if (roomEnd>0) room=confJid.substring(0, roomEnd);
        String server;
        String nick=null;
        int serverEnd=confJid.indexOf('/');
        if (serverEnd>0) {
            server=confJid.substring(roomEnd+1,serverEnd);
            nick=confJid.substring(serverEnd+1);
        } else {
            server=confJid.substring(roomEnd+1);
        }
        createForm(display, room, server, nick, password);
    }

    public ConferenceForm(Display display, String room, String server, String nick, String password) {
        createForm(display, room, server, nick, password);
    }

    private void createForm(final Display display, String room, String server, String nick, final String password) {
        this.display=display;
        parentView=display.getCurrent();
        
        Form formJoin=new Form(SR.MS_JOIN_CONFERENCE);
        
        roomField=new TextField(SR.MS_ROOM, room, 64, ConstMIDP.TEXTFIELD_URL);
        TextFieldCombo.setLowerCaseLatin(roomField); 
        formJoin.append(roomField);
        
        hostField=new TextFieldCombo(SR.MS_AT_HOST, server, 64, ConstMIDP.TEXTFIELD_URL, "muc-host", display);
        TextFieldCombo.setLowerCaseLatin(hostField); 
        formJoin.append(hostField);
        
        if (nick==null) nick=sd.account.getNickName();
        nickField=new TextFieldCombo(SR.MS_NICKNAME, nick, 32, TextField.ANY, "roomnick", display);
        formJoin.append(nickField);
        
        passField=new TextField(SR.MS_PASSWORD, password, 32, TextField.ANY | ConstMIDP.TEXTFIELD_SENSITIVE );
        formJoin.append(passField);
        
        msgLimitField=new NumberField(SR.MS_MSG_LIMIT, 20, 0, 100);
        formJoin.append(msgLimitField);
        
        formJoin.addCommand(cmdJoin);
        //formJoin.addCommand(cmdBookmarks);
        formJoin.addCommand(cmdAdd);
        formJoin.addCommand(cmdAddAuto);
        
        formJoin.addCommand(cmdCancel);
        formJoin.setCommandListener(this);
        
        display.setCurrent(formJoin);
    }
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) { destroyView(); }
        //if (c==cmdBookmarks) { new Bookmarks(display, null); }
        if (c==cmdJoin || c==cmdAdd || c==cmdAddAuto) {
            String nick=nickField.getString().trim();
            String host=hostField.getString().trim();
            String room=roomField.getString().trim();
            String pass=passField.getString();
            int msgLimit=msgLimitField.getValue();
            
            if (nick.length()==0) return;
            if (room.length()==0) return;
            if (host.length()==0) return;
            StringBuffer gchat=new StringBuffer(room.trim());
            gchat.append('@');
            gchat.append(host.trim());
            //sd.roster.mucContact(gchat.toString(), Contact.ORIGIN_GROUPCHAT);
            if (c==cmdAdd) new Bookmarks(display, new BookmarkItem(gchat.toString(), nick, pass, false));
            if (c==cmdAddAuto) new Bookmarks(display, new BookmarkItem(gchat.toString(), nick, pass, true));
            if (c==cmdJoin) {
                try {
                    gchat.append('/');
                    gchat.append(nick);
                    join(gchat.toString(),pass, msgLimit);
                    
                    display.setCurrent(sd.roster);
                } catch (Exception e) {
                    e.printStackTrace();
                    //display.setCurrent(new Alert("Exception", e.toString(), null, AlertType.ERROR), sd.roster);
                }
            }
        }
    }
    
    public static void join(String name, String pass, int maxStanzas) {
        StaticData sd=StaticData.getInstance();
        
        
        
        ConferenceGroup grp=sd.roster.initMuc(name, pass);
        // требуется для возможности нормального выхода
        //sd.roster.mucContact(name, Contact.ORIGIN_GC_MYSELF); 
        //sd.roster.activeRooms.addElement(jid);
 
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            // adding password to presence
            x.addChild("password", pass);
        }
        
        JabberDataBlock history=x.addChild("history", null);
        history.setAttribute("maxstanzas", String.valueOf(maxStanzas));
        history.setAttribute("maxchars","32768");
        try {
            long last=grp.getConference().lastMessageTime;
            long delay= ( grp.conferenceJoinTime - last ) /1000 ;
            if (last!=0) history.setAttribute("seconds",String.valueOf(delay)); // todo: change to since
        } catch (Exception e) {};

        //sd.roster.groups.getGroup(name.substring(0, name.indexOf('@'))).imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
        //sd.roster.sendPresence(name, null, x);
        int status=StaticData.getInstance().roster.myStatus;
        if (status==Presence.PRESENCE_INVISIBLE) status=Presence.PRESENCE_ONLINE;
        sd.roster.sendDirectPresence(status, name, x);
        sd.roster.reEnumRoster();
    }
    public void destroyView(){
        if (parentView!=null) display.setCurrent(parentView);
    }
}
