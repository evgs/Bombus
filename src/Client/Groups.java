/*
 * Groups.java
 *
 * Created on 8 Май 2005 г., 0:36
 */

package Client;
import java.util.*;

/**
 *
 * @author Evg_S
 */
public class Groups{
    
    Vector g;
    public Groups(){
        g=new Vector();
        addGroup(Roster.TRANSP_GROUP);
        addGroup(Roster.SELF_GROUP);
        addGroup(Roster.SRC_RESULT_GROUP);
        addGroup(Roster.NIL_GROUP);
        addGroup(Roster.IGNORE_GROUP);
        addGroup(Roster.COMMON_GROUP);
    }
    
    public void resetCounters(){
        for (Enumeration e=g.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
            grp.tncontacts=grp.tonlines=0;
            grp.Contacts=new Vector();
        }
    }
    
    public void addToVector(Vector d, int index){
        Group gr=getGroup(index);
        if (gr.Contacts.size()>0){
            d.addElement(gr);
            if (!gr.collapsed) for (Enumeration e=gr.Contacts.elements();e.hasMoreElements();){
                d.addElement(e.nextElement());
            }
        }
        gr.onlines=gr.tonlines;
        gr.ncontacts=gr.tncontacts;
        gr.Contacts=null;
    }

    public Group getGroup(int Index) {
        return (Group)g.elementAt(Index);
    }
    
    public Group getGroup(String Name) {
        for (Enumeration e=g.elements();e.hasMoreElements();){
            Group grp=(Group)e.nextElement();
            if (Name.equals(grp.name)) return grp;
        }
        return null;
    }
    public Group addGroup(String name) {
        Group grp=new Group(g.size(),name);
        g.addElement(grp);
        return grp;
    }
    public Vector getStrings(){
        Vector s=new Vector();
        for (int i=Roster.COMMON_INDEX; i<g.size(); i++) {
            s.addElement(((Group)g.elementAt(i)).name);
        }
        s.addElement(Roster.IGNORE_GROUP);
        return s;
    }
    public int getCount() {return g.size();}
    
}
