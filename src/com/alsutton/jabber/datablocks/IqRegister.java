/*
 * IqRegister.java
 *
 * Created on 24 Апрель 2005 г., 3:00
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;


/**
 *
 * @author Evg_S
 */
public class IqRegister extends JabberDataBlock{
    
    /** Creates a new instance of IqRegister */
    public IqRegister(String username, String password, String id) {
        super( );
        
        setTypeAttribute( "set" );
        setAttribute( "id", id );
        
        JabberDataBlock qB = addChild("query", null );
        qB.setNameSpace( "jabber:iq:register" );
        qB.addChild("username",username);
        qB.addChild("password",password);
        
    }
  public String getTagName()
  {
    return "iq";
  }    
}
