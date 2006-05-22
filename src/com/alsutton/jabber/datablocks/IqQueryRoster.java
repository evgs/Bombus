/*
 * IqQueryRoster.java
 *
 * Created on 12 Январь 2005 г., 0:17
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
*/

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;

/**
 * Class representing the iq message block
 */

public class IqQueryRoster extends Iq
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
    super(null, Iq.TYPE_GET, "getros" );

    addChild("query", null).setNameSpace( "jabber:iq:roster" );
  }
  
  /** add to roster*/
  public IqQueryRoster(String jid, String name, String group, String subscription) {
    super(null, Iq.TYPE_SET, "addros");

    JabberDataBlock qB = addChild("query", null );
    qB.setNameSpace( "jabber:iq:roster" );
        JabberDataBlock item= qB.addChild("item",null);
        item.setAttribute("jid", jid);
        item.setAttribute("name", name);
        item.setAttribute("subscription", subscription);
        if (group!=null) {
            item.addChild("group",group);
        }
  }
}
