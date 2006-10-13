/*
 * DiscoContact.java
 *
 * Created on 7 Июнь 2006 г., 22:41
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;

import Client.Contact;
import ui.IconTextElement;

public class DiscoContact extends Contact{
    public DiscoContact(final String Nick, final String sJid, final int Status) {
        super(Nick, sJid, Status, null);
    }
    public String toString() { return (nick==null)?getJid():nick; }

    public int compare(IconTextElement right){
        Contact c=(Contact) right;
        return this.toString().compareTo( c.toString() );
    }

    public String getTipString() {
        return getJid();
    }
}
