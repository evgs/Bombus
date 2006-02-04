/*
 * ConstMIDP.java
 *
 * Created on 22 ќкт€брь 2005 г., 19:14
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
