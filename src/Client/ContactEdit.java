/*
 * ContactEdit.java
 *
 * Created on 7 Май 2005 г., 2:15
 */

package Client;
import javax.microedition.lcdui.*;
import java.util.*;

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
    
    //Command cmdIcq=new Command("@icq.jabber.ru",Command.ITEM,1);
    Command cmdOk=new Command("Add", Command.OK, 1);
    Command cmdCancel=new Command("Cancel",Command.BACK,99);
    
    boolean newContact=true;
    Config cf;
    Roster roster;
    //StoreContact sC;
    
    public ContactEdit(Display display, Contact c) {
        this.display=display;
        parentView=display.getCurrent();
        
        StaticData sd=StaticData.getInstance();
        roster=sd.roster;
        
        Vector groups=sd.roster.groups.getStrings();
        cf=StaticData.getInstance().config;
        
        f=new Form("Add contact");
        
        tJid=new TextField("User JID",null, 64, TextField.EMAILADDR); 
        //tJid.addCommand(cmdIcq);
        
        tNick=new TextField("Name",null, 32, TextField.ANY); 
        tGroup=new TextField("Group",null, 32, TextField.ANY);
        
        
/*#!MIDP1#*///<editor-fold>
        tGrpList=new ChoiceGroup("Existing groups", Choice.POPUP);
        tTranspList=new ChoiceGroup("Transport", Choice.POPUP);
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--        tGrpList=new ChoiceGroup("Existing groups", Choice.EXCLUSIVE);
//--        tTranspList=new ChoiceGroup("Transport", Choice.EXCLUSIVE);
/*$MIDP1$*///</editor-fold>
        
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
        tTranspList.append(sd.account.getServerN(), null);
        for (Enumeration e=sd.roster.getHContacts().elements(); e.hasMoreElements(); ){
            Contact ct=(Contact)e.nextElement();
            Jid transpJid=new Jid(ct.getJid()); //TODO: исправить этот хак (отрезание ресурса)
            if (transpJid.isTransport()) 
                tTranspList.append(transpJid.getJid(),null);
        }
        tTranspList.append("<Other>",null);
        
        if (c!=null) {
            String jid=c.getBareJid();
            // edit contact
            tJid.setString(jid);
            tNick.setString(c.nick);
            sel=c.group-Groups.COMMON_INDEX;
            if (sel==-1) sel=groups.size()-1;
            if (sel<0) sel=0;
            tGroup.setString(group(sel));
            if (c.group!=Groups.NIL_INDEX  && c.group!=Groups.SRC_RESULT_INDEX) {
                // edit contact
                f.setTitle(jid);
                cmdOk=new Command("Update", Command.OK, 1);
                newContact=false;
            } else c=null; // adding not-in-list
        } 
        if (c==null){
            f.append(tJid);
            f.append(tTranspList);
        }
        updateChoise(tJid.getString(),tTranspList);
        f.append(tNick);
        f.append(tGroup);
        
        tGrpList.append("<New Group>",null);
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
            int at=tJid.getString().indexOf('@');
            if (at<0) at=tJid.size();
            tJid.delete(at, tJid.size()-at);
            tJid.insert("@",at);
            tJid.insert(transport, at+1);
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
