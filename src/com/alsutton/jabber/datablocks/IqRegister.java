/*
 * IqRegister.java
 *
 * Created on 24 Апрель 2005 г., 3:00
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;


/**
 *
 * @author Evg_S
 */
public class IqRegister extends Iq
{
    
    /** Creates a new instance of IqRegister */
    public IqRegister(String username, String password, String id) {
        super(null, Iq.TYPE_SET, id );
        
        JabberDataBlock qB = addChild("query", null );
        qB.setNameSpace( "jabber:iq:register" );
        qB.addChild("username",username);
        qB.addChild("password",password);
        
    }
}
