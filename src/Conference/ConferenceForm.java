/*
 * GroupChatForm.java
 *
 * Created on 24 »юль 2005 г., 18:32
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import ui.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class ConferenceForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    Command cmdJoin=new Command("Join", Command.SCREEN, 1);
    Command cmdBookmarks=new Command("Bookmarks", Command.SCREEN, 2);
    Command cmdAdd=new Command("Add bookmark", Command.SCREEN, 3);
    Command cmdCancel=new Command ("Cancel", Command.BACK, 99);
    
    TextField roomField;
    TextField hostField;
    TextField nickField;
    TextField passField;
    
    StaticData sd=StaticData.getInstance();
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display) { this(display, null, null); }
    /** Creates a new instance of GroupChatForm */
    public ConferenceForm(Display display, String room, String server) {
        this.display=display;
        parentView=display.getCurrent();
        
        Form formJoin=new Form("Join conference");

        // Lobo's M55 exception test
        //try {
        if (room==null) room=Config.getInstance().defGcRoom;
        if (server==null) server="conference."+sd.account.getServer();
        
        roomField=new TextField("Room", room, 64, TextField.URL);
        formJoin.append(roomField);
        
        hostField=new TextField("at Host", server, 64, TextField.URL);
        formJoin.append(hostField);
        
        nickField=new TextField("Nickname", sd.account.getNickName(), 32, TextField.ANY);
        formJoin.append(nickField);
        
        passField=new TextField("Password", "", 32, TextField.ANY | ConstMIDP.TEXTFIELD_SENSITIVE );
        formJoin.append(passField);
        
        formJoin.addCommand(cmdJoin);
        formJoin.addCommand(cmdBookmarks);
        formJoin.addCommand(cmdAdd);
        //} catch (Exception e) { formJoin.append(e.toString()); }
        
        formJoin.addCommand(cmdCancel);
        formJoin.setCommandListener(this);
        display.setCurrent(formJoin);
    }
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) { destroyView(); }
        if (c==cmdBookmarks) { new Bookmarks(display, null); }
        if (c==cmdJoin || c==cmdAdd) {
            String nick=nickField.getString();
            String host=hostField.getString();
            String room=roomField.getString();
            String pass=passField.getString();
            if (nick.length()==0) return;
            if (room.length()==0) return;
            if (host.length()==0) return;
            StringBuffer gchat=new StringBuffer(room.trim());
            gchat.append('@');
            gchat.append(host.trim());
            //sd.roster.mucContact(gchat.toString(), Contact.ORIGIN_GROUPCHAT);
            if (c==cmdAdd) new Bookmarks(display, new BookmarkItem(gchat.toString(), nick, pass));
            else {
                gchat.append('/');
                gchat.append(nick.trim());
                join(gchat.toString(),pass);
                
                display.setCurrent(sd.roster);
            }
        }
    }
    public static void join(String name, String pass) {
        StaticData sd=StaticData.getInstance();
        
        sd.roster.mucContact(name, Contact.ORIGIN_GROUPCHAT);
        // требуетс€ дл€ возможности нормального выхода
        sd.roster.mucContact(name, Contact.ORIGIN_GC_MYSELF); 
        //sd.roster.activeRooms.addElement(jid);
 
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        if (pass.length()!=0) {
            // adding password to presence
            x.addChild("password", pass);
        }
        //sd.roster.groups.getGroup(name.substring(0, name.indexOf('@'))).imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
        sd.roster.sendPresence(name, null, x);
        sd.roster.reEnumRoster();
    }
    public void destroyView(){
        if (parentView!=null) display.setCurrent(parentView);
    }
}
