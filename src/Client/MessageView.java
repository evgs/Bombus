/*
 * MessageView.java
 *
 * Created on 20 Февраль 2005 г., 17:42
 */

package Client;
import ui.*;
import javax.microedition.lcdui.*;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class MessageView 
        extends ComplexStringList 
        implements 
            CommandListener, 
            Messages.MessageParser.NotifyAddLine,
            Runnable
{

    int titlecolor; // зависит от типа сообщения
    
    Command CmdBack=new Command("Back",Command.BACK,99);

    public int getTitleBGndRGB() {return 0x338888;} 
    public int getTitleRGB() {return titlecolor;} 
    
    int repaintCounter=5;
    
    public void notifyRepaint(Vector v){ 
        AttachList(v);
        if ((--repaintCounter)>=0) return;
        repaintCounter=5;
        redraw(); 
    }
    
    public void notifyFinalized(){ redraw(); }
    
    Msg msg;
    public void run() {
        StaticData sd=StaticData.getInstance();
        sd.parser.parseMsg(
                msg.body,
                sd.smilesIcons, 
                getWidth()-6,
                false, this);
    }

    /** Creates a new instance of MessageView */
    public MessageView(Display display, Msg msg) {
        super(display);
        
        titlecolor=msg.getColor1();
        ComplexString title=new ComplexString(null);
        title.addElement(msg.getMsgHeader());
        setTitleLine(title);
        
        this.msg=msg;
        new Thread(this).start();

        addCommand(CmdBack);
        setCommandListener(this);

    }
    public void eventOk(){
        destroyView();
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==CmdBack) {
            destroyView();
            return;
        }
    }
}
