/*
 * AffiliationItem.java
 *
 * Created on 30 ќкт€брь 2005 г., 11:53
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package Conference.affiliation;

import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
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
            case AFFILIATION_OWNER: return ImageList.ICON_REGISTER_INDEX;
            case AFFILIATION_ADMIN: return ImageList.ICON_MODERATOR_INDEX;
            case AFFILIATION_MEMBER: return 0;
            case AFFILIATION_OUTCAST: return ImageList.ICON_ERROR_INDEX;
        }
        return ImageList.ICON_INVISIBLE_INDEX; 
    }

    public String jid;
    public int affiliation;
        
    /** Creates a new instance of AffiliationItem */
    public AffiliationItem(String jid, String affiliation) {
        super(StaticData.getInstance().rosterIcons);
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
