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
    
}
