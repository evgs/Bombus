/*
 * PrivacySelect.java
 *
 * Created on 26 Август 2005 г., 23:04
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package PrivacyLists;
import Client.Roster;
import javax.microedition.lcdui.*;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacySelect 
        extends VirtualList 
        implements CommandListener,
        JabberBlockListener
{
    private Vector list=new Vector();
    
    private Command cmdCancel=new Command ("Back", Command.BACK, 99);
    private Command cmdActivate=new Command ("Activate", Command.SCREEN, 10);
    private Command cmdDefault=new Command ("Set default", Command.SCREEN, 11);
    /** Creates a new instance of PrivacySelect */
    public PrivacySelect(Display display) {
        super(display);
        createTitleItem(1, "Privacy Lists", null);
        addCommand(cmdActivate);
        addCommand(cmdDefault);
        addCommand(cmdCancel);
        setCommandListener(this);
        list.addElement(new PrivacyList(Roster.IGNORE_GROUP));
        list.addElement(new PrivacyList(null));//none
    }

    protected int getItemCount() { return list.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) list.elementAt(index); }
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) destroyView();
        if (c==cmdActivate || c==cmdDefault) {
            generateIgnoreList();
            PrivacyList active=((PrivacyList)getFocusedObject());
            for (Enumeration e=list.elements(); e.hasMoreElements(); ) {
                PrivacyList pl=(PrivacyList)e.nextElement();
                boolean state=(pl==active);
                if (c==cmdActivate) pl.isActive=state; else pl.isDefault=state;
            }
            ((PrivacyList)getFocusedObject()).activate( (c==cmdActivate)? "active":"default" ); 
            redraw();
        }
    }
    
    public void blockArrived(JabberDataBlock data){
        
    }

    private void generateIgnoreList(){
        JabberDataBlock ignoreList=new JabberDataBlock("list", null, null);
        ignoreList.setAttribute("name", Roster.IGNORE_GROUP);
        JabberDataBlock item=PrivacyItem.itemIgnoreList().constructBlock();
        ignoreList.addChild(item);
        PrivacyList.privacyListRq(true, ignoreList);
    }
}
