/*
 * FormField.java
 *
 * Created on 5 Èþíü 2005 ã., 20:30
 */

package ServiceDiscovery;
import javax.microedition.lcdui.*;
import com.alsutton.jabber.*;
/**
 *
 * @author Evg_S
 */
public class FormField {
    
    public String label;
    public String name;
    public Item formItem;
    boolean hidden;
    /** Creates a new instance of FormField */
    public FormField(JabberDataBlock field) {
        name=field.getTagName();
        label=name;
        String body=field.getText();
        
        if ( name.equals("instructions") )
            formItem=new StringItem("Instructions", body);
        else 
            formItem=new TextField(label, body, 64, 0);
        
        if ( name.equals("key") ) hidden=true; 
    }
    JabberDataBlock getJabberDataBlock(){
        JabberDataBlock j=null;
        if (formItem instanceof TextField) {
            if (name.equals("registered")) return null;
            j=new JabberDataBlock(name, null, null);
            j.addText(((TextField)formItem).getString());
        }
        return j;
    }
}
