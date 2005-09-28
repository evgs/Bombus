/*
 * IqGetVCard.java
 *
 * Created on 4 Май 2005 г., 22:48
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;
import javax.microedition.lcdui.Image;

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
  
      private final static String TOPFIELDS []={
        "FN",  "NICKNAME",  "BDAY",  "LOCALITY", "COUNTRY", "URL", "DESC"
    }; 
    private final static String TOPNAMES []={
        "Name",  "Nick",  "Birthday",  "City", "Country", "URL", "About"
    }; 
    
    
   public static String dispatchVCard(JabberDataBlock data) {
        if (data==null) return "No vCard available";
        if (!data.isJabberNameSpace("vcard-temp")) return "unknown vCard namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<TOPFIELDS.length; i++){
            // TODO: добавить вложенные поля vCard
            String field=data.getChildBlockText(TOPFIELDS[i].toLowerCase());
            if (field.length()>0) {
                vc.append(TOPNAMES[i]);
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
       return data.getChildBlockText(TOPFIELDS[1].toLowerCase());
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
