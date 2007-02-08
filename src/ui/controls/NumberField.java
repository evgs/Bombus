/*
 * SignNumberField.java
 *
 * Created on 10 Декабрь 2005 г., 1:29
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui.controls;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
//#if !MIDP1
import javax.microedition.lcdui.ItemCommandListener;
//#endif
import javax.microedition.lcdui.TextField;
import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author EvgS
 */
public class NumberField extends TextField 
//#if !MIDP1
    implements ItemCommandListener 
//#endif
{
    private int initValue;
    private int minValue;
    private int maxValue;
    private Command sign;
    //private Command clear;
    /** Creates a new instance of SignNumberField */
    public NumberField(String label, int initValue, int minValue, int maxValue) {
	super(label, String.valueOf(initValue), 6, 
	    (minValue<0)?ConstMIDP.TEXTFIELD_DECIMAL:NUMERIC );
	this.initValue=initValue;
	this.minValue=minValue;
	this.maxValue=maxValue;
	sign=new Command(SR.MS_CHSIGN, Command.ITEM, 3);  //1->3 - Siemens S75 workaround
	//clear=new Command(SR.MS_CLEAR, Command.ITEM, 4);  //2->4
//#if !MIDP1
	if (minValue<0) addCommand(sign);
        //addCommand(clear);
	setItemCommandListener(this);
//#endif
    }
    
    public int getValue() {
	try {
	    int value=Integer.parseInt(getString());
	    if (value>maxValue) return maxValue;
	    if (value<minValue) return minValue;
	    return value;
	} catch (NumberFormatException e) { /* returning initValue */ }
	return initValue;
    }

//#if !MIDP1
    public void commandAction(Command command, Item item) {
	StringBuffer body=new StringBuffer( getString() );
	//if (command==clear) body.setLength(0);
        if (command==sign) {
            if ( body.charAt(0)=='-' ) 
                body.deleteCharAt(0);
            else
                body.insert(0,'-');
            }
	setString(body.toString());
    }
//#endif
}
