/*
 * vCardForm.java
 *
 * Created on 3 ������� 2005 �., 0:37
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package vcard;
import Client.StaticData;
import java.util.*;
import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author EvgS
 */
public class vCardForm 
        implements CommandListener
{
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    protected Command cmdPublish=new Command(SR.MS_PUBLISH, Command.OK /*Command.SCREEN*/, 1);
    protected Command cmdRefresh=new Command(SR.MS_REFRESH, Command.SCREEN, 2);
    
    private Form f;
    private Vector items=new Vector();
    private VCard vcard;
    
    /** Creates a new instance of vCardForm */
    public vCardForm(Display display, VCard vcard, boolean editable) {
        this.display=display;
        parentView=display.getCurrent();
        
        this.vcard=vcard;
        
        f=new Form(SR.MS_VCARD);
        f.append(vcard.getJid());
        
        for (int index=0; index<vcard.getCount(); index++) {
            String data=vcard.getVCardData(index);
            String name=(String)VCard.vCardLabels.elementAt(index);
            Item item=null;
            if (editable) {
                item=new TextField(name, data, 200, TextField.ANY);
                items.addElement(item);
            } else if (data!=null) {
                item=new StringItem (name, data);
            }
            if (item!=null) {
                f.append(item);
//#if !(MIDP1)
                f.append(new Spacer(256, 3));
//#else
//--                f.append("\n");
//#endif
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
            VCard.request(vcard.getJid());
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
