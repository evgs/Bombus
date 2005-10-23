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
public class Group extends IconTextElement {
    String name;
    String label;
    int index;
    public int ncontacts;
    public int onlines;
    public int imageExpandedIndex=ImageList.ICON_EXPANDED_INDEX;
    
    Vector Contacts;
    public int tonlines;
    public int tncontacts;
    
    boolean collapsed;
    
    public Group(int index, String name, String label) {
        super(StaticData.getInstance().rosterIcons);
        this.index=index; 
        this.name=name;
        this.label=label;
        
        if (index==Groups.SRC_RESULT_INDEX) 
            imageExpandedIndex=ImageList.ICON_SEARCH_INDEX;
    }
    public int getColor(){ return 0x000080; }
    public int getImageIndex() {
        return collapsed?
            ImageList.ICON_COLLAPSED_INDEX
            :imageExpandedIndex;
    }
    
    public String getName() { return name; }
    public String toString(){ return ((label==null)?name:label)+" ("+onlines+'/'+ncontacts+')'; }
    public void onSelect(){
        collapsed=!collapsed;
    }
    
}
