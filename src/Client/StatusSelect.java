/*
 * SelectStatus.java
 *
 * Created on 27 Февраль 2005 г., 16:43
 */

package Client;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.*;

/**
 *
 * @author Eugene Stahov
 */
public class StatusSelect extends VirtualList implements CommandListener{
    
    private Command cmdOk=new Command("Select",Command.OK,1);
    private Command cmdMsg=new Command("Set Message",Command.SCREEN,1);
    private Command cmdPriority=new Command("Set Priority",Command.SCREEN,2);
    private Command cmdAll=new Command("All Priorities",Command.SCREEN,3);
    private Command cmdCancel=new Command("Back",Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    public StatusSelect(Display d) {
        super();
        setTitleImages(StaticData.getInstance().rosterIcons);
        createTitle(1, "Status",null);
        
        addCommand(cmdOk);
        addCommand(cmdMsg);
        addCommand(cmdPriority);
        addCommand(cmdAll);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        attachDisplay(d);
    }
    public VirtualElement getItemRef(int Index){
        return (VirtualElement)StaticData.getInstance().statusList.elementAt(Index);
    }
    
    ExtendedStatus getSel(){ return (ExtendedStatus)getSelectedObject();}
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdMsg) {
            new MIDPTextBox(
                    display, 
                    "Status Message", 
                    getSel().getMessage(), 
                    new MsgListener() );
        };
        if (c==cmdPriority) {
            new MIDPTextBox(
                    display, 
                    "Priority", 
                    String.valueOf(getSel().getPriority() ), 
                    new PriorityListener() );
        };
        if (c==cmdAll) {
            new MIDPTextBox(
                    display, 
                    "All Priorities", 
                    String.valueOf(getSel().getPriority() ), 
                    new PriorityAll() );
        };
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        int status=getSel().getImageIndex();
        try {
            StaticData.getInstance().roster.sendPresence(status);
        } catch (Exception e) { e.printStackTrace(); }
        destroyView();
    }
    
    public int getItemCount(){   return StaticData.getInstance().statusList.size(); }
    
    private class MsgListener implements MIDPTextBox.TextBoxNotify{
        public void OkNotify(String text_return){
            getSel().setMessage(text_return);
            save();
        }
    }
    
    private class PriorityListener implements MIDPTextBox.TextBoxNotify{
        public void OkNotify(String text_return){
            int priority=0;
            try { priority=Integer.parseInt(text_return); }
            catch (Exception e) {};
            if (priority<0) priority=0;
            if (priority>99) priority=99;
            getSel().setPriority(priority);
            save();
        }
    }
    private class PriorityAll implements MIDPTextBox.TextBoxNotify{
        public void OkNotify(String text_return){
            int priority=0;
            try { priority=Integer.parseInt(text_return); }
            catch (Exception x) {};
            if (priority<0) priority=0;
            if (priority>99) priority=99;
            for (Enumeration e=StaticData.getInstance().statusList.elements(); e.hasMoreElements();) {
                ((ExtendedStatus)e.nextElement()).setPriority(priority);
            }
            save();
        }
    }
    private void save(){
        ExtendedStatus.saveStatusToStorage(StaticData.getInstance().statusList);
    }
}
