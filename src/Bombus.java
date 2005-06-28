/*
 * Bombus.java
 *
 * Created on 5 ‗םגאנü 2005 ד., 21:46
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

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class Bombus extends MIDlet implements Runnable{
    
    private Display display;    // The display for this MIDlet
    private boolean IsRunning;
    //IconTextList l;
    
    /** Bombus constructor. starts splashscreen */
    public Bombus() {
        display = Display.getDisplay(this);
        SplashScreen s= SplashScreen.getInstance();
        display.setCurrent(s);
        s.setProgress("Loading",3);
    }
    
    /** Entry point  */
    public void startApp() {
        
        if (IsRunning) return;
        
        IsRunning=true;

        new Thread(this).start();
    }
    
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() { }

    public void run(){
        
        StaticData sd=StaticData.getInstance();

        SplashScreen s= SplashScreen.getInstance();
        s.setProgress(5);
        
        try {
            s.img=Image.createImage("/images/splash.png");
            s.setProgress("Bombus "+Version.version,7);
        } catch (Exception e) {
            e.printStackTrace();
/*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(e, "Bombus:69");
/*$USE_LOGGER$*///</editor-fold>
        }

        sd.initFields(this);
        //s.setProgress(10);
        
        /*s.setProgress("* - Account setup",12);
        try {
            wait(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        s.setProgress(17);

        boolean selAccount=( (sd.account_index<0) || s.keypressed!=0);
        if (selAccount) s.setProgress("Entering setup",20);

        sd.roster=new Roster(display);
        
        if (!selAccount) {
            // connect whithout account select
            selAccount=(Account.launchAccount()==null);
        }
        if (selAccount) { new AccountSelect(display, true); }
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) {
    }
  
}
