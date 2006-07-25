/*
 * IqLast.java
 *
 * Created on 25 Июль 2006 г., 19:14
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;
import ui.Time;

/**
 *
 * @author EvgS
 */
public class IqLast extends Iq{
    
    /** Creates a new instance of IqLast */
    public IqLast(JabberDataBlock request, long lastMessageTime) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
        JabberDataBlock query=addChild("query",null);
        query.setNameSpace("jabber:iq:last");
        long last=(Time.localTime()-lastMessageTime)/1000;
        query.setAttribute("seconds", String.valueOf(last));
    }
}
