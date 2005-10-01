/*
 * IqGetVCard.java
 *
 * Created on 4 Май 2005 г., 22:48
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;
import javax.microedition.lcdui.Image;
import VCard.*;

/**
 * Class representing the iq message block
 */

public class IqGetVCard extends JabberDataBlock
{
    public IqGetVCard(String to, String id ) {
        super( );
        
        setTypeAttribute( "get" );
        setAttribute( "to", to );
        setAttribute( "id", id );
        
        JabberDataBlock qB = addChild("vCard", null);
        qB.setNameSpace( "vcard-temp" );
    }
    
    
  /**
   * Method to return the tag name
   *
   * @return Always the string "iq".
   */
  public String getTagName()
  {
    return "iq";
  }
  
    
   public static String dispatchVCard(JabberDataBlock data) {
       new vCard();
        if (data==null) return "No vCard available";
        if (!data.isJabberNameSpace("vcard-temp")) return "unknown vCard namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<vCard.vCardFields.size(); i++){
            // TODO: добавить вложенные поля vCard
            String f1=(String)vCard.vCardFields.elementAt(i);
            String f2=(String)vCard.vCardFields2.elementAt(i);
            JabberDataBlock d2=data;
            if (f2!=null) {
                d2=data.getChildBlock(f2.toLowerCase());
            }
            
            String field=d2.getChildBlockText(f1.toLowerCase());
            if (field.length()>0) {
                vc.append(vCard.vCardLabels.elementAt(i));
                vc.append((char)0xa0);
                vc.append(field);
                vc.append((char)'\n');
            }
        }
        if (data.getChildBlock("photo")!=null) {
            vc.append("Photo available");
        }
        return vc.toString();
    }

   public static String getNickName(JabberDataBlock data) {
       if (!data.isJabberNameSpace("vcard-temp")) return "";
       return data.getChildBlockText(((String)(vCard.vCardFields.elementAt(1))).toLowerCase());
   }
   
   public static Image getPhoto(JabberDataBlock data) {
       if (data==null) return null;
       JabberDataBlock photo=data.getChildBlock("photo");
       if (photo==null) return null;
       try {
           photo=photo.getChildBlock("binval");
           byte src[]=(byte[])photo.getChildBlocks().lastElement();
           return Image.createImage(src, 0, src.length);
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
   }
}
