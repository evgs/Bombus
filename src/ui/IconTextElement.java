/*
 * IconTextList.java
 *
 * Created on 30 январь 2005 г., 18:19
 */

package ui;
import ui.ImageList;
import javax.microedition.lcdui.*;
import java.util.*;


/**
 *
 * @author Eugene Stahov
 */
abstract public class IconTextElement implements VirtualElement 
{
    
    int itemHeight;
    int imageYOfs;
    int fontYOfs;
    int imgWidth;
    
    ImageList il;
    
    Font f; // шрифт дл€ рисовани€
    
    abstract protected int getImageIndex();
    //abstract public void onSelect();

    //private Element _callback;
    
    /*public void attachElements(Element e){
        _callback=e;
        redraw();
    }
     */
    
    public int getBGndRGB(){ return 0xffffff;}
    public void drawItem(Graphics g,int ofs,boolean sel){
        
       //Image img=il.getImage(_callback.getImage(index));
        String str=null;
        str=toString();
       
       g.setFont(f);
       g.setColor(getColor());
       il.drawImage(g, getImageIndex(), 2, imageYOfs);
       //g.drawImage(img,2, imageYOfs, Graphics.TOP|Graphics.LEFT);
       g.clipRect(4+imgWidth, 0, 255, itemHeight);
       g.drawString(str,4+imgWidth-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
    }
    public int getVWidth(){ 
        try {
            return f.stringWidth(toString())+imgWidth+4;            
        } catch (Exception e) {
            return 0;
        }
    }
    //public int getItemCount()
    public int getVHeight(){ return itemHeight;}
    public int getColorBGnd(){ return 0xffffff;}
    
    /*public void eventOk(){
        if (atCursor!=null) atCursor.onSelect();
    }
     */
/** Creates a new instance of IconTextList */
    public IconTextElement(ImageList il) {
        super();
        this.il=il;
        f=Font.getDefaultFont();
        int hf=f.getHeight();
        int hi=il.getHeight();
        imgWidth=il.getWidth();
        itemHeight=(hi>hf)?hi:hf;
        imageYOfs=(itemHeight-hi)/2;
        fontYOfs=1+(itemHeight-hf)/2;
    }
}
