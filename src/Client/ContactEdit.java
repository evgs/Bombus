/*
 * ContactEdit.java
 *
 * Created on 7 Май 2005 г., 2:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Conference.MucContact;
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public final class ContactEdit
        implements CommandListener, ItemStateListener {
    private Display display;
    public Displayable parentView;
    
    Form f;
    TextField tJid;
    TextField tNick;
    TextField tGroup;
    ChoiceGroup tGrpList;
    ChoiceGroup tTranspList;
    int ngroups;
    
    Command cmdOk=new Command(SR.MS_ADD, Command.OK, 1);
    Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK,99);
    
    boolean newContact=true;
    Config cf;
    Roster roster;
    //StoreContact sC;
    
    public ContactEdit(Display display, Contact c) {
        this.display=display;
        parentView=display.getCurrent();
        
        StaticData sd=StaticData.getInstance();
        roster=sd.roster;
        
        Vector groups=sd.roster.groups.getRosterGroupNames();
        cf=Config.getInstance();
        
        f=new Form(SR.MS_ADD_CONTACT);
        
        tJid=new TextField(SR.MS_USER_JID, null, 64, TextField.EMAILADDR); 
        
        tNick=new TextField(SR.MS_NAME, null, 32, TextField.ANY); 
        tGroup=new TextField(SR.MS_GROUP ,null, 32, TextField.ANY);
        
        
        tGrpList=new ChoiceGroup(SR.MS_EXISTING_GROUPS , ConstMIDP.CHOICE_POPUP);
        tTranspList=new ChoiceGroup(SR.MS_TRANSPORT, ConstMIDP.CHOICE_POPUP);
        
        ngroups=0;
        if (groups!=null) {
            ngroups=groups.size();
            for (int i=0;i<ngroups; i++) {
                String gn=(String)groups.elementAt(i);
                tGrpList.append(gn, null);
            }
        }

        int sel=0;
        
        // Transport droplist
        tTranspList.append(sd.account.getServer(), null);
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=new Jid(ct.getJid()); //TODO: исправить этот хак (отрезание ресурса)
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getBareJid(),null);
        }
        tTranspList.append(SR.MS_OTHER,null);
        
        try {
            String jid;
            if (c instanceof MucContact) {
                jid=Jid.getBareJid( ((MucContact)c).realJid );
            } else {
                jid=c.getBareJid();
            }
            // edit contact
            tJid.setString(jid);
            tNick.setString(c.nick);
            
            if (c instanceof MucContact) {
                c=null;
                throw new Exception();
            } 
            
            sel=c.getGroupIndex()-Groups.COMMON_INDEX;
            if (sel==-1) sel=groups.size()-1;
            if (sel<0) sel=0;
            tGroup.setString(group(sel));
            
            if (c.getGroupIndex()!=Groups.NIL_INDEX  && c.getGroupIndex()!=Groups.SRC_RESULT_INDEX) {
                // edit contact
                f.setTitle(jid);
                cmdOk=new Command(SR.MS_UPDATE, Command.OK, 1);
                newContact=false;
            } else c=null; // adding not-in-list
        } catch (Exception e) {}; // if MucContact does not contains realJid
        
        if (c==null){
            f.append(tJid);
            f.append(tTranspList);
        }
        updateChoise(tJid.getString(),tTranspList);
        f.append(tNick);
        f.append(tGroup);
        
        tGrpList.append(SR.MS_NEWGROUP,null);
        tGrpList.setSelectedIndex(sel, true);
        
        f.append(tGrpList);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    //public interface StoreContact {
    //    public void storeContact(String jid, String name, String group, boolean newContact);
    //}

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String jid=getString(tJid);
            if (jid!=null) {
                // сохранение контакта
                String name=getString(tNick);
                String group=getString(tGroup);
                roster.storeContact(jid,name,group, newContact);
                destroyView();
                return;
            }
        }
        
        if (c==cmdCancel) destroyView();
    }
    
    private String getString(TextField t){
        if (t.size()==0) return null;
        String s=t.getString().trim();
        if (s.length()==0) return null;
        return s;
    }
    
    private String group(int index) {
        if (index==0) return null;
        if (index==tGrpList.size()-1) return null;
        return tGrpList.getString(index);
    }
    
    private void updateChoise(String str, ChoiceGroup grp) {
        int sz=grp.size();
        int set=sz-1;
        for (int i=0; i<sz; i++) {
            if (str.equals(grp.getString(i))) {
                set=i;
                break;
            }
        }
        if (grp.getSelectedIndex()!=set) 
            grp.setSelectedIndex(set, true);
    }
    
    public void itemStateChanged(Item item){
        if (item==tGrpList) {
            int index=tGrpList.getSelectedIndex();
            tGroup.setString(group(index));
        }
        if (item==tGroup) {
            updateChoise(tGroup.getString(), tGrpList);
        }
        if (item==tTranspList) {
            int index=tTranspList.getSelectedIndex();
            if (index==tTranspList.size()-1) return;
            
            String transport=tTranspList.getString(index);
            
            String jid=tJid.getString();
            StringBuffer jidBuf=new StringBuffer(jid);
            
            int at=jid.indexOf('@');
            if (at<0) at=tJid.size();
            
            jidBuf.setLength(at);
            jidBuf.append('@');
            jidBuf.append(transport);
            tJid.setString(jidBuf.toString());
        }
        if (item==tJid) {
            String s1=tJid.getString();
            int at=tJid.getString().indexOf('@');
            try {
                updateChoise(s1.substring(at+1), tTranspList);
            } catch (Exception e) {}
        }
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView/*roster*/);
    }
}
