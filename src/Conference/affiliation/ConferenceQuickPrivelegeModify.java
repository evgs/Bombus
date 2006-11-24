/*
 * ConferenceQuickPrivelegeModify.java
 *
 * Created on 12 Ноябрь 2006 г., 19:02
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference.affiliation;

import Client.Roster;
import Client.StaticData;
import Conference.*;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.Hashtable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.controls.TextFieldCombo;

/**
 *
 * @author Evg_S
 */
public class ConferenceQuickPrivelegeModify implements CommandListener{
    public final static int KICK=1;
    public final static int VISITOR=2;
    public final static int PARTICIPANT=3;
    public final static int MODERATOR=4;
    
    public final static int OUTCAST=5;
    public final static int NONE=6;
    public final static int MEMBER=7;
    public final static int ADMIN=8;
    public final static int OWNER=9;

    private Display display;
    private Form f;
    private TextFieldCombo reason;
    private MucContact victim;
    
    private Command cmdOk;
    private Command cmdNoReason=new Command(SR.MS_NO_REASON, Command.SCREEN, 2);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    
    private int action;

    /**
     * Creates a new instance of ConferenceQuickPrivelegeModify
     */
    public ConferenceQuickPrivelegeModify(Display display, MucContact victim, int action) {

        this.victim=victim;
        this.action=action;
        
        switch (action) {
            case KICK: 
                f=new Form(SR.MS_KICK);
                break;

            case OUTCAST:
                f=new Form(SR.MS_BAN);
                f.append(SR.MS_CONFIRM_BAN);
                break;
                
        } // switch
        
        if (f==null) {
            setMucMod();
            return;
        }
        
        this.display=display;
        
        StringBuffer user=new StringBuffer(victim.nick);
        if (victim.jid!=null) {
            user.append(" (");
            user.append(victim.realJid);
            user.append(")");
        }
        f.append(new StringItem("User",user.toString()));
        
        reason=new TextFieldCombo("Reason", "", 64, TextField.ANY, "reason", display);
        f.append(reason);
        
        cmdOk=new Command( (action==KICK)? SR.MS_KICK : SR.MS_BAN, Command.SCREEN, 1);
        f.addCommand(cmdOk);
        f.addCommand(cmdNoReason);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdNoReason) { 
            reason.setString("");
            return;
        }
        
        if (command==cmdOk) setMucMod(); 
        display.setCurrent(StaticData.getInstance().roster);
    }
    
    private void setMucMod(){
        JabberDataBlock iq=new Iq(victim.jid.getBareJid(), Iq.TYPE_SET, "itemmuc");
        JabberDataBlock query=iq.addChild("query", null);
        query.setNameSpace("http://jabber.org/protocol/muc#admin"); 
        //TODO: separate usecases to muc#owner, muc#admin and muc#moderator
        JabberDataBlock item=new JabberDataBlock("item", null, null);
        query.addChild(item);

        try {
            String rzn=reason.getString();
            if (rzn.length()!=0) item.addChild("reason", rzn);
        } catch (Exception e) {}
        
        switch (action) {
            case KICK: 
                item.setAttribute("role", "none");
                item.setAttribute("nick", victim.nick);
                break;

            case OUTCAST:
                item.setAttribute("affiliation", "outcast");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case PARTICIPANT:
                item.setAttribute("role", "participant");
                item.setAttribute("nick", victim.nick);
                break;
                
            case VISITOR:
                item.setAttribute("role", "visitor");
                item.setAttribute("nick", victim.nick);
                break;
                
            case MODERATOR:
                item.setAttribute("role", "moderator");
                item.setAttribute("nick", victim.nick);
                break;
                
            case MEMBER:
                item.setAttribute("affiliation", "member");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case NONE:
                item.setAttribute("affiliation", "none");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case ADMIN:
                item.setAttribute("affiliation", "admin");
                item.setAttribute("jid", victim.realJid);
                break;
                
            case OWNER:
                item.setAttribute("affiliation", "owner");
                item.setAttribute("jid", victim.realJid);

        }
        //System.out.println(iq);
        StaticData.getInstance().roster.theStream.send(iq);
    }
}
