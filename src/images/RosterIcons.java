/*
 * RosterIcons.java
 *
 * Created on 3 Декабрь 2005 г., 20:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package images;

import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class RosterIcons extends ImageList{
    
    private final static int ICONS_IN_ROW=8;
    private final static int ICONS_IN_COL=10;
    /** Creates a new instance of RosterIcons */
    private RosterIcons() {
	super("/images/skin.png");
	height=resImage.getHeight()/ICONS_IN_COL;
	width=resImage.getWidth()/ICONS_IN_ROW;
    }
    private static ImageList instance;
    public static ImageList getInstance() {
	if (instance==null) instance=new RosterIcons();
	return instance;
    }

    public static final int ICON_INVISIBLE_INDEX = 81;

    public static final int ICON_ERROR_INDEX = 82;

    public static final int ICON_TRASHCAN_INDEX = 83;

    public static final int ICON_PROGRESS_INDEX = 84;

    public static final int ICON_MODERATOR_INDEX = 96;

    public static final int ICON_PRIVACY_ACTIVE = 102;

    public static final int ICON_PRIVACY_PASSIVE = 103;

    public static final int ICON_GROUPCHAT_INDEX = 112;

    public static final int ICON_GCJOIN_INDEX = 113;

    public static final int ICON_SEARCH_INDEX = 114;

    public static final int ICON_REGISTER_INDEX = 115;

    public static final int ICON_MESSAGE_INDEX = 128;

    public static final int ICON_AUTHRQ_INDEX = 129;

    public static final int ICON_COMPOSING_INDEX = 130;

    public static final int ICON_EXPANDED_INDEX = 131;

    public static final int ICON_COLLAPSED_INDEX = 132;

    public static final int ICON_MESSAGE_BUTTONS = 133;

    public static final int ICON_PROFILE_INDEX = 144;

    public static final int ICON_PRIVACY_ALLOW = 149;

    public static final int ICON_PRIVACY_BLOCK = 150;

    public static final int ICON_KEYBLOCK_INDEX = 151;
    
}
