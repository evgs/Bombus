/*
 * vCardForm.java
 *
 * Created on 3 ќкт€брь 2005 г., 0:37
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package VCard;
import java.util.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author EvgS
 */
public class vCardForm {
    
    private Display display;
    private Displayable parentView;
    
    protected Command cmdCancel=new Command("Cancel", Command.BACK, 99);
    //protected Command cmdOK=new Command("OK", Command.OK, 1);
    
    private Form f;
    
    /** Creates a new instance of vCardForm */
    public vCardForm(Display display, vCard vcard, boolean editable) {
        for (int index=0; index<vcard.vCardData.size();index++) {
            String data=(String)vcard.vCardData.elementAt(index);
            String name=(String)vCard.vCardLabels.elementAt(index);
            Item item=null;
            if (editable) {
                item=new TextField(name, data, 200, TextField.ANY);
            } else if (data.length()!=0) {
                item=new StringItem (name, data);
            }
        }
    }
}
