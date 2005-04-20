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
    
    public int getHeight();
    public int getWidth();
    public int getColorBGnd(); 
    public int getColor(); 
    public void drawItem(int index, Graphics g, int ofs, boolean selected);
}
