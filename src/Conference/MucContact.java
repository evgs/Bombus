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

/**
 *
 * @author root
 */
public class MucContact extends Contact{
    
    public String realJid;
    public int affiliationIndex;
    public int roleIndex;

    public long conferenceJoinTime;

    /** Creates a new instance of MucContact */
    public MucContact() {
    }
    
}
