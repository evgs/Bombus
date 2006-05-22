/*
 * IqGetVCard.java
 *
 * Created on 4 Май 2005 г., 22:48
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.*;
import java.util.*;
import javax.microedition.lcdui.Image;

/**
 * Class representing the iq message block
 */

public class IqGetVCard extends Iq
{
    public IqGetVCard(String to, String id ) {
        super(to, Iq.TYPE_GET, id );
        addChild("vCard", null).setNameSpace( "vcard-temp" );
    }
    
}
