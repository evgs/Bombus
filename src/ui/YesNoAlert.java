/*
 * YesNoAlert.java
 *
 * Created on 8 Май 2005 г., 23:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;

/**
 *
 * @author Evg_S
 */
public abstract class YesNoAlert extends Form implements CommandListener{
    
    private Display display;
    private Displayable parentView;

    
    Command cmdYes=new Command("Yes", Command.OK, 1);
    Command cmdNo=new Command("No", Command.BACK, 99);
    /** Creates a new instance of YesNoAlert */
    public YesNoAlert(Display display, Displayable parentView, String title, String alertText) {
        super(title);
        addCommand(cmdYes);
        addCommand(cmdNo);
        setCommandListener(this);

        append("\n");
        append(alertText);
        
        this.display=display;
        this.parentView=parentView;//display.getCurrent();
        display.setCurrent(this);
        
    }
    public void commandAction(Command c, Displayable d ){
        destroyView();
        if (c==cmdYes) {
            yes();
        } else no();
    }
    abstract public void yes();
    public void no(){};
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
