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
    boolean smiles;
    Thread t=null;
    
    Command CmdBack=new Command("Back",Command.BACK,99);
/*#DefaultConfiguration,Release#*///<editor-fold>
    Command CmdTSM=new Command("Smiles", "Toggle Smiles", Command.SCREEN,1);
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#!DefaultConfiguration,Release#*///<editor-fold>
//--    Command CmdTSM=new Command("Toggle Smiles", Command.SCREEN,1);
/*$!DefaultConfiguration,Release$*///</editor-fold>

    Command CmdSubscr=new Command("Authorize", Command.SCREEN,2);
    Command cmdPhoto=new Command("Photo", Command.SCREEN,3);

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
    
    public void keyLeft(){
        if (win_top==0) changeMsg(-1); else super.keyLeft();
    }
    
    public void keyRight(){
        if (atEnd) changeMsg(1); else super.keyRight();
    }
    
    private void changeMsg(int offset){
        int nextMsg=msgIndex+offset;
        if (nextMsg<0 || nextMsg>=nMsgs) return;
        msgIndex=nextMsg;

        (t=new Thread(this)).start();
    }
    
    int msgIndex;
    Msg msg;
    int nMsgs;
    Contact contact;
    StaticData sd;
    
    public void run() {
        msg=(Msg)contact.msgs.elementAt(msgIndex);

        if (msg.unread) contact.resetNewMsgCnt();
        msg.unread=false;

        titlecolor=msg.getColor1();
        ComplexString title=new ComplexString(sd.rosterIcons);
        title.addElement(msg.getMsgHeader());
        title.addRAlign();
        title.addElement(null);
        setTitleLine(title);
        
        if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) addCommand(CmdSubscr);
        else removeCommand(CmdSubscr);
        if (msg.photo!=null) addCommand(cmdPhoto);
        else removeCommand(cmdPhoto);
        
        win_top=0;

        sd.parser.parseMsg(
                msg,
                (smiles)?sd.smilesIcons:null, 
                getWidth()-6,
                false, this);
        if (msgIndex==contact.lastUnread)
            if (contact.needsCount())
            sd.roster.countNewMsgs();
    }

    public void beginPaint(){
        int micon=0;
        if (contact==null) return;
        if (title==null) return;
        
        nMsgs=contact.msgs.size();
        if (nMsgs>1) {
            if (msgIndex==0) micon=1;
            if (msgIndex==nMsgs-1) micon=2;
            title.setElementAt(new Integer(ImageList.ICON_MESSAGE_BUTTONS+micon),2);
        }
    }
    /** Creates a new instance of MessageView */
    public MessageView(Display display, int msgIndex, Contact contact) {
        super(display);

        sd=StaticData.getInstance();
        smiles=sd.config.smiles;
        this.msgIndex=msgIndex;
        this.contact=contact;

        addCommand(CmdBack);
        addCommand(CmdTSM);
        setCommandListener(this);
        
        (t=new Thread(this)).start();

    }
    public void eventOk(){
        destroyView();
        ((VirtualList)parentView).moveCursorTo(msgIndex);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==CmdBack) {
            eventOk();
            return;
        }
        if (c==CmdSubscr) {
            Jid j=new Jid(msg.from);
            sd.roster.sendPresence(j.getJid(), "subscribed");
            sd.roster.sendPresence(j.getJid(), "subscribe");
            msg.messageType=Msg.MESSAGE_TYPE_IN;
            destroyView();
        }
        if (c==CmdTSM) toggleSmiles();
        
        if (c==cmdPhoto) new PhotoView(display, msg.photo);
    }
    
    public void userKeyPressed(int KeyCode){
        if (KeyCode==KEY_STAR) toggleSmiles();
    }

    protected void keyGreen(){
        (new MessageEdit(display,contact,null)).setParentView(parentView);
    }
    
    private void toggleSmiles(){
        smiles=!smiles;
        while (t.isAlive());
        (t=new Thread(this)).start();
    }
}

class PhotoView implements CommandListener {
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command("Cancel", Command.BACK, 99);
    //protected Command cmdOK=new Command("OK", Command.OK, 1);
    
    private Form f;
    
    /**
     * constructor
     */
   
    public PhotoView(Display display, Image photo) {
        f=new Form("Photo");
        
        f.append(photo);
        f.addCommand(cmdCancel);
        f.setCommandListener(this);
        
        this.display=display;
        parentView=display.getCurrent();
        display.setCurrent(f);
    }
    
    /**
     * Called when action should be handled
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdCancel) { destroyView(); return;}
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
}
