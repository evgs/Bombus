/*
 * ConstMIDP.java
 *
 * Created on 22 ќкт€брь 2005 г., 19:14
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
//#else
//--    public final static int TEXTFIELD_DECIMAL=	TextField.ANY;
//--    public final static int CHOICE_POPUP=	ChoiceGroup.EXCLUSIVE;
//#endif

}
