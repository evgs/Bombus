/*
 * SmilesIcons.java
 *
 * Created on 3 Декабрь 2005 г., 20:07
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
public class SmilesIcons extends ImageList{
    
    /** Creates a new instance of SmilesIcons */
    private SmilesIcons() {
	super("/images/smiles.png",15,15);
    }
    private static ImageList instance;
    public static ImageList getInstance() {
	if (instance==null) instance=new SmilesIcons();
	return instance;
    }
}
