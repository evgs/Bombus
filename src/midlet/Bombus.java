/*
 * Bombus.java
 *
 * Created on 5 Январь 2005 г., 21:46
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

/**
 *
 * @author Eugene Stahov
 */
package midlet;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import locale.SR;

import ui.*;

import Client.*;
import Info.Version;


/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class Bombus extends MIDlet implements Runnable{
    
    private Display display;    // The display for this MIDlet
    private boolean isRunning;
    private boolean isMinimized;
    StaticData sd;
    //IconTextList l;
    
    private static Bombus instance; 
        
    /** Bombus constructor. starts splashscreen */
    public Bombus() {
	instance=this; 
        display = Display.getDisplay(this);
        SplashScreen s= SplashScreen.getInstance();
        display.setCurrent(s);
        s.setProgress(/*SR.MS_LOADING*/ "Loading",3); // this message will not be localized
        sd=StaticData.getInstance();
    }
    
    /** Entry point  */
    public void startApp() {
        
        if (isRunning) {
	    hideApp(false);
            return;
        }
        
        isRunning=true;

        new Thread(this).start();
    }
    
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() { }

    public void run(){
        
        SplashScreen s= SplashScreen.getInstance();
        s.setProgress(5);
        
        try {
            s.img=Image.createImage("/images/splash.png");
            s.setProgress("Bombus "+Version.version,7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sd.initFields();
        s.setProgress(10);
	Config cf=Config.getInstance();

	s.setProgress(17);
        boolean selAccount=( (cf.accountIndex<0) || s.keypressed!=0);
        if (selAccount) s.setProgress("Entering setup",20);

        s.setProgress(23);
        sd.roster=new Roster(display);
        
        if (!selAccount) {
            // connect whithout account select
	    boolean autologin=cf.autoLogin;
            selAccount=(Account.loadAccount(autologin)==null);
	    if (!autologin) s.close();
        }
        if (selAccount) { new AccountSelect(display, true); }
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) {
    }

    public void hideApp(boolean hide) {
	if (hide) {
	    display.setCurrent(null);
	} else {
            if (isMinimized) {
                display.setCurrent(sd.roster);
            }
	}
        isMinimized=hide;
    }
    
    public static Bombus getInstance() {
        return instance;
    }
  
}
