/*
 * Group.java
 *
 * Created on 8 Май 2005 г., 0:36
 */

package Client;
import java.util.*;
import ui.*;


/**
 *
 * @author Evg_S
 */
class Group extends IconTextElement {
    String name;
    int index;
    public int ncontacts;
    public int onlines;
    
    Vector Contacts;
    public int tonlines;
    public int tncontacts;
    
    boolean collapsed;
    public Group(int index, String name) {
        super(StaticData.getInstance().rosterIcons);
        this.index=index; this.name=name;
    }
    public int getColor(){ return 0x000080; }
    public int getImageIndex() {
        return collapsed?
            ImageList.ICON_COLLAPSED_INDEX
            :(index==Roster.SRC_RESULT_INDEX)?
                ImageList.ICON_SEARCH_INDEX
                :ImageList.ICON_EXPANDED_INDEX;
    }
    public String toString(){ return name+" ("+onlines+'/'+ncontacts+')'; }
    public void onSelect(){
        collapsed=!collapsed;
    }
    
}
