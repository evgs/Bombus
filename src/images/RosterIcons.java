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

    public static final int ICON_INVISIBLE_INDEX = 0x51;

    public static final int ICON_ERROR_INDEX = 0x52;

    public static final int ICON_TRASHCAN_INDEX = 0x53;

    public static final int ICON_PROGRESS_INDEX = 0x54;

    public static final int ICON_MODERATOR_INDEX = 0x60;

    public static final int ICON_PRIVACY_ACTIVE = 0x66;

    public static final int ICON_PRIVACY_PASSIVE = 0x67;

    public static final int ICON_GROUPCHAT_INDEX = 0x70;

    public static final int ICON_GCJOIN_INDEX = 0x71;

    public static final int ICON_SEARCH_INDEX = 0x72;

    public static final int ICON_REGISTER_INDEX = 0x73;

    public static final int ICON_MSGCOLLAPSED_INDEX = 0x74;

    public static final int ICON_MESSAGE_INDEX = 0x80;

    public static final int ICON_AUTHRQ_INDEX = 0x81;

    public static final int ICON_COMPOSING_INDEX = 0x82;

    public static final int ICON_EXPANDED_INDEX = 0x83;

    public static final int ICON_COLLAPSED_INDEX = 0x84;

    public static final int ICON_MESSAGE_BUTTONS = 0x85;

    public static final int ICON_PROFILE_INDEX = 0x90;

    public static final int ICON_PRIVACY_ALLOW = 0x95;

    public static final int ICON_PRIVACY_BLOCK = 0x96;

    public static final int ICON_KEYBLOCK_INDEX = 0x97;

    
}
