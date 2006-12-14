/*
 * Reconnect.java
 *
 * Created on 14 Декабрь 2006 г., 1:51
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import com.alsutton.jabber.datablocks.Presence;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Gauge;
import locale.SR;
import ui.AlertBox;

/**
 *
 * @author Evg_S
 */
public class Reconnect extends AlertBox implements Runnable{

    private Gauge timer;
    boolean isRunning;
    private final static int WAITTIME=5;
    /** Creates a new instance of Reconnect */
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 2);
    public Reconnect(String title, String body, Display display) {
        super(title, body, null, display, StaticData.getInstance().roster);
        alert.setTimeout(Alert.FOREVER);
        
        timer=new Gauge(null, false, WAITTIME, 1);
//#if (!MIDP1)
        alert.setIndicator(timer);
//#endif
        alert.addCommand(cmdCancel);
        
        new Thread(this).start();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdOk) {
            if (isRunning) {
                isRunning=false;
                StaticData.getInstance().roster.doReconnect();
            }
        }
        isRunning=false;
        display.setCurrent(next);
    }

    public void run() {
        isRunning=true;
        while (isRunning) {
            try { 
                Thread.sleep(1000);
            } catch (Exception e) { break; }
            int value=timer.getValue()+1;
            timer.setValue(value);
            if (value>=WAITTIME) break;
        }
        commandAction(cmdOk, alert);
    }
}
