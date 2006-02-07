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
    
    public static void draw(Graphics g, String text) {
        Font f=FontCache.getNormalFont();
        g.setFont(f);
        int height=f.getHeight()+3;
        int width=f.stringWidth(text)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height;
        height--;
        g.translate(0, y);
        
        g.setColor(Colors.BALLOON_INK);
        g.fillRoundRect(0, 0, width, height, 6, 6);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRoundRect(1, 1, width-2, height-2, 6, 6);
       
        g.setColor(Colors.BALLOON_INK);
        g.drawString(text, 5, 1, Graphics.TOP | Graphics.LEFT);
    }
}
