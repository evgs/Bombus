/*
 * MessageView.java
 *
 * Created on 20 Февраль 2005 г., 17:42
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Messages;
import Client.*;
import images.RosterIcons;
import images.SmilesIcons;
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
    
    Command cmdBack=new Command("Back",Command.BACK,99);
    Command cmdTSM=new Command("Smiles", Command.SCREEN,1);

    Command cmdSubscr=new Command("Authorize", Command.SCREEN,2);
    Command cmdPhoto=new Command("Photo", Command.SCREEN,3);

    private MessageList msglist;

    public int getTitleBGndRGB() {return 0x338888;} 
    public int getTitleRGB() {return titlecolor;} 
    
    int repaintCounter=5;
    
    public void notifyRepaint(Vector v, Msg parsedMsg){
        if (parsedMsg!=msg) return;
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
        lines=null;
        win_top=0;
        (t=new Thread(this)).start();
    }
    
    int msgIndex;
    Msg msg;
    int nMsgs;
   
    StaticData sd;
    
    public void run() {
        msg=msglist.getMessage(msgIndex);

	msglist.markRead(msgIndex);

        titlecolor=msg.getColor();
        ComplexString title=new ComplexString(RosterIcons.getInstance());
        title.addElement(msg.getMsgHeader());
        title.addRAlign();
        title.addElement(null);
        setTitleItem(title);
        
        if (msg.messageType==Msg.MESSAGE_TYPE_AUTH) addCommand(cmdSubscr);
        else removeCommand(cmdSubscr);
        //if (msg.photo!=null) addCommand(cmdPhoto);
        //else removeCommand(cmdPhoto);
        
        //win_top=0;

        MessageParser.getInstance().parseMsg(
                msg,
                (smiles)?SmilesIcons.getInstance():null, 
                getWidth()-6,
                false, this);
    }

    protected void beginPaint(){
        int micon=0;
        if (msglist==null) return;
        if (title==null) return;
        
        nMsgs=msglist.getItemCount();
        if (nMsgs>1) {
            if (msgIndex==0) micon=1;
            if (msgIndex==nMsgs-1) micon=2;
            title.setElementAt(new Integer(ImageList.ICON_MESSAGE_BUTTONS+micon),2);
        }
    }
    /** Creates a new instance of MessageView */
    public MessageView(Display display, int msgIndex, MessageList msglist) {
        super(display);
	
	enableListWrapping(false);

        sd=StaticData.getInstance();
        smiles=Config.getInstance().smiles;
        this.msgIndex=msgIndex;
        this.msglist=msglist;

        addCommand(cmdBack);
        addCommand(cmdTSM);
        setCommandListener(this);
        
        (t=new Thread(this)).start();

    }
    public void eventOk(){
        destroyView();
        if (msglist.getItemCount()>1)
        ((VirtualList)parentView).moveCursorTo(msgIndex, true);
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdBack) {
            eventOk();
            return;
        }
        if (c==cmdSubscr) {
            Jid j=new Jid(msg.from);
            sd.roster.sendPresence(j.getJidFull(), "subscribe", null);
            sd.roster.sendPresence(j.getJidFull(), "subscribed", null);
            msg.messageType=Msg.MESSAGE_TYPE_IN;
            destroyView();
        }
        if (c==cmdTSM) toggleSmiles();
        
        //if (c==cmdPhoto) new PhotoView(display, msg.photo);
    }
    
    public void userKeyPressed(int KeyCode){
        if (KeyCode==KEY_STAR) toggleSmiles();
    }

    protected void keyGreen(){
	msglist.keyGreen();
    }
    
    private void toggleSmiles(){
        smiles=!smiles;
        while (t.isAlive());
        (t=new Thread(this)).start();
    }
}

