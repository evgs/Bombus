/*
 * IqQueryRoster.java
 *
 * Created on 12 январь 2005 г., 0:17
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;

/**
 * Class representing the iq message block
 */

public class IqQueryRoster extends JabberDataBlock
{
  /**
   * Constructor including an Attribute list
   *
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  /*public IqQueryRoster( JabberDataBlock _parent, Hashtable _attributes )
  {
    super( _parent, _attributes );
  }
   */

  public IqQueryRoster() {
    super( );

    setAttribute( "type", "get" );
    setAttribute( "id", "getros" );

    JabberDataBlock qB = new JabberDataBlock( "query", null, null );
    qB.setNameSpace( "jabber:iq:roster" );

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
}
/**
 *
 * @author Eugene Stahov
 */
    
    /** Creates a new instance of IqQueryRoster */
    
