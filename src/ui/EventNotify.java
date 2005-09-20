/*
 * EventNotify.java
 *
 * Created on 3 Март 2005 г., 23:37
 */

package ui;
import javax.microedition.lcdui.*;
import java.io.InputStream;

/*#!MIDP1#*///<editor-fold>
import javax.microedition.media.*;
/*$!MIDP1$*///</editor-fold>

/*#MIDP1#*///<editor-fold>
//--import com.siemens.mp.game.*;
//--import com.siemens.mp.media.*;
//--import com.siemens.mp.m55.*;
/*$MIDP1$*///</editor-fold>

/**
 *
 * @author Eugene Stahov
 */
public class EventNotify 
        implements Runnable
{
    
    private EventProfile ep;
    
    private Display display;
/*#!MIDP1#*///<editor-fold>
    private static Player player;
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--    private static Player player;
//--    
/*$MIDP1$*///</editor-fold>
    
    /** Creates a new instance of EventNotify */
    public EventNotify(Display display, EventProfile ep) {
        this.display=display;
        this.ep=ep;
    }
    
    public void startNotify (){
/*#!MIDP1#*///<editor-fold>
        release();
        
        if (ep.soundName!=null)
        try {
            InputStream is = getClass().getResourceAsStream(ep.soundName);
            //Player p = Manager.createPlayer(is, "audio/X-wav");
            player = Manager.createPlayer(is, ep.soundType);
            player.prefetch();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ep.enableLights) display.flashBacklight(1000);
        if (ep.lenVibra>0) display.vibrate(ep.lenVibra);
        
/*$!MIDP1$*///</editor-fold>
        
/*#MIDP1#*///<editor-fold>
//--        if (ep.soundName!=null)
//--        try {
//--            player = Manager.createPlayer(ep.soundName);
//--            player.realize();
//--            player.prefetch();
//--            player.start();
//--        } catch (Exception e) {
//--            e.printStackTrace();
//--        }
//--        if (ep.lenVibra>0) Vibrator.triggerVibrator(ep.lenVibra);
//--        if (ep.enableLights) new Thread(this).start();
/*$MIDP1$*///</editor-fold>
    }
    
    public void run(){
/*#MIDP1#*///<editor-fold>
//--        try {
//--            new Light();
//--            Light.setLightOn();
//--            Thread.sleep(1500);
//--            Light.setLightOff();
//--        } catch (Exception e) { e.printStackTrace();}
/*$MIDP1$*///</editor-fold>
    }
    
    public void release(){
        if (player!=null) player.close();
        player=null;
    }
    
/*#USE_LED_PATTERN#*///<editor-fold>
//--    public static void leds(int pattern, boolean state){
//--        if (state) Ledcontrol.playPattern(pattern);
//--        else       Ledcontrol.stopPattern();
//--    }
/*$USE_LED_PATTERN$*///</editor-fold>
}
