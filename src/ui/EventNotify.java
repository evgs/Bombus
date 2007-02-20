/*
 * EventNotify.java
 *
 * Created on 3.03.2005, 23:37
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui;
import javax.microedition.lcdui.*;
import java.io.InputStream;

//#if !(MIDP1)
import javax.microedition.media.*;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
//#endif

//#if USE_SIEMENS_API
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
        implements Runnable
//#if USE_SIEMENS_API || !(MIDP1)
	,PlayerListener
//#endif
{
    
    private int lenVibra;
    private boolean enableLights;
    private boolean toneSequence;
    private String soundName;
    private String soundType;
    
    private Display display;

//#if USE_SIEMENS_API || !(MIDP1)
    private static Player player;
//#endif
    
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
//#elif USE_SIEMENS_API
//--            player = Manager.createPlayer(soundName);
//#endif

//#if USE_SIEMENS_API || !(MIDP1)
            player.addPlayerListener(this);
	    player.realize();
	    player.prefetch();
	    

//	    try {
//		VolumeControl vol=(VolumeControl) player.getControl("VolumeControl");
//		vol.setLevel(sndVolume);
//	    } catch (Exception e) { e.printStackTrace(); }

	    player.start();
//#endif
        } catch (Exception e) { }

//#if !(MIDP1)        
	if (enableLights) display.flashBacklight(1000);
//#endif

    if (lenVibra>0)
//#if !(MIDP1)
         display.vibrate(lenVibra);
//#elif USE_SIEMENS_API
//--        Vibrator.triggerVibrator(lenVibra);
//#endif
        
	if (toneSequence 
//#if USE_SIEMENS_API
//--	|| enableLights
//#endif
	) new Thread(this).start();
    }
    
    public void run(){
        try {
//#if USE_SIEMENS_API
//--	    if (enableLights) { new Light(); Light.setLightOn(); }
//#endif

	    if (toneSequence) {
		for (int i=0; i<tone.length(); ) {
//#if USE_SIEMENS_API || !(MIDP1)
		    int note=(tone.charAt(i++)-'A')+12*(tone.charAt(i++)-'0');
		    int duration=150;
		    Manager.playTone(note, duration, 100);
		    Thread.sleep(duration);
//#endif
		}
	    }
//#if USE_SIEMENS_API
//--            Thread.sleep(1500);
//--	    if (enableLights) { Light.setLightOff(); }
//#endif
        } catch (Exception e) { e.printStackTrace();}
    }
    
    public synchronized void release(){
//#if USE_SIEMENS_API || !(MIDP1)
        if (player!=null) {
	    player.removePlayerListener(this);
	    player.close();
	}
        player=null;
//#endif
    }
    
//#if USE_SIEMENS_API || !(MIDP1)
    public void playerUpdate(Player player, String string, Object object) {
	if (string.equals(PlayerListener.END_OF_MEDIA)) {    release(); }
    }
//#endif

//#if USE_LED_PATTERN
//--    public static void leds(int pattern, boolean state){
//--        if (state) Ledcontrol.playPattern(pattern);
//--        else       Ledcontrol.stopPattern();
//--    }
//#endif

}
