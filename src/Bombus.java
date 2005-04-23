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

/*#DefaultConfiguration#*///<editor-fold>
import javax.microedition.media.Manager;
/*$DefaultConfiguration$*///</editor-fold>
/*#M55#*///<editor-fold>
//--import com.siemens.mp.media.Manager;
/*$M55$*///</editor-fold>

import ui.*;

import Client.*;

/** Entry point class
 *
 * @author  Eugene Stahov
 * @version
 */
public class Bombus extends MIDlet implements Runnable{
    
    private Command exitCommand; // The exit command
    private Display display;    // The display for this MIDlet
    private boolean IsRunning;
    //IconTextList l;
    
    /** Bombus constructor. starts splashscreen */
    public Bombus() {
        display = Display.getDisplay(this);
        SplashScreen s= SplashScreen.getInstance();
        display.setCurrent(s);
        s.setProgress("Loading",3);
        exitCommand = new Command("Exit", Command.SCREEN, 2);
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
/*#DefaultConfiguration,M55#*///<editor-fold>

        {
            String s[]=Manager.getSupportedContentTypes(null);
            for (int i=0;i<s.length;i++){
                System.out.println(s[i]);
            }
            System.out.println("----");
        }
        {
            String s[]=Manager.getSupportedProtocols(null);
            for (int i=0;i<s.length;i++){
                System.out.println(s[i]);
            }
            
        }
/*$DefaultConfiguration,M55$*///</editor-fold>
        
        StaticData sd=StaticData.getInstance();

        SplashScreen s= SplashScreen.getInstance();
        s.setProgress(5);
        
        try {
            s.img=Image.createImage("/images/splash.png");
            s.setProgress("Bombus "+Version.version,7);
        } catch (Exception e) {
            e.printStackTrace();
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

        if ( (StaticData.getInstance().account_index<0) || s.keypressed!=0)
        {
            s.setProgress("Entering setup",20);
            StaticData.getInstance().account_index=-1;
            new AccountSelect(display);
            s.setProgress("Loading",25);
        } else Account.launchAccount(display);
        s.setProgress(26);
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) {
    }
  
}
