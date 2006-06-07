/*
 * SearchResult.java
 *
 * Created on 10 Июль 2005 г., 21:40
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;
import images.RosterIcons;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;

/**
 *
 * @author EvgS
 */
public class SearchResult
        extends VirtualList
        implements CommandListener {
    
    StaticData sd=StaticData.getInstance();
    private Command cmdBack=new Command("Back", Command.BACK, 98);
    private Command cmdAdd=new Command("Add", Command.SCREEN, 1);
    
    private Vector items;
    boolean xData;
    
    /** Creates a new instance of SearchResult */
    public SearchResult(Display display, JabberDataBlock result) {
        super(display);
        
        String service=result.getAttribute("from");
        
        setTitleItem(new Title(2, null, service));
        
        addCommand(cmdBack);
        setCommandListener(this);
        
        items=new Vector();
        
        JabberDataBlock query=result.getChildBlock("query");
        if (query==null) return;
        
        JabberDataBlock x=query.getChildBlock("x");
        if (x!=null) { query=x; xData=true; }
        
        sd.roster.cleanupSearch();
        
        for (Enumeration e=query.getChildBlocks().elements(); e.hasMoreElements(); ){
            JabberDataBlock child=(JabberDataBlock) e.nextElement();
	    
            if (child.getTagName().equals("item")) {
                StringBuffer vcard=new StringBuffer();
                String jid=null;
		
	        int status=Presence.PRESENCE_ONLINE;

                // Form vcard=new Form(null);
                if (!xData) { jid=child.getAttribute("jid"); }
                // пїЅпїЅпїЅпїЅ item
                for (Enumeration f=child.getChildBlocks().elements(); f.hasMoreElements(); ){
                    JabberDataBlock field=(JabberDataBlock) f.nextElement();
                    String name;
                    String value;
                    if (xData) {
                        name=field.getAttribute("var");
                        value=field.getChildBlockText("value");
                    } else {
                        name=field.getTagName();
                        value=field.getText();
                    }
                    if (name.equals("jid")) jid=value;
                    if (value.length()>0)
                    {
                        //vcard.append(new StringItem(name,value+"\n"));
                        vcard.append(name);
                        vcard.append((char)0xa0);
                        vcard.append(value);
                        vcard.append((char)'\n');
                    }
		    // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ jit
		    if (name.equals("status")) if (value.equals("offline")) status=Presence.PRESENCE_OFFLINE;
                }
                Contact serv=new DiscoContact(null, jid, status);
                serv.setGroup(sd.roster.groups.getGroup(Groups.SRC_RESULT_INDEX));
                Msg m=new Msg(Msg.MESSAGE_TYPE_IN, jid, "Short info", vcard.toString());
                m.unread=false;
                serv.addMessage(m);
                
                items.addElement(serv);
                sd.roster.addContact(serv);
            }
        }
        sd.roster.reEnumRoster();
        addCommand(cmdAdd);
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}

    public void commandAction(Command c, Displayable d){
        if (c==cmdAdd){
            destroyView();
            new ContactEdit(display, (Contact)getFocusedObject());
            return;
        }
        
        if (c==cmdBack) destroyView(); 
    }
    
    public void eventOk(){
        /*Form f=(Form)vcards.elementAt(cursor);
        
        display.setCurrent(f);
        f.setCommandListener(this);
        f.addCommand(cmdBack);
        f.addCommand(cmdAdd);*/
        new ContactMessageList((Contact) getFocusedObject(), display);
    }
}
