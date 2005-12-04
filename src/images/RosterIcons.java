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
    
    /** Creates a new instance of RosterIcons */
    private RosterIcons() {
	super("/images/skin.png",13,12);
    }
    private static ImageList instance;
    public static ImageList getInstance() {
	if (instance==null) instance=new RosterIcons();
	return instance;
    }
    
}
