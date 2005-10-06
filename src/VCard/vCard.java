/*
 * vCard.java
 *
 * Created on 24 Сентябрь 2005 г., 1:24
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package VCard;
import java.util.*;
import java.io.*;

/**
 *
 * @author EvgS
 */
public class vCard {

    public static Vector vCardFields;
    public static Vector vCardFields2;
    public static Vector vCardLabels;
    
    public Vector vCardData;
    /** Creates a new instance of vCard */
    public vCard() {
        if (vCardFields==null) fieldsLoader();
    }
    
    private void fieldsLoader(){
        StringBuffer buf=new StringBuffer();
        
        vCardFields=new Vector();
        vCardFields2=new Vector();
        vCardLabels=new Vector();
        
        InputStream in=this.getClass().getResourceAsStream("/vcard.txt");
        try {
            while (true) {
                int c=in.read();
                if (c<0) break;
                switch (c) {
                    case 0x0d:
                    case 0x0a:
                        if (buf.length()>0){
                            //vCardFields.addElement(field.toString());
                            vCardLabels.addElement(buf.toString());
                        }
                        buf.setLength(0);
                        break;
                    case 0x09:
                        vCardFields.addElement(buf.toString());
                        buf.setLength(0);
                        break;
                    case '/':
                        vCardFields2.setSize(vCardFields.size()+1);
                        vCardFields2.setElementAt(buf.toString(), vCardFields.size());
                        buf.setLength(0);
                        break;
                    default:
                        buf.append((char)c);
                }
            }
            in.close();
            vCardFields2.setSize(vCardFields.size());
        } catch (Exception e) {}
    }
}
