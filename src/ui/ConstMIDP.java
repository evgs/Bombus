/*
 * ConstMIDP.java
 *
 * Created on 22.11.2005, 19:14
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author EvgS
 */
public class ConstMIDP {
    
//#if !(MIDP1)
    public final static int TEXTFIELD_DECIMAL=	TextField.DECIMAL;
    public final static int CHOICE_POPUP=	ChoiceGroup.POPUP;
    public final static int TEXTFIELD_SENSITIVE=TextField.SENSITIVE;
//#else
//--    public final static int TEXTFIELD_DECIMAL=	TextField.ANY;
//--    public final static int CHOICE_POPUP=	ChoiceGroup.EXCLUSIVE;
//--    public final static int TEXTFIELD_SENSITIVE=0;
//#endif
    public final static int TEXTFIELD_URL=TextField.ANY;

//#if (!MIDP1)
    public final static String MIDP=		"MIDP2";
//#elif (USE_SIEMENS_API) 
//#     public final static String MIDP=		"MIDP1(Siemens)";
//#elif (RIM)
//#     public final static String MIDP=		"MIDP1(RIM)";
//#else
//#     public final static String MIDP=		"MIDP1";
//#endif
}
