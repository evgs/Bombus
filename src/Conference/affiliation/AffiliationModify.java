/*
 * AffiliationModify.java
 *
 * Created on 30.11.2005, 15:32
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

package Conference.affiliation;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.JabberStream;
import com.alsutton.jabber.datablocks.Iq;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.YesNoAlert;
import ui.controls.TextFieldCombo;

/**
 *
 * @author EvgS
 */
public class AffiliationModify implements CommandListener, YesNoAlert.YesNoListener{
    
    Display display;
    Displayable parentView;
    
    Form f=new Form("Affiliation");
    TextField jid;
    TextFieldCombo reason;
    ChoiceGroup affiliation;
    
    String room;
    int recentAffiliation;
    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdOk=new Command(SR.MS_SET, Command.OK, 1);
    
    /** Creates a new instance of AffiliationModify */
    public AffiliationModify(Display display, String room, String jid, String affiliation, String reason) {
        this.display=display;
        parentView=display.getCurrent();
        
        this.room=room;
        this.jid=new TextField(SR.MS_JID /*"Jid"*/ , jid, 80, TextField.URL);
        this.reason=new TextFieldCombo("Reason", reason, 256, TextField.ANY, "reason", display);
        f.append(this.jid);
        
        this.affiliation=new ChoiceGroup(SR.MS_SET_AFFILIATION /*"Set affiliation to"*/, ui.ConstMIDP.CHOICE_POPUP);
        for (int index=0; index<=AffiliationItem.AFFILIATION_OUTCAST; index++) {
            String name=AffiliationItem.getAffiliationName(index);
            this.affiliation.append(name, null);
            if (affiliation.equals(name)) recentAffiliation=index;
        }
        this.affiliation.setSelectedIndex(recentAffiliation, true);
        f.append(this.affiliation);
        
        f.append(this.reason);
        
        f.addCommand(cmdCancel);
        f.addCommand(cmdOk);
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    
    private void modify(){
        JabberStream stream=StaticData.getInstance().roster.theStream;
        
        JabberDataBlock request=new Iq(room, Iq.TYPE_SET, "admin_modify");
        JabberDataBlock query=request.addChild("query", null);
        query.setNameSpace("http://jabber.org/protocol/muc#admin");
        JabberDataBlock child=query.addChild("item", null);
        child.setAttribute("jid", jid.getString());
        child.setAttribute("affiliation", AffiliationItem.getAffiliationName(affiliation.getSelectedIndex()));
        
        String rs=reason.getString();
        if (rs.length()>0) child.addChild("reason", rs);
        
        //processIcon(true);
        //System.out.println(request.toString());
        //stream.addBlockListener(this);
        stream.send(request);
        try {
            Thread.sleep(300);
        } catch (Exception ex) {}
        
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
                new YesNoAlert(display, SR.MS_MODIFY_AFFILIATION/*"Modify affiliation"*/, warn.toString(), this);
            } else modify();
        }
        if (c==cmdCancel) { destroyView(); }
    }
    
    public void ActionConfirmed() {
        modify();
        destroyView();
    }

    private void destroyView() { display.setCurrent(parentView); }
}
