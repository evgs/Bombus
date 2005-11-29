/*
 * Group.java
 *
 * Created on 8 Май 2005 г., 0:36
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
    int index; // group index
    public int ncontacts;
    public int onlines;
    public int imageExpandedIndex=ImageList.ICON_EXPANDED_INDEX;
        
    public Vector contacts;
    
    //Vector tcontacts;
    public int tonlines;
    public int tncontacts;
    
    boolean collapsed;
    
    public Group(String name /*, String label*/) {
        super(StaticData.getInstance().rosterIcons);
        this.name=name;
        /*this.label=label;*/
        
    }
    public int getColor(){ return 0x000080; }
    public int getImageIndex() {
        return collapsed?
            ImageList.ICON_COLLAPSED_INDEX
            :imageExpandedIndex;
    }
    
    public String getName() { return name; }
    protected String title(String titleStart) {
	return titleStart+" ("+onlines+'/'+ncontacts+')';
    }
    public String toString(){ return title(name);  }

    public void onSelect(){
        collapsed=!collapsed;
    }

    public void setIndex(int index) {
	this.index = index;
        if (index==Groups.SRC_RESULT_INDEX) 
            imageExpandedIndex=ImageList.ICON_SEARCH_INDEX;
    }
    
}
