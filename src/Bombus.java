/*
 * Bombus.java
 *
 * Created on 5 ‗םגאנü 2005 ד., 21:46
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 *
 * שלוכü=bombus
 * Al-Colibry :)
 */

/**
 *
 * @author Eugene Stahov
 */
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

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
    private boolean IsRunning;
    StaticData sd;
    //IconTextList l;
    
    private static Bombus instance; 
        
    /** Bombus constructor. starts splashscreen */
    public Bombus() {
        display = Display.getDisplay(this);
        SplashScreen s= SplashScreen.getInstance();
        display.setCurrent(s);
        s.setProgress("Loading",3);
        sd=StaticData.getInstance();
    }
    
    /** Entry point  */
    public void startApp() {
        
        if (IsRunning) {
            if (sd.isMinimized) {
                display.setCurrent(sd.roster);
                sd.isMinimized=false;
            }
            return;
        }
        
        IsRunning=true;

        new Thread(this).start();
    }
    
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() { }

    public void run(){
        
	instance=this; 
	
        SplashScreen s= SplashScreen.getInstance();
        s.setProgress(5);
        
        try {
            s.img=Image.createImage("/images/splash.png");
            s.setProgress("Bombus "+Version.version,7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sd.initFields();
	Config cf=Config.getInstance();
        //s.setProgress(10);
        
        /*s.setProgress("* - Account setup",12);
        try {
            wait(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        s.setProgress(17);

        boolean selAccount=( (cf.accountIndex<0) || s.keypressed!=0);
        if (selAccount) s.setProgress("Entering setup",20);

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

    public static Bombus getInstance() {
        return instance;
    }
  
}
