/*
 * SplashScreen.java
 *
 * singleton
 *
 * Created on 9 Февраль 2005 г., 21:31
 */

package ui;

import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class SplashScreen extends Canvas implements CommandListener{
    
    public final static int COLOR_PGS_REMAINED=0xffffff;
    public final static int COLOR_PGS_COMPLETE=0x0000ff;
    public final static int COLOR_PGS_BORDER=  0x808080;
    public final static int COLOR_PGS_BGND=    0x000000;
    
    private String capt;
    private int pos=-1;
    
    private int width;
    private int height;
    
    public Image img;
    
    public int keypressed=0;
    
    /*protected void sizeChanged(int w, int h){
        width=w;height=h;
    }
     */
    
    private static SplashScreen s;
    public static SplashScreen getInstance(){
        if (s==null) s=new SplashScreen();
        return s;
    }
    
    /** Creates a new instance of SplashScreen */
    private SplashScreen() {
    }
    
    public void paint(Graphics g){
        if (pos==-1) return;
        width=getWidth();
        height=getHeight();
        Font f=Font.getDefaultFont();
        
        int xp=pos*width/100;
        int xt=width/2;
        int h=f.getHeight()+1;
/*#DefaultConfiguration,Release#*///<editor-fold>
        int y=height-h-4;
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--        int y=height-h;
/*$M55,M55_Release$*///</editor-fold>
        
        g.setColor(COLOR_PGS_BGND);
        g.fillRect(0,0, width, height);
        
        if (img!=null) g.drawImage(img, width/2, 0, Graphics.TOP|Graphics.HCENTER);
        
        g.setColor(COLOR_PGS_BORDER);
        g.drawRect(0, y, width-1, h-1);
        
        g.setColor(COLOR_PGS_REMAINED);
        //g.setStrokeStyle(Graphics.DOTTED); <- не работает
        g.fillRect(1, y+1, width-2,h-2);
        //g.setStrokeStyle(Graphics.SOLID);
        
        g.setFont(f);
        g.setColor(COLOR_PGS_COMPLETE);
        g.drawString(capt,xt,y+2, Graphics.TOP|Graphics.HCENTER);
        
        g.setClip(1, y+1, xp, h-2);
        g.fillRect(1, y+1, width-2,h-2);
        
        g.setColor(COLOR_PGS_REMAINED);
        g.drawString(capt,xt,y+2, Graphics.TOP|Graphics.HCENTER);
    }
    
    public void setProgress(int progress) {
        pos=progress;
        repaint();
    }

    public void setFailed(){
        SplashScreen.getInstance().setProgress("Failed", 0);
    }
    public void setProgress(String caption, int progress){
        capt=caption;
        pos=progress;
        System.out.println(capt);
        repaint();
    }
    public int getProgress(){
        return pos;
    }

    protected void keyPressed(int keyCode) {
        keypressed=keyCode;
        //notifyAll();
    }
    
    // close splash
    private Display display;
    private Displayable parentView;
    private Command cmdExit=new Command("Hide Splash", Command.BACK, 99);
    
    public void setExit(Display display, Displayable nextDisplayable){
        this.display=display;
        parentView=nextDisplayable;
        setCommandListener(this);
        addCommand(cmdExit);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdExit) close();
    }
    
    public void close(){
        display.setCurrent(parentView);
        repaint();
        img=null;
    }
}
