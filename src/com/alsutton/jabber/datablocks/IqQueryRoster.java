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

    setTypeAttribute( "get" );
    setAttribute( "id", "getros" );

    JabberDataBlock qB = new JabberDataBlock( "query", null, null );
    qB.setNameSpace( "jabber:iq:roster" );

    addChild(qB);

  }
  
  /** add to roster*/
  public IqQueryRoster(String jid, String name, String group, String subscription) {
    super( );

    setTypeAttribute( "set" );
    setAttribute( "id", "addros" );

    JabberDataBlock qB = new JabberDataBlock( "query", this, null );
    qB.setNameSpace( "jabber:iq:roster" );
        JabberDataBlock item=new JabberDataBlock("item",qB,null);
        item.setAttribute("jid", jid);
        if (name!=null) item.setAttribute("name", name);
        if (subscription!=null) item.setAttribute("subscription", name);
        if (group!=null) {
            item.addChild(new JabberDataBlock(item,"group",group));
        }
        qB.addChild(item);
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
    
