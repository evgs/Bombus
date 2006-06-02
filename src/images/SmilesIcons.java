/*
 * SmilesIcons.java
 *
 * Created on 3 Декабрь 2005 г., 20:07
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package images;

import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class SmilesIcons extends ImageList{
    
    private final static int SMILES_IN_ROW=16;
    /** Creates a new instance of SmilesIcons */
    private SmilesIcons() {
	super("/images/smiles.png", 0, SMILES_IN_ROW);
    }
    private static ImageList instance;
    public static ImageList getInstance() {
	if (instance==null) instance=new SmilesIcons();
	return instance;
    }
}
