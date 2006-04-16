/*
 * AffiliationModify.java
 *
 * Created on 30 ќкт€брь 2005 г., 15:32
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference.affiliation;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.YesNoAlert;

/**
 *
 * @author EvgS
 */
public class AffiliationModify implements CommandListener{
    
    Display display;
    Displayable parentView;
    
    Form f=new Form("Affiliation");
    TextField jid;
    ChoiceGroup affiliation;
    
    String room;
    int recentAffiliation;
    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdOk=new Command(SR.MS_SET, Command.OK, 1);
    
    /** Creates a new instance of AffiliationModify */
    public AffiliationModify(Display display, String room, String jid, String affiliation) {
        this.display=display;
        parentView=display.getCurrent();
        
        this.room=room;
        this.jid=new TextField(SR.MS_JID /*"Jid"*/ , jid, 80, TextField.URL);
        f.append(this.jid);
        
        this.affiliation=new ChoiceGroup(SR.MS_SET_AFFILIATION /*"Set affiliation to"*/, ui.ConstMIDP.CHOICE_POPUP);
        for (int index=0; index<=AffiliationItem.AFFILIATION_OUTCAST; index++) {
            String name=AffiliationItem.getAffiliationName(index);
            this.affiliation.append(name, null);
            if (affiliation.equals(name)) recentAffiliation=index;
        }
        this.affiliation.setSelectedIndex(recentAffiliation, true);
        f.append(this.affiliation);
        
        f.addCommand(cmdCancel);
        f.addCommand(cmdOk);
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    
    private void modify(){
        JabberStream stream=StaticData.getInstance().roster.theStream;
        
        JabberDataBlock request=new Iq();
        request.setTypeAttribute("set");
        request.setAttribute("to", room);
        request.setAttribute("id", "admin_modify");
        JabberDataBlock query=request.addChild("query", null);
        query.setNameSpace("http://jabber.org/protocol/muc#admin");
        JabberDataBlock child=query.addChild("item", null);
        child.setAttribute("jid", jid.getString());
        child.setAttribute("affiliation", AffiliationItem.getAffiliationName(affiliation.getSelectedIndex()));
        
        
        //processIcon(true);
        //System.out.println(request.toString());
        //stream.addBlockListener(this);
        stream.send(request);
        try {
            Affiliations a=(Affiliations) parentView;
            a.getList();
        } catch (Exception e) {};
        destroyView();
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            if (jid.size()==0) return;
            if (recentAffiliation==AffiliationItem.AFFILIATION_OWNER) {
                StringBuffer warn=new StringBuffer(SR.MS_ARE_YOU_SURE_WANT_TO_DISCARD /*"Are You sure want to discard "*/);
                warn.append(jid.getString());
                warn.append(SR.MS_FROM_OWNER_TO/*" from OWNER to "*/);
                warn.append(AffiliationItem.getAffiliationName(affiliation.getSelectedIndex()));
                new YesNoAlert(display, parentView, SR.MS_MODIFY_AFFILIATION/*"Modify affiliation"*/, warn.toString()) {
                    public void yes() {
                        modify();
                    }
                };
            } else modify();
        }
        if (c==cmdCancel) { destroyView(); }
    }
    
    private void destroyView() { display.setCurrent(parentView); }
}
