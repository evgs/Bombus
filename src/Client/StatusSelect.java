/*
 * SelectStatus.java
 *
 * Created on 27 Февраль 2005 г., 16:43
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import images.RosterIcons;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.*;

/**
 *
 * @author Eugene Stahov
 */
public class StatusSelect extends VirtualList implements CommandListener, Runnable{
    
    private Command cmdOk=new Command("Select",Command.OK,1);
    private Command cmdEdit=new Command("Edit",Command.SCREEN,2);
    //private Command cmdPriority=new Command("Set Priority",Command.SCREEN,2);
    //private Command cmdAll=new Command("All Priorities",Command.SCREEN,3);
    private Command cmdCancel=new Command("Back",Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    private Vector statusList=StatusList.getInstance().statusList;
    
    public StatusSelect(Display d) {
        super();
        setTitleImages(RosterIcons.getInstance());
        createTitleItem(1, "Status",null);
        
        addCommand(cmdOk);
        addCommand(cmdEdit);
        //addCommand(cmdPriority);
        //addCommand(cmdAll);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        /*
        int status=StaticData.getInstance().roster.myStatus;
        int i;
        for (i=0;i<statusList.size(); i++) {
            if (status==((ExtendedStatus)getItemRef(i)).getImageIndex()) break;
        }
        moveCursorTo(i);
        */
        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)statusList.elementAt(Index);
    }
    
    private ExtendedStatus getSel(){ return (ExtendedStatus)getFocusedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdEdit) {
            new StatusForm( display, getSel() );
        };
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        destroyView();
        new Thread(this).start();
    }
    
    public void run(){
        int status=getSel().getImageIndex();
        try {
            StaticData.getInstance().roster.sendPresence(status);
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public int getItemCount(){   return StatusList.getInstance().statusList.size(); }
    
    private void save(){
        StatusList.getInstance().saveStatusToStorage();
    }

    class StatusForm implements CommandListener{
        private Display display;
        public Displayable parentView;
        
        private Form f;
        private TextField tfPriority;
        private TextField tfMessage;
        
        private ChoiceGroup chPriorityAll;
        
        private ExtendedStatus status;
        
        private Command cmdOk=new Command("Ok",Command.OK,1);
        private Command cmdCancel=new Command("Cancel",Command.BACK,99);
        
        public StatusForm(Display display, ExtendedStatus status){
            this.display=display;
            parentView=display.getCurrent();
            this.status=status;
            
            f=new Form(status.getName());
            
            tfPriority=new TextField(
                    "Priority", 
                    String.valueOf(status.getPriority()), 
                    3, TextField.NUMERIC);
            f.append(tfPriority);

            chPriorityAll=new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
            chPriorityAll.append("for all status types", null);
            f.append(chPriorityAll);
            
            tfMessage=new TextField("Message", status.getMessage(), 50, 0);
            f.append(tfMessage);
            
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            display.setCurrent(f);
        }
        
        public void commandAction(Command c, Displayable d){
            if (c==cmdOk) {
                status.setMessage(tfMessage.getString());
                
                int priority=0;
                try { 
                    priority=Integer.parseInt(tfPriority.getString()); 
                } catch (Exception x) {};
                
                if (priority<0) priority=0;
                if (priority>99) priority=99;
                status.setPriority(priority);
                
                boolean flags[]=new boolean[1];
                chPriorityAll.getSelectedFlags(flags);
                if (flags[0]) {
                    for (Enumeration e=StatusList.getInstance().statusList.elements(); e.hasMoreElements();) {
                        ((ExtendedStatus)e.nextElement()).setPriority(priority);
                    }
                }
                
                save();
                destroyView();
            }
            if (c==cmdCancel) {  destroyView();  }
        }
        
        public void destroyView(){
            if (display!=null)   display.setCurrent(parentView);
        }
    }
    
}

