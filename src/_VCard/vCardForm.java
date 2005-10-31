/*
 * vCardForm.java
 *
 * Created on 3 ќкт€брь 2005 г., 0:37
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package vcard;
import Client.StaticData;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class vCardForm 
        implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command("Cancel", Command.BACK, 99);
    protected Command cmdPublish=new Command("Publish", Command.OK, 1);
    protected Command cmdRefresh=new Command("Refresh", Command.SCREEN, 2);
    
    private Form f;
    private Vector items=new Vector();
    private vCard vcard;
    
    /** Creates a new instance of vCardForm */
    public vCardForm(Display display, vCard vcard, boolean editable) {
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;
        
        f=new Form("vCard");
        f.append(vcard.getJid());
        
        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)vCard.vCardLabels.elementAt(index);
            Item item=null;
            if (editable) {
                item=new TextField(name, data, 200, TextField.ANY);
                items.addElement(item);
            } else if (data!=null) {
                item=new StringItem (name, data);
            }
            if (item!=null) {
                f.append(item);
/*#!MIDP1#*///<editor-fold>
                f.append(new Spacer(256, 3));
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--                f.append("\n");
/*$MIDP1$*///</editor-fold>
            }
        }
        Image photo=vcard.getPhoto();
        if (photo!=null) f.append(photo);
        
        f.append("[end of vCard]");
        
        f.addCommand(cmdCancel);
        f.addCommand(cmdRefresh);
        if (editable) f.addCommand(cmdPublish);
        f.setCommandListener(this);
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) destroyView();
        if (c==cmdRefresh) {
            vCard.request(vcard.getJid());
            destroyView();
        }
        if (c!=cmdPublish) return;
        
        for (int index=0; index<vcard.getCount(); index++) {
            String field=((TextField)items.elementAt(index)).getString();
            if (field.length()==0) field=null;
            vcard.setVCardData(index, field);
        }
        //System.out.println(vcard.constructVCard().toString());
        StaticData.getInstance().roster.theStream.send(vcard.constructVCard());
        destroyView();
    }
    
    private void destroyView() {
        display.setCurrent(parentView);
    }
}
