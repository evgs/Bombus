/*
 * Baloon.java
 *
 * Created on 6 Февраль 2006 г., 23:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ui.controls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.FontCache;

/**
 *
 * @author Evg_S
 */
public class Balloon {
    
    public static int getHeight(){
        Font f=FontCache.getBalloonFont();
        return f.getHeight()+3;
    }
    
    public static void draw(Graphics g, String text) {
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int height=getHeight();
        int width=f.stringWidth(text)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height-1;
        g.translate(0, y);
        
        g.setColor(Colors.BALLOON_INK);
        g.fillRect(2, 0, width, height);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(3, 1, width-2, height-2);
       
        g.setColor(Colors.BALLOON_INK);
        g.drawString(text, 5, 2, Graphics.TOP | Graphics.LEFT);
    }
}
