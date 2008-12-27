/*
 * Snow.java
 *
 * Created on 27 Декабрь 2008 г., 15:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ny;

import java.util.Random;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import util.ArrayLoader;

/**
 *
 * @author evgs
 */
public class Snow extends Canvas
    implements Runnable 
{
    private class SnowRun implements Runnable {
        private Snow snow;
        public SnowRun(Snow snow) {
            this.snow=snow;
        }
        
        public void run() {
            try {
                Thread.sleep(50);
                display.callSerially(snow);
                ((Canvas)display.getCurrent()).repaint();
            } catch (Exception e) {};

        }
    }
    
    private SnowRun snowRun;
    
    private final static int PARTICLES=256;
    private final static int DPARTICLES = 5;

    private Display display;
    //private Displayable parentView;
    
    int px[];
    int rx[];
    int py[];
    int dx[];
    int dy[];
    int phase[];
    int df[];
    byte sinus[];

    private int width;
    private int height;
    
    Random r;

    
    private void init() {
        px=new int[PARTICLES];
        rx=new int[PARTICLES];
        py=new int[PARTICLES];
        dx=new int[PARTICLES];
        dy=new int[PARTICLES];
        phase=new int[PARTICLES];
        df=new int[PARTICLES];
        
        /*sinus=new byte[256];
        
        for (int i=0; i<256; i++) {
            sinus[i]=(byte)(Math.sin(Math.PI*i/128)*127);
        }*/
        
        r=new Random();
        
        snowRun=new SnowRun(this);
    }
    
    private void newParticle(int i) {
        if (width<=0) return;
        py[i]=1;
        px[i]=r.nextInt(width);
        dx[i]=r.nextInt(32)+1;
        dy[i]=r.nextInt(4)+1;
        phase[i]=r.nextInt(256);
        df[i]=r.nextInt(16)+3;
    }
    
    /** Creates a new instance of Snow */
    public Snow(Display d) {
        this.display=d;
        init();
        //display.setCurrent(this);
        display.callSerially(this);
        repaint();
    }

    public void paint(Graphics graphics) {
        width=graphics.getClipWidth();
        height=graphics.getClipHeight();
        
        //graphics.setColor(0x00);
        //graphics.fillRect(0,0, width, height);
        
        graphics.setColor(0xffffff);
        for (int i=0; i<PARTICLES; i++) {
            int x=rx[i] + px[i];
            int y=py[i];
            graphics.drawLine(x, y, x, y);
        }
    }
    
    public void destroyView() {
        //display.setCurrent(parentView);
        snowRun=null;
    }

    public void run() {
        if (width!=0) {
            System.out.println(width);
        }
        if (sinus==null) sinus=new ArrayLoader().readByteArray("/sin256");
        
        
        int fallParticles=DPARTICLES;
        for (int i=0; i<PARTICLES; i++) {
            py[i]+=dy[i];
            rx[i]=dx[i]*sinus[phase[i]] >>7;
            phase[i]=(phase[i]+df[i]) & 0xff;
            
            if (py[i]>height) py[i]=0;
            
            if (py[i]==0 && fallParticles>0) {
                newParticle(i);
                fallParticles--;
            }
        }
        
        
        if (snowRun!=null) new Thread (snowRun).start();
        
    }

}
