/*
 * KeyBlock.java
 *
 * Created on 15 Май 2005 г., 3:08
 */

package ui;

import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class KeyBlock extends Canvas implements Runnable{
    
    public final static int COLOR_BLK_BORDER=  0x808080;
    public final static int COLOR_BLK_TEXT=    0xffffff;
    public final static int COLOR_BLK_BGND=    0x000000;
    
    private int width;
    private int height;
    
    private Display display;
    private Displayable parentView;
    
    private Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    /** Creates a new instance */
    public KeyBlock(Display display, ComplexString status, char exitKey) {
        this.status=status;
        this.display=display;
        kHold=this.exitKey=exitKey;
        
        parentView=display.getCurrent();
        status.setElementAt(new Integer(ImageList.ICON_KEYBLOCK_INDEX),6);
        repaint();

        new Thread(this).start();
    }
    
    public void run(){
        try {
            img=Image.createImage("/images/splash.png");
        } catch (Exception e) {};
        
        display.setCurrent(this);
    }
    
    public void paint(Graphics g){
        width=getWidth();
        height=getHeight();
        Font f=Font.getDefaultFont();
        
        g.setColor(COLOR_BLK_BGND);
        g.fillRect(0,0, width, height);
        
        if (img!=null) g.drawImage(img, width/2, 0, Graphics.TOP|Graphics.HCENTER);
        
        int h=f.getHeight()+1;
/*#DefaultConfiguration,Release#*///<editor-fold>
        int y=height-h-4;
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--        int y=height-h;
/*$M55,M55_Release$*///</editor-fold>
        g.setColor(COLOR_BLK_TEXT);
        g.translate(0, y);
        status.drawItem(g, 0, false);
        
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
        if (display!=null)   display.setCurrent(parentView);
    }    
}
