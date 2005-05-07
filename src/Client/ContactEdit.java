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
    int ngroups;
    
    //Command cmdIcq=new Command("@icq.jabber.ru",Command.ITEM,1);
    Command cmdOk=new Command("Add", Command.OK, 1);
    Command cmdCancel=new Command("Cancel",Command.BACK,99);
    
    Config cf;
    StoreContact sC;
    
    public ContactEdit(Display display, Vector groups, StoreContact sC, Contact c) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=StaticData.getInstance().config;
        this.sC=sC;
        
        f=new Form("Add contact");
        
        tJid=new TextField("User JID",null, 64, 0); 
        //tJid.addCommand(cmdIcq);
        
        tNick=new TextField("Name",null, 64, 0); 
        tGroup=new TextField("Group",null, 64, 0);
        
        
        tGrpList=new ChoiceGroup(null, Choice.EXCLUSIVE);
        ngroups=0;
        if (groups!=null) {
            ngroups=groups.size();
            for (int i=0;i<ngroups; i++) {
                String gn=(String)groups.elementAt(i);
                tGrpList.append(gn, null);
            }
        }

        int sel=0;
        if (c!=null) {
            String jid=c.jid.getJid();
            f.setTitle(jid);
            // edit contact
            tJid.setString(jid);
            tNick.setString(c.nick);
            sel=c.group-Roster.COMMON_INDEX;
            if (sel==-1) sel=groups.size()-1;
            if (sel<0) sel=0;
            tGroup.setString(group(sel));
            cmdOk=new Command("Update", Command.OK, 1);
        } else f.append(tJid);
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
    
    public interface StoreContact {
        public void StoreContact(String jid, String name, String group);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            String jid=getString(tJid);
            String name=getString(tNick);
            String group=getString(tGroup);
            if (jid!=null) {
                sC.StoreContact(jid,name,group);
                destroyView();
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
    
    public void itemStateChanged(Item item){
        if (item==tGrpList) {
            int index=tGrpList.getSelectedIndex();
            tGroup.setString(group(index));
        }
        if (item==tGroup) {
            tGrpList.setSelectedIndex(ngroups, true);
        };
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
}
