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
import javax.microedition.media.PlayerListener;
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
        implements Runnable,
	PlayerListener
{
    
    private int lenVibra;
    private boolean enableLights;
    private String soundName;
    private String soundType;
    
    private Display display;

    private static Player player;
    
    /** Creates a new instance of EventNotify */
    public EventNotify(
	Display display, 
	String soundMediaType, 
	String soundFileName, 
	int vibraLength, 
	boolean enableLights
    ) {
        this.display=display;
	this.soundName=soundFileName;
	this.soundType=soundMediaType;
	this.lenVibra=vibraLength;
	this.enableLights=enableLights;
    }
    
    public void startNotify (){
        release();
//#if !(MIDP1)
        
        if (soundName!=null)
        try {
            InputStream is = getClass().getResourceAsStream(soundName);
            //Player p = Manager.createPlayer(is, "audio/X-wav");
            player = Manager.createPlayer(is, soundType);
	    player.addPlayerListener(this);
            player.prefetch();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (enableLights) display.flashBacklight(1000);
        if (lenVibra>0) display.vibrate(lenVibra);
        
//#endif
        
//#if MIDP1
//--        if (soundName!=null)
//--        try {
//--            player = Manager.createPlayer(soundName);
//--		player.addPlayerListener(this);
//--            player.realize();
//--            player.prefetch();
//--            player.start();
//--        } catch (Exception e) {
//--            e.printStackTrace();
//--        }
//--        if (lenVibra>0) Vibrator.triggerVibrator(lenVibra);
//--        if (enableLights) new Thread(this).start();
//#endif
    }
    
    public void run(){
//#if MIDP1
//--        try {
//--            new Light();
//--            Light.setLightOn();
//--            Thread.sleep(1500);
//--            Light.setLightOff();
//--        } catch (Exception e) { e.printStackTrace();}
//#endif
    }
    
    public synchronized void release(){
        if (player!=null) {
	    player.removePlayerListener(this);
	    player.close();
	}
        player=null;
    }
    
//#if USE_LED_PATTERN
//--    public static void leds(int pattern, boolean state){
//--        if (state) Ledcontrol.playPattern(pattern);
//--        else       Ledcontrol.stopPattern();
//--    }
//#endif

    public void playerUpdate(Player player, String string, Object object) {
	if (string.equals(PlayerListener.END_OF_MEDIA)) {
	    System.out.println(string);
	    release();
	}
    }
}
