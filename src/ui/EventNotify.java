/*
 * EventNotify.java
 *
 * Created on 3 Март 2005 г., 23:37
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;
import java.io.InputStream;

//#if !(MIDP1)
import javax.microedition.media.*;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
//#endif

//#if MIDP1
//--import com.siemens.mp.game.*;
//--import com.siemens.mp.media.*;
//--import com.siemens.mp.media.control.VolumeControl;
//--import com.siemens.mp.m55.*;
//#endif

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
    private boolean toneSequence;
    private String soundName;
    private String soundType;
    
    private Display display;

    private static Player player;
    
    private final static String tone="A6E6J6";
    //private int sndVolume;
    
    /** Creates a new instance of EventNotify */
    public EventNotify(
	Display display, 
	String soundMediaType, 
	String soundFileName, 
	//int sndVolume,
	int vibraLength, 
	boolean enableLights
    ) {
        this.display=display;
	this.soundName=soundFileName;
	this.soundType=soundMediaType;
	this.lenVibra=vibraLength;
	this.enableLights=enableLights;
	if (soundType!=null) toneSequence= soundType.equals("tone");
	//this.sndVolume=100;
    }
    
    public void startNotify (){
        release();
        if (soundName!=null)
        try {
//#if !(MIDP1)        
            InputStream is = getClass().getResourceAsStream(soundName);
            //Player p = Manager.createPlayer(is, "audio/X-wav");
            player = Manager.createPlayer(is, soundType);
//#else
//--            player = Manager.createPlayer(soundName);
//#endif
	    player.addPlayerListener(this);
	    player.realize();
	    player.prefetch();
	    

//	    try {
//		VolumeControl vol=(VolumeControl) player.getControl("VolumeControl");
//		vol.setLevel(sndVolume);
//	    } catch (Exception e) { e.printStackTrace(); }

	    player.start();
        } catch (Exception e) { }

//#if !(MIDP1)        
	if (enableLights) display.flashBacklight(1000);
//#endif

    if (lenVibra>0)
//#if !(MIDP1)
         display.vibrate(lenVibra);
//#else
//--        Vibrator.triggerVibrator(lenVibra);
//#endif
        
	if (toneSequence 
//#if MIDP1
//--	|| enableLights
//#endif
	) new Thread(this).start();
    }
    
    public void run(){
        try {
//#if MIDP1
//--	    if (enableLights) { new Light(); Light.setLightOn(); }
//#endif
	    if (toneSequence) {
		for (int i=0; i<tone.length(); ) {
		    int note=(tone.charAt(i++)-'A')+12*(tone.charAt(i++)-'0');
		    int duration=150;
		    Manager.playTone(note, duration, 100);
		    Thread.sleep(duration);
		}
	    }
//#if MIDP1
//--            Thread.sleep(1500);
//--	    if (enableLights) { Light.setLightOff(); }
//#endif
        } catch (Exception e) { e.printStackTrace();}
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
	if (string.equals(PlayerListener.END_OF_MEDIA)) {    release(); }
    }
}
