/*
 * TransferSendFile.java
 *
 * Created on 4 Ноябрь 2006 г., 0:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package io.file.transfer;

import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import ui.controls.TextFieldCombo;

/**
 *
 * @author Evg_S
 */
public class TransferSendFile 
        implements CommandListener, BrowserListener
{
    
    private Display display;
    private Displayable parentView;
    
    Form f;
    TextField fileName;
    /*TextField size;*/
    TextField description;

    private String to;
    
    Command cmdOk=new Command("Ok", Command.OK, 1);
    Command cmdBack=new Command("Cancel", Command.BACK, 99);
    Command cmdPath=new Command("Select File", Command.SCREEN, 2);

    /** Creates a new instance of TransferAcceptFile */
    public TransferSendFile(Display display, String recipientJid) {
        this.display=display;
        this.to=recipientJid;
        parentView=display.getCurrent();
        
        f=new Form("Send file");
        f.append(new StringItem("To: ", recipientJid));
        
        fileName=new TextFieldCombo("File", null, 256, TextField.ANY | TextField.UNEDITABLE, "sendfile", display );
        f.append(fileName);
        
        /*size=new TextField("size", "", 8, TextField.ANY | TextField.UNEDITABLE );
        f.append(size);*/
        
        description=new TextField("description", "", 128, TextField.ANY );
        f.append(description);

        
        f.addCommand(cmdOk);
        f.addCommand(cmdPath);
        f.addCommand(cmdBack);
        
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    public void BrowserFilePathNotify(String pathSelected) { fileName.setString(pathSelected); }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdPath) { new Browser(fileName.getString(), display, this, false); return; }
        if (c==cmdOk) {
            try {
                TransferTask task=new TransferTask(to, String.valueOf(System.currentTimeMillis()), fileName.getString(), description.getString());
                TransferDispatcher.getInstance().sendFile(task);
            } catch (Exception e) {}
        }
        
        display.setCurrent(parentView);
    }
  
}
