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
import locale.SR;

/**
 *
 * @author Evg_S
 */
public class YesNoAlert extends Form implements CommandListener{
    
    public interface YesNoListener {
        public void ActionConfirmed();
    }
    
    private Display display;
    private Displayable parentView;
    
    private YesNoListener listener;

    
    Command cmdYes=new Command(SR.MS_YES, Command.OK, 1);
    Command cmdNo=new Command(SR.MS_NO, Command.BACK, 99);
    
    /** Creates a new instance of YesNoAlert */
    public YesNoAlert(Display display, String title, String alertText, YesNoListener listener) {
        super(title);
        addCommand(cmdYes);
        addCommand(cmdNo);
        setCommandListener(this);
        this.listener=listener;
        
        append("\n");
        append(alertText);
        
        this.display=display;
        this.parentView=display.getCurrent();
        display.setCurrent(this);
        
    }
    public void commandAction(Command c, Displayable d ){
        destroyView();
        if (c==cmdYes) {
            yes();
        } else no();
    }
    public void yes() {
        if (listener!=null) listener.ActionConfirmed(); 
    };
    public void no(){};
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
