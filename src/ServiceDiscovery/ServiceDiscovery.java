/*
 * ServiceDiscovery.java
 *
 * Created on 4 Èþíü 2005 ã., 21:12
 */

package ServiceDiscovery;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import Client.*;

/**
 *
 * @author Evg_S
 */
public class ServiceDiscovery 
        extends VirtualList
        implements CommandListener,
        ServiceDiscoveryListener
{
    private final static String NS_ITEMS="http://jabber.org/protocol/disco#items";
    private final static String NS_INFO="http://jabber.org/protocol/disco#info";
    private final static String NS_REGS="jabber:iq:register";
    private final static String NS_SRCH="jabber:iq:search";
    private final static String NS_MUC="http://jabber.org/protocol/muc";
    
    
    private String strJoin="Join Groupchat";
    private String strReg="Register";
    private String strSrch="Search";
    
    private Command cmdRfsh=new Command("Refresh", Command.SCREEN, 10);
    private Command cmdBack=new Command("Back", Command.BACK, 98);
    private Command cmdCancel=new Command("Cancel", Command.CANCEL, 99);

    private Config cf;
    private StaticData sd=StaticData.getInstance();
    
    private Vector items;
    private Vector stackItems=new Vector();
    
    private Vector cmds;
    
    private String service;

    private boolean blockWait;

    private JabberStream stream;
    
    private class State{
        public String service;
        public Vector items;
        public int cursor;
    }
    
    /** Creates a new instance of ServiceDiscovery */
    public ServiceDiscovery(Display display, JabberStream stream) {
        super();
        setTitleImages(sd.rosterIcons);

        createTitle(2, null, null).addRAlign();
        getTitleLine().addElement(null);
        
        this.display=display;
        this.stream=stream;
        parentView=display.getCurrent();
        display.setCurrent(this);
        sd.roster.discoveryListener=this;
        
        addCommand(cmdRfsh);
        addCommand(cmdCancel);
/*#M55,M55_Release#*///<editor-fold>
//--        addCommand(cmdBack);
/*$M55,M55_Release$*///</editor-fold>
        setCommandListener(this);

        service=sd.account.getServerN();
        
        items=new Vector();
        //cmds=new Vector();
        
        requestQuery(NS_INFO, "disco");
    }
    
    public int getItemCount(){ return items.size();}
    public VirtualElement getItemRef(int index) { return (VirtualElement) items.elementAt(index);}
    
    public void beginPaint(){ getTitleLine().setElementAt(sd.roster.messageIcon,3); }
    
    
    private void titleUpdate(){
        int icon=(blockWait)?ImageList.ICON_RECONNECT_INDEX:0;
        getTitleLine().setElementAt(new Integer(icon), 0);
        getTitleLine().setElementAt(service, 1);
        getTitleLine().setElementAt(sd.roster.messageIcon, 3);
    }
    
    private void requestQuery(String namespace, String id){
        blockWait=true; titleUpdate(); redraw();
        JabberDataBlock req=new Iq(null, null);
        req.setTypeAttribute("get");
        req.setAttribute("to",service);
        req.setAttribute("id",id);
        JabberDataBlock qry=new JabberDataBlock("query",null,null);
        qry.setNameSpace(namespace);
        req.addChild(qry);
        
        stream.send(req);
    }
    
    public void blockArrived(JabberDataBlock data) {
        JabberDataBlock query=data.getChildBlock("query");
        Vector childs=query.getChildBlocks();
        String id=data.getAttribute("id");
        if (id.equals("disco2")) {
            Vector items=new Vector();
            if (childs!=null)
            for (Enumeration e=childs.elements(); e.hasMoreElements(); ){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")){
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    Contact serv=new Contact(name,jid,0,null);
                    items.addElement(serv);
                }
            }
            if (data.getAttribute("from").equals(service)) {
                for (Enumeration e=cmds.elements(); e.hasMoreElements();) 
                    items.insertElementAt(e.nextElement(),0);
                this.items=items;
                moveCursorHome();
                blockWait=false; titleUpdate(); 
            }
        } else if (id.equals("disco")) {
            Vector cmds=new Vector();
            if (childs!=null)
            for (Enumeration e=childs.elements(); e.hasMoreElements();) {
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("feature")) {
                    String var=i.getAttribute("var");
                    if (var.equals(NS_MUC)) { cmds.addElement(new DiscoCommand(0,strJoin)); }
                    if (var.equals(NS_REGS)) { cmds.addElement(new DiscoCommand(1,strReg)); }
                    if (var.equals(NS_SRCH)) { cmds.addElement(new DiscoCommand(2,strSrch)); }
                } 
            }
            if (data.getAttribute("from").equals(service)) {
                this.cmds=cmds;
                requestQuery(NS_ITEMS, "disco2");
            }
        } else if (id.equals ("discoreg")) {
            new RegForm(display, data, stream);
        } else if (id.equals("discoResult")) {
            String text="Successful";
            String title=data.getAttribute("type");
            if (title.equals("error")) {
                text=data.getChildBlock("error").getText();
            }
            Alert alert=new Alert(title, text, null, AlertType.ALARM);
            alert.setTimeout(15*1000);
            display.setCurrent(alert, this);
        }
        redraw();
    }
    
    public void eventOk(){
        super.eventOk();
        Object o= getSelectedObject();
        if (o!=null) 
        if (o instanceof Contact) {
            
            State st=new State();
            st.cursor=cursor;
            st.items=items;
            st.service=service;
            stackItems.addElement(st);
            
            items=new Vector();
            addCommand(cmdBack);
            service=((Contact) o).jid.getJidFull();
            requestQuery(NS_INFO,"disco");
        } 
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdBack){ 
/*#M55,M55_Release#*///<editor-fold>
//--            if (stackItems.isEmpty()) { 
//--                sd.roster.discoveryListener=null; 
//--                destroyView(); 
//--                return;
//--            }
/*$M55,M55_Release$*///</editor-fold>
            
            State st=(State)stackItems.lastElement();
            stackItems.removeElement(st);
            
            service=st.service;
            items=st.items;
            moveCursorTo(st.cursor);
            redraw();
            
/*#!M55,M55_Release#*///<editor-fold>
            if (stackItems.isEmpty()) removeCommand(cmdBack);
/*$!M55,M55_Release$*///</editor-fold>
        }
        if (c==cmdRfsh) {requestQuery(NS_INFO, "disco"); }
        if (c==cmdCancel){ sd.roster.discoveryListener=null; destroyView(); }
    }
    
    private class DiscoCommand extends IconTextElement {
        String name;
        int index;
        
        public DiscoCommand(int index, String name) {
            super(StaticData.getInstance().rosterIcons);
            this.index=index; this.name=name;
        }
        public int getColor(){ return 0x000080; }
        public int getImageIndex() { return ImageList.ICON_COLLAPSED_INDEX; }
        public String toString(){ return name; }
        public void onSelect(){
            switch (index) {
                case 1:
                    requestQuery(NS_REGS, "discoreg");
                    break;
                default:
            }
        }
    }
    
}
