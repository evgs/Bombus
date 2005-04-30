/*
 * VirtualElement.java
 *
 * Created on 29 Март 2005 г., 0:13
 */

package ui;
import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public interface VirtualElement {
    
    public int getVHeight();
    public int getVWidth();
    public int getColorBGnd(); 
    public int getColor(); 
    public void drawItem(Graphics g, int ofs, boolean selected);
}
