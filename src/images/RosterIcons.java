/*
 * RosterIcons.java
 *
 * Created on 3 Декабрь 2005 г., 20:02
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package images;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.ImageList;
import util.StringLoader;

/**
 *
 * @author EvgS
 */
public class RosterIcons extends ImageList{
    
    private static RosterIcons instance;

    public static RosterIcons getInstance() {
	if (instance==null) instance=new RosterIcons();
	return instance;
    }

    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=6;
    
    private Hashtable transports;
    private Vector transpSkins;
    
    /** Creates a new instance of RosterIcons */
    private RosterIcons() {
	super("/images/skin.png", ICONS_IN_COL, ICONS_IN_ROW);
        
        transports=new StringLoader().hashtableLoader("/images/transports.txt"); //new Hashtable();
        transpSkins=new Vector(transports.size());
        
        transports.put("conference", new Integer(ICON_GROUPCHAT_INDEX));
        
    }
    
    public int getTransportIndex(String name){
        Object o=transports.get(name);
        if (o instanceof String) {
            int index=(transpSkins.size()+1)<<24;
            // loading additional skin
            ImageList customTransp=new ImageList((String) o, 1, ICONS_IN_ROW);
            // customTransp loading success?
            if (customTransp.getHeight()==0) customTransp=this;
            
            transpSkins.addElement( customTransp );
            transports.put(name, new Integer(index) );
            
            return index;
        } else {
            return (o==null)?0:((Integer)o).intValue();
        }
        //if (resource) if (index==6) index=0;
    }
    

    public void drawImage(Graphics g, int index, int x, int y) {
        if (index>0x0ffffff) 
            ((ImageList)transpSkins.elementAt( (index>>24) -1 )).drawImage(g, index & 0xff, x, y);
        else super.drawImage(g, index, x, y);
    }

    public static final int ICON_INVISIBLE_INDEX = 0x10;

    public static final int ICON_ERROR_INDEX = 0x11;

    public static final int ICON_TRASHCAN_INDEX = 0x12;

    public static final int ICON_PROGRESS_INDEX = 0x13;

    public static final int ICON_MODERATOR_INDEX = 0x50;

    public static final int ICON_PRIVACY_ACTIVE = 0x46;

    public static final int ICON_PRIVACY_PASSIVE = 0x47;

    public static final int ICON_GROUPCHAT_INDEX = 0x40;

    public static final int ICON_GCJOIN_INDEX = 0x41;

    public static final int ICON_SEARCH_INDEX = 0x14;

    public static final int ICON_REGISTER_INDEX = 0x15;

    public static final int ICON_MSGCOLLAPSED_INDEX = 0x16;

    public static final int ICON_MESSAGE_INDEX = 0x20;

    public static final int ICON_AUTHRQ_INDEX = 0x21;

    public static final int ICON_COMPOSING_INDEX = 0x22;
    public static final int ICON_AD_HOC=ICON_COMPOSING_INDEX;

    public static final int ICON_EXPANDED_INDEX = 0x23;

    public static final int ICON_COLLAPSED_INDEX = 0x24;

    public static final int ICON_MESSAGE_BUTTONS = 0x25;

    public static final int ICON_PROFILE_INDEX = 0x30;

    public static final int ICON_PRIVACY_ALLOW = 0x36;

    public static final int ICON_PRIVACY_BLOCK = 0x37;

    public static final int ICON_KEYBLOCK_INDEX = 0x17;
}
