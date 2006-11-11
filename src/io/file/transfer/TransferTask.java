/*
 * TransferTask.java
 *
 * Created on 28 Октябрь 2006 г., 17:00
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file.transfer;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import com.alsutton.jabber.datablocks.Message;
import images.RosterIcons;
import io.file.FileIO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.IconTextElement;
import util.strconv;

/**
 *
 * @author Evg_S
 */
public class TransferTask 
        extends IconTextElement
        implements Runnable
{
    
    public final static int COMPLETE=1;
    public final static int PROGRESS=3;
    public final static int ERROR=4;
    public final static int HANDSHAKE=6;
    public final static int IN_ASK=7;
    public final static int NONE=5;
    
    private int state=NONE;
    private boolean sending;
    boolean showEvent;
    String jid;
    String id;
    String sid;
    String fileName;
    String description;
    String errMsg;
    int fileSize;
    private int filePos;
    String filePath;
    private FileIO file;
    private OutputStream os;
    private InputStream is;
    
    private Vector methods;
    
    /** Creates TransferTask for incoming file */
    public TransferTask(String jid, String id, String sid, String name, String description, int size, Vector methods) {
        super(RosterIcons.getInstance());
        state=IN_ASK;
        showEvent=true;
        this.jid=jid;
        this.id=id;
        this.sid=sid;
        this.fileName=name;
        this.description=description;
        this.fileSize=size;
        this.methods=methods;
    }
    
    /**
     * Sending constructor
     */
    public TransferTask(String jid, String sid, String fileName, String description) {
        super(RosterIcons.getInstance());
        state=HANDSHAKE;
        sending=true;
        //showEvent=true;
        this.jid=jid;
        this.sid=sid;
        this.fileName=fileName.substring( fileName.lastIndexOf('/')+1 );
        this.description=description;
        //this.fileSize=size;
        //this.methods=methods;
        try {
            file=FileIO.createConnection(fileName);
            is=file.openInputStream();
            
            fileSize=(int)file.fileSize();
        } catch (Exception e) {
            e.printStackTrace();
            state=ERROR;
            errMsg="Can't open file";
            showEvent=true;
        }
    }

    protected int getImageIndex() { return state; }

    public int getColor() { return (sending)? Colors.MESSAGE_OUT : Colors.MESSAGE_IN; }

    public void drawItem(Graphics g, int ofs, boolean sel) {
        int xpgs=(g.getClipWidth()/3)*2;
        int pgsz=g.getClipWidth()-xpgs-4;
        int filled=(fileSize==0)? 0 : (pgsz*filePos)/fileSize; 
        
        int oldColor=g.getColor();
        g.setColor(0xffffff);
        
        g.fillRect(xpgs, 3, pgsz, getVHeight()-6);
        g.setColor(0x668866);
        g.drawRect(xpgs, 3, pgsz, getVHeight()-6);
        g.fillRect(xpgs, 3, filled, getVHeight()-6);
        g.setColor(oldColor);
        
        super.drawItem(g, ofs, sel);
        showEvent=false;
    }
    
    public String toString() { return fileName; }

    public String getTipString() { 
        return (errMsg==null)? String.valueOf(fileSize) : errMsg; 
    }

    void decline() {
        JabberDataBlock reject=new Iq(jid, Iq.TYPE_ERROR, id);
        JabberDataBlock error=reject.addChild("error",null);
        error.setTypeAttribute("cancel");
        error.setAttribute("code","405");
        error.addChild("not-allowed",null).setNameSpace("urn:ietf:params:xml:ns:xmpp-stanzas");
        TransferDispatcher.getInstance().send(reject, true);
        
        state=ERROR;
        errMsg="Rejected";
        showEvent=true;
    }

    void accept() {
        
        try {
            file=FileIO.createConnection(filePath+fileName);
            os=file.openOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            decline();
            return;
        }
        JabberDataBlock accept=new Iq(jid, Iq.TYPE_RESULT, id);
        
        JabberDataBlock si=accept.addChild("si", null);
        si.setNameSpace("http://jabber.org/protocol/si");
        
        JabberDataBlock feature=si.addChild("feature", null);
        feature.setNameSpace("http://jabber.org/protocol/feature-neg");
        
        JabberDataBlock x=feature.addChild("x", null);
        x.setNameSpace("jabber:x:data");
        x.setTypeAttribute("submit");
        
        JabberDataBlock field=x.addChild("field", null);
        field.setAttribute("var","stream-method");
        field.addChild("value", "http://jabber.org/protocol/ibb");
        
        TransferDispatcher.getInstance().send(accept, true);
        state=HANDSHAKE;
    }
    
    void writeFile(byte b[]){
        try {
            os.write(b);
            filePos+=b.length;
            state=PROGRESS;
        } catch (IOException ex) {
            ex.printStackTrace();
            state=ERROR;
            errMsg="Write error";
            showEvent=true;
            //todo: terminate transfer
        }
    }

    int readFile(byte b[]) {
        try {
            int len=is.read(b);
            if (len<0) len=0;
            filePos+=len;
            state=PROGRESS;
            return len;
        } catch (IOException ex) {
            ex.printStackTrace();
            state=ERROR;
            errMsg="Read error";
            showEvent=true;
            //todo: terminate transfer
            return 0;
        }
    }

    boolean isAcceptWaiting() { return state==IN_ASK; }

    void closeFile() {
        try {
            if (os!=null)
                os.close();
            if (is!=null)
                is.close();
            file.close();
            if (state!=ERROR) state=COMPLETE;
        } catch (Exception ex) {
            ex.printStackTrace();
            errMsg="File close error";
            state=ERROR;
        }
        file=null;
        is=null;
        os=null;
        showEvent=true;
    }

    void sendInit() {
            if (state==ERROR) return;
            
            JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, sid); 
            
            JabberDataBlock si=iq.addChild("si", null);
            si.setNameSpace("http://jabber.org/protocol/si");
            si.setAttribute("id",sid);
            si.setAttribute("mime-type","text/plain");
            si.setAttribute("profile", "http://jabber.org/protocol/si/profile/file-transfer");
            
            JabberDataBlock file=si.addChild("file",null);
            file.setNameSpace("http://jabber.org/protocol/si/profile/file-transfer");
            file.setAttribute("name", fileName);
            file.setAttribute("size", String.valueOf(fileSize));
            
            JabberDataBlock feature=si.addChild("feature", null);
            feature.setNameSpace("http://jabber.org/protocol/feature-neg");
            
            JabberDataBlock x=feature.addChild("x", null);
            x.setNameSpace("jabber:x:data");
            x.setTypeAttribute("form");
            
            JabberDataBlock field=x.addChild("field", null);
            field.setTypeAttribute("list-single");
            field.setAttribute("var", "stream-method");
            
            field.addChild("option", null).addChild("value", "http://jabber.org/protocol/ibb");
            
            TransferDispatcher.getInstance().send(iq, true);
            
        
    }

    void initIBB() {
        JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, sid);
        JabberDataBlock open=iq.addChild("open", null);
        open.setNameSpace("http://jabber.org/protocol/ibb");
        open.setAttribute("sid", sid);
        open.setAttribute("block-size","2048");
        TransferDispatcher.getInstance().send(iq, false);
    }

    public void run() {
        byte buf[]=new byte[2048];
        int seq=0;
        try {
        while (true) {
            int sz=readFile(buf);
            if (sz==0) break;
            
            JabberDataBlock msg=new Message(jid);
            
            JabberDataBlock data=msg.addChild("data", strconv.toBase64(buf, sz));
            data.setNameSpace("http://jabber.org/protocol/ibb");
            data.setAttribute("sid", sid);
            data.setAttribute("seq", String.valueOf(seq));   seq++;
            
            JabberDataBlock amp=msg.addChild("amp",null);
            amp.setNameSpace("http://jabber.org/protocol/amp");
            
            JabberDataBlock rule;
            
            rule=amp.addChild("rule", null);
            rule.setAttribute("condition", "deliver-at"); 
            rule.setAttribute("value", "stored");
            rule.setAttribute("action", "error");
            
            rule=amp.addChild("rule", null);
            rule.setAttribute("condition", "match-resource"); 
            rule.setAttribute("value", "exact");
            rule.setAttribute("action", "error");
            
            TransferDispatcher.getInstance().send(msg, false);
            TransferDispatcher.getInstance().repaintNotify();

            Thread.sleep( 2000L ); //shaping traffic
        }
        } catch (Exception e) { /*null pointer exception if terminated*/}
        closeFile();
        JabberDataBlock iq=new Iq(jid, Iq.TYPE_SET, "close");
        JabberDataBlock close=iq.addChild("close", null);
        close.setNameSpace("http://jabber.org/protocol/ibb");
        close.setAttribute("sid", sid);
        TransferDispatcher.getInstance().send(iq, false);
        TransferDispatcher.getInstance().eventNotify();
    }

    void startTransfer() {
        new Thread(this).start();
    }

    boolean isStopped() {
        return ((state==COMPLETE) || (state==ERROR));
    }

    void cancel() {
        if (isStopped()) return;
        state=ERROR;
        errMsg="Canceled";
        closeFile();
    }
}
