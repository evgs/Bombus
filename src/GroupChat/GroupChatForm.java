/*
 * GroupChatForm.java
 *
 * Created on 24 »юль 2005 г., 18:32
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package GroupChat;
import Client.*;
import com.alsutton.jabber.JabberDataBlock;
import ui.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class GroupChatForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    StaticData sd=StaticData.getInstance();
    
    Command cmdJoin=new Command("Join", Command.OK, 1);
    Command cmdCancel=new Command ("Cancel", Command.BACK, 99);
    
    TextField roomField;
    TextField hostField;
    TextField nickField;
    
    /** Creates a new instance of GroupChatForm */
    public GroupChatForm(Display display) { this(display, null, null); }
    /** Creates a new instance of GroupChatForm */
    public GroupChatForm(Display display, String room, String server) {
        this.display=display;
        parentView=display.getCurrent();
        
        Form formJoin=new Form("Join conference");

        // Lobo's M55 exception test
        try {
            if (room==null) room=sd.config.defGcRoom;
            if (server==null) server="conference."+sd.account.getServerN();
            
            roomField=new TextField("Room", room, 64, TextField.URL);
            formJoin.append(roomField);
            
            hostField=new TextField("at Host", server, 64, TextField.URL);
            formJoin.append(hostField);
            
            nickField=new TextField("Nickname", sd.account.getNickName(), 32, TextField.URL);
            formJoin.append(nickField);
            
            formJoin.addCommand(cmdJoin);
        } catch (Exception e) { formJoin.append(e.toString()); }
        
        formJoin.addCommand(cmdCancel);
        formJoin.setCommandListener(this);
        display.setCurrent(formJoin);
    }
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) { destroyView(); }
        if (c!=cmdJoin) return;
        String nick=nickField.getString();
        String host=hostField.getString();
        String room=roomField.getString();
        if (nick.length()==0) return;
        if (room.length()==0) return;
        if (host.length()==0) return;
        StringBuffer gchat=new StringBuffer(room);
        gchat.append('@');
        gchat.append(host);
        //sd.roster.mucContact(gchat.toString(), Contact.ORIGIN_GROUPCHAT);
        gchat.append('/');
        gchat.append(nick);
        String jid=gchat.toString();
        sd.roster.mucContact(jid, Contact.ORIGIN_GROUPCHAT);
        // требуетс€ дл€ возможности нормального выхода
        sd.roster.mucContact(jid, Contact.ORIGIN_GC_MYSELF); 
        //sd.roster.activeRooms.addElement(jid);
 
        JabberDataBlock x=new JabberDataBlock("x", null, null);
        x.setNameSpace("http://jabber.org/protocol/muc");
        sd.roster.groups.getGroup(room).imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
        sd.roster.sendPresence(jid, null, x);
        display.setCurrent(sd.roster);
    }
    public void destroyView(){
        if (parentView!=null) display.setCurrent(parentView);
    }
}
