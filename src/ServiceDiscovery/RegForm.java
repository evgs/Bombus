/*
 * RegForm.java
 *
 * Created on 5 »юнь 2005 г., 20:04
 */

package ServiceDiscovery;
import java.util.*;
import javax.microedition.lcdui.*;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
//import Client.*;


/**
 *
 * @author Evg_S
 */
public class RegForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Vector fields;
    private String xmlns;
    private String service;
    
    private Form form;
    
    private Command cmdOk=new Command("Send", Command.OK, 1);
    private Command cmdCancel=new Command("Cancel", Command.BACK, 99);
    
    //Roster roster=StaticData.getInstance().roster;
    JabberStream stream;
    
    /** Creates a new instance of RegForm */
    public RegForm(Display display, JabberDataBlock regform, JabberStream stream) {
        service=regform.getAttribute("from");
        JabberDataBlock query=regform.getChildBlock("query");
        xmlns=query.getAttribute("xmlns");
        // todo: обработать ошибку query
        fields=new Vector();
        
        Form form=new Form(service);
        
        Vector vFields=query.getChildBlocks();
        for (Enumeration e=vFields.elements(); e.hasMoreElements(); ){
            FormField field=new FormField((JabberDataBlock)e.nextElement());
            fields.addElement(field);
            if (!field.hidden)    form.append(field.formItem);
        }
        
        form.setCommandListener(this);
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        
        this.display=display;
        this.parentView=display.getCurrent();
        this.stream=stream;
        display.setCurrent(form);
    }
    
    private void sendForm(String id){
        JabberDataBlock req=new Iq(null, null);
        req.setTypeAttribute("set");
        req.setAttribute("to",service);
        req.setAttribute("id",id);
        JabberDataBlock qry=new JabberDataBlock("query",null,null);
        qry.setNameSpace(xmlns);
        req.addChild(qry);
        
        for (Enumeration e=fields.elements(); e.hasMoreElements(); ) {
            JabberDataBlock ch=((FormField) e.nextElement()).getJabberDataBlock();
            if (ch!=null) qry.addChild(ch);
        }
        System.out.println(req.toString());
        stream.send(req);
    }

    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) destroyView();
        if (c==cmdOk) { 
            sendForm("discoResult");
            destroyView();
        }
    }

    public void destroyView(){
        display.setCurrent(parentView);
    }
}
