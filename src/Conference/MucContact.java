/*
 * MucContact.java
 *
 * Created on 2 Май 2006 г., 14:05
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference;

import Client.Contact;
import com.alsutton.jabber.datablocks.Presence;

/**
 *
 * @author root
 */
public class MucContact extends Contact{
    
    public String realJid;
    public int affiliationIndex;
    public int roleIndex;

    /** Creates a new instance of MucContact */
    public MucContact(String nick, String jid) {
        super(nick, jid, Presence.PRESENCE_OFFLINE, "muc");
        offline_type=Presence.PRESENCE_OFFLINE;
    }
    
}
