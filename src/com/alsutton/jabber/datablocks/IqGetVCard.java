/*
 * IqGetVCard.java
 *
 * Created on 4 Май 2005 г., 22:48
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;

/**
 * Class representing the iq message block
 */

public class IqGetVCard extends JabberDataBlock
{
    public IqGetVCard(String to) {
        super( );
        
        setTypeAttribute( "get" );
        setAttribute( "to", to );
        setAttribute( "id", "getvc" );
        
        JabberDataBlock qB = new JabberDataBlock( "vCard", null, null );
        qB.setNameSpace( "vcard-temp" );
        
        addChild(qB);
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
        if (!data.isJabberNameSpace("vcard-temp")) return "unknown vCard namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<TOPFIELDS.length; i++){
            String field=data.getTextForChildBlock(TOPFIELDS[i].toLowerCase());
            if (field.length()>0) {
                vc.append(TOPNAMES[i]);
                vc.append((char)0xa0);
                vc.append(field);
                vc.append((char)'\n');
            }
        }
        return vc.toString();
    }

}
