/*
 * MessageEdit.java
 *
 * Created on 20 Февраль 2005 г., 21:20
 */

package Client;
import javax.microedition.lcdui.*;
import ui.VirtualList;

/**
 *
 * @author Eugene Stahov
 */
public class MessageEdit implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    private TextBox t;
    
    private Contact to;
    private Command CmdCancel=new Command("Cancel",Command.CANCEL,99);
    private Command CmdSend=new Command("Send",Command.SCREEN,1);
    private Command CmdSmile=new Command("Add Smile",Command.SCREEN,2);
    private Command CmdInsMe=new Command("/me",Command.SCREEN,3);
    
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Contact to, String body) {
        this.to=to;
        this.display=display;
        parentView=display.getCurrent();
        t=new TextBox(to.toString(),null,500,TextField.ANY);
        try {
            if (body!=null) t.setString(body);
        } catch (Exception e) {
            t.setString("<large text>");
        }
        t.addCommand(CmdSend);
        t.addCommand(CmdInsMe);
        t.addCommand(CmdSmile);
        t.addCommand(CmdCancel);
        t.setCommandListener(this);
        
/*#!M55,M55_Release#*///<editor-fold>
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
/*$!M55,M55_Release$*///</editor-fold>
        display.setCurrent(t);
    }
    
    public void AddText(String s) {
        t.insert(s, t.getCaretPosition());
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==CmdCancel) { destroyView(); return; }
        if (c==CmdInsMe) { t.insert("/me ", 0); return; }
        if (c==CmdSmile) { new SmilePicker(display, this); }
        if (c==CmdSend)   {
            Roster r=StaticData.getInstance().roster;
            String from=StaticData.getInstance().account.toString();
            //r.USERNAME+'@'+r.SERVER_NAME;
            String body=t.getString();
            
            // затычка от пустых сообщений. пока так :(
            if (body.length()==0) return;
            
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,null,body);
            to.addMessage(msg);
            //((VirtualList)parentView).moveCursorEnd();
            
            try {
                r.sendMessage(to.getJid(),body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            destroyView(); 
            return; 
        }
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
