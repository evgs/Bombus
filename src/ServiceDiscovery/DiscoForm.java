/*
 * RegForm.java
 *
 * Created on 5 Июнь 2005 г., 20:04
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
public class DiscoForm implements CommandListener{
    
    private Display display;
    private Displayable parentView;
    
    private Vector fields;
    private String xmlns;
    private String service;
    
    private String node;
    private String sessionId;
    
    private String childName;
    
    //private Form form;
    
    private boolean xData;
    
    private Command cmdOk=new Command("Send", Command.OK /*Command.SCREEN*/, 1);
    private Command cmdCancel=new Command("Cancel", Command.BACK, 99);
    
    private String id;
    
    //Roster roster=StaticData.getInstance().roster;
    JabberStream stream;
    
    //private JabberBlockListener listener;
    
    /** Creates a new instance of RegForm */
    public DiscoForm(Display display, JabberDataBlock regform, JabberStream stream, String resultId, String childName) {
        service=regform.getAttribute("from");
        this.childName=childName;
        JabberDataBlock query=regform.getChildBlock(childName);
        xmlns=query.getAttribute("xmlns");
        node=query.getAttribute("node");
        sessionId=query.getAttribute("sessionid");
        JabberDataBlock x=query.getChildBlock("x");
        this.id=resultId;
        //this.listener=listener;
        // todo: обработать ошибку query
        fields=new Vector();
        Form form=new Form(service);

        // for instructions
        
        Vector vFields=(xData=(x!=null))? x.getChildBlocks() : query.getChildBlocks();

	Enumeration e;        
        
        if (vFields!=null) {
            for (e=vFields.elements(); e.hasMoreElements(); ){
                FormField field=new FormField((JabberDataBlock)e.nextElement());
                if (field.instructions) {
                    fields.insertElementAt(field, 0);
                } else { fields.addElement(field); }
            }

            if (x!=null) {
                JabberDataBlock registered=query.getChildBlock("registered");
                if (registered!=null) {
                    FormField unreg=new FormField(registered);
                    fields.addElement(unreg);
                }
            }
            
            for (e=fields.elements(); e.hasMoreElements(); ){
                FormField field=(FormField) e.nextElement();
                if (!field.hidden) form.append(field.formItem);
            }
        }
        
       
        form.setCommandListener(this);
        
        if (childName.equals("command")) {
            if (query.getAttribute("status").equals("completed")) {
                form.append("Complete.");
            } else form.addCommand(cmdOk);
        } else form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        
        this.display=display;
        this.parentView=display.getCurrent();
        this.stream=stream;
        display.setCurrent(form);
    }
    
    private void sendForm(String id){
        JabberDataBlock req=new Iq(service, Iq.TYPE_SET, id);
        JabberDataBlock qry=req.addChild(childName,null);
        qry.setNameSpace(xmlns);
        //qry.setAttribute("action", "complete");
        qry.setAttribute("node", node);
        qry.setAttribute("sessionid", sessionId);
        
        if (xData) {
            JabberDataBlock x=qry.addChild("x", null);
            x.setNameSpace("jabber:x:data");
            x.setAttribute("type", "submit");
            qry=x;
        }
        
        for (Enumeration e=fields.elements(); e.hasMoreElements(); ) {
            FormField f=(FormField) e.nextElement();
            if (f==null) continue;
            JabberDataBlock ch=f.constructJabberDataBlock();
            if (ch!=null) {
                if (ch.getTagName().equals("remove")) {
                    qry.getChildBlocks().removeAllElements();
                }
                qry.addChild(ch);
            }
        }
        
        //System.out.println(req.toString());
        //if (listener!=null) stream.addBlockListener(listener);
        stream.send(req);
    }

    
    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) destroyView();
        if (c==cmdOk) { 
            sendForm(id);
            destroyView();
        }
    }

    public void destroyView(){
        display.setCurrent(parentView);
    }
}
