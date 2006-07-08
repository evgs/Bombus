/*
 * IqTimeReply.java
 *
 * Created on 10 Сентябрь 2005 г., 23:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package com.alsutton.jabber.datablocks;

import com.alsutton.jabber.JabberDataBlock;

/**
 *
 * @author EvgS
 */
public class IqTimeReply extends Iq{
    
    /** Creates a new instance of IqTimeReply */
    public IqTimeReply(JabberDataBlock request) {
        super(request.getAttribute("from"),
              Iq.TYPE_RESULT,
              request.getAttribute("id") );
        JabberDataBlock query=addChild("query",null);
        query.setNameSpace("jabber:iq:time");
        query.addChild("utc",ui.Time.utcLocalTime());
        query.addChild("display", ui.Time.dispLocalTime());
    }
}
