/*
 * InviteForm.java
 *
 * Created on 15 Май 2006 г., 20:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;

import Client.Contact;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Message;
import com.alsutton.jabber.datablocks.Presence;
import java.util.Enumeration;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author root
 */
public class InviteForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    Form form;
    TextField reason;
    ChoiceGroup conferenceList;
    
    Contact contact;
    
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    /** Creates a new instance of InviteForm */
    public InviteForm(Contact contact, Display display) {
        this.display=display;
        this.contact=contact;
        parentView=display.getCurrent();
        
        form=new Form(SR.MS_INVITE);
        reason=new TextField(SR.MS_INVITE_REASON, null, 200, TextField.ANY);
        
        conferenceList=new ChoiceGroup (SR.MS_CONFERENCE, ConstMIDP.CHOICE_POPUP);
        for (Enumeration c=StaticData.getInstance().roster.getHContacts().elements(); c.hasMoreElements(); ) {
            try {
                MucContact mc=(MucContact)c.nextElement();
                if (mc.origin==Contact.ORIGIN_GROUPCHAT && mc.status==Presence.PRESENCE_ONLINE)
                    conferenceList.append(mc.toString(), null);
            } catch (Exception e) {}
        }
        

        form.append(contact.getName());
        form.append("\n");
        form.append(conferenceList);
        form.append(reason);
        
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        form.setCommandListener(this);
        
        display.setCurrent(form);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String room=conferenceList.getString( conferenceList.getSelectedIndex());
            String rs=reason.getString();
            
            Message inviteMsg=new Message(room);
            JabberDataBlock x=inviteMsg.addChild("x",null);
            x.setNameSpace("http://jabber.org/protocol/muc#user");
            JabberDataBlock invite=x.addChild("invite",null);
            invite.setAttribute("to", contact.getBareJid());
            invite.addChild("reason",rs);
            
            //System.out.println(inviteMsg.toString());
            StaticData.getInstance().roster.theStream.send(inviteMsg);
            display.setCurrent(StaticData.getInstance().roster);
        }
        if (c==cmdCancel) { display.setCurrent(parentView); }
    }
    
}
