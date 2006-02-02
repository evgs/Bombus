/*
 * AffiliationItem.java
 *
 * Created on 30 ќкт€брь 2005 г., 11:53
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Conference.affiliation;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import images.RosterIcons;
import ui.IconTextElement;
import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class AffiliationItem extends IconTextElement{
    public final static int AFFILIATION_NONE=0;
    public final static int AFFILIATION_OWNER=1;
    public final static int AFFILIATION_ADMIN=2;
    public final static int AFFILIATION_MEMBER=3;
    public final static int AFFILIATION_OUTCAST=4;
    
    public static String getAffiliationName(int index){
        switch (index) {
            case AFFILIATION_OWNER: return "owner";
            case AFFILIATION_ADMIN: return "admin";
            case AFFILIATION_MEMBER: return "member";
            case AFFILIATION_OUTCAST: return "outcast";
        }
        return "none";
    };
    
    public int getImageIndex(){ 
        switch (affiliation) {
            case AFFILIATION_OWNER: return RosterIcons.ICON_REGISTER_INDEX;
            case AFFILIATION_ADMIN: return RosterIcons.ICON_MODERATOR_INDEX;
            case AFFILIATION_MEMBER: return 0;
            case AFFILIATION_OUTCAST: return RosterIcons.ICON_ERROR_INDEX;
        }
        return RosterIcons.ICON_INVISIBLE_INDEX; 
    }

    public String jid;
    public int affiliation;
        
    /** Creates a new instance of AffiliationItem */
    public AffiliationItem(String jid, String affiliation) {
        super(RosterIcons.getInstance());
        this.jid=jid;
        for (int index=1; index<5; index++) {
            if (affiliation.equals(getAffiliationName(index))) this.affiliation=index;
        }
    }
    
    public AffiliationItem(JabberDataBlock item) {
        this(item.getAttribute("jid"), item.getAttribute("affiliation"));
    }
    
    
    public int getColor() { return 0; }
    
    public String toString() { return jid; }
}
