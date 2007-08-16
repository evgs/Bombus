/*
 * KeyBlock.java
 *
 * Created on 15.05.2005, 3:08
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

import Client.Config;
import images.RosterIcons;
import javax.microedition.lcdui.*;
import java.util.*;
import midlet.Bombus;

/**
 *
 * @author Eugene Stahov
 */
public class KeyBlock extends Canvas implements Runnable{
    
    private int width;
    private int height;
    
    private Display display;
    private Displayable parentView;
    
    private Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    private TimerTaskClock tc;
    
    boolean motorola_backlight;
    boolean singleflash;
    
    /** Creates a new instance */
    public KeyBlock(
            Display display, 
            ComplexString status, 
            char exitKey, 
            boolean motorola_backlight) 
    {
        this.status=status;
        this.display=display;
        kHold=this.exitKey=exitKey;
        this.motorola_backlight=motorola_backlight;
        
        parentView=display.getCurrent();
        status.setElementAt(new Integer(RosterIcons.ICON_KEYBLOCK_INDEX),6);
        repaint();
        
        singleflash=true;

        new Thread(this).start();
        
        tc=new TimerTaskClock();
        
        
//#if !(MIDP1)
        setFullScreenMode(Config.getInstance().fullscreen);
//#elif USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOff();
//#endif
        //System.gc();   // heap cleanup
    }
    
    public void run(){
        try {
            img=Bombus.splash;
            if (img==null) img=Image.createImage("/images/splash.png");
        } catch (Exception e) {};
        
        display.setCurrent(this);
    }
    
    public void paint(Graphics g){
        width=getWidth();
        height=getHeight();
        Font f=FontCache.getClockFont();
        
        g.setColor(Colors.BLK_BGND);
        g.fillRect(0,0, width, height);
        
        if (img!=null) g.drawImage(img, width/2, 0, Graphics.TOP|Graphics.HCENTER);
        
        int h=f.getHeight()+1;
//#if ALCATEL_FONT
//#         int y=height-h-4;
//#else
        int y=height-h;
//#endif
        g.setColor(Colors.BLK_INK);
        g.translate(0, y);
        status.drawItem(g, 0, false);
        
        String time=Time.timeLocalString(Time.utcTimeMillis());
        int tw=f.stringWidth(time);
        
        g.translate(width/2, -h);
        g.setColor(Colors.BLK_BGND);
        g.fillRect(-tw/2-5, -h, tw+10, h);

        g.setColor(Colors.BLK_INK);
        g.setFont(f);
        g.drawString(time, 0, 0, Graphics.BOTTOM | Graphics.HCENTER);
//#if !(MIDP1)
	//display.flashBacklight(0); // тест на самсунгах
        if (motorola_backlight) 
            if (singleflash) display.flashBacklight(1);
        singleflash=false;
//#endif
    }
    
    public void keyPressed(int keyCode) { 
        //System.out.println("blocked press"+(char) keyCode);
        kHold=0; 
    }
    public void keyReleased(int keyCode) { 
        //System.out.println("blocked released"+(char) keyCode); kHold=0; 
    }
    protected void keyRepeated(int keyCode) { 
        //System.out.println("blocked repeat"+(char) keyCode);
        if (kHold==0)
        if (keyCode==exitKey) destroyView(); 
    }

    private void destroyView(){
        status.setElementAt(null,6);
//#if !(MIDP1)
        if (motorola_backlight) display.flashBacklight(Integer.MAX_VALUE);
//#endif
        if (display!=null)   display.setCurrent(parentView);
        img=null;
        tc.stop();
//#if USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOn();
//#endif
        System.gc();
    }
    
    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            repaint();
//#if USE_SIEMENS_API
//--	com.siemens.mp.game.Light.setLightOff();
//#endif
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }
}
