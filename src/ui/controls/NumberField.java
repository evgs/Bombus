/*
 * SignNumberField.java
 *
 * Created on 10 Декабрь 2005 г., 1:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
//#if !MIDP1
import javax.microedition.lcdui.ItemCommandListener;
//#endif
import javax.microedition.lcdui.TextField;
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
    /** Creates a new instance of SignNumberField */
    public NumberField(String label, int initValue, int minValue, int maxValue) {
	super(label, String.valueOf(initValue), 6, 
	    (minValue<0)?ConstMIDP.TEXTFIELD_DECIMAL:NUMERIC );
	this.initValue=initValue;
	this.minValue=minValue;
	this.maxValue=maxValue;
	sign=new Command("- (Sign)", Command.ITEM, 1);
//#if !MIDP1
	if (minValue<0) addCommand(sign);
	setItemCommandListener(this);
//#endif
    }
    
    public int getValue() {
	try {
	    int value=Integer.parseInt(getString());
	    if (value>maxValue) return maxValue;
	    if (value<minValue) return maxValue;
	    return value;
	} catch (NumberFormatException e) { /* returning initValue */ }
	return initValue;
    }

//#if !MIDP1
    public void commandAction(Command command, Item item) {
	StringBuffer body=new StringBuffer( getString() );
	if ( body.charAt(0)=='-' ) 
	    body.deleteCharAt(0);
	else
	    body.insert(0,'-');
	setString(body.toString());
    }
//#endif
}
