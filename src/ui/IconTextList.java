/*
 * IconTextList.java
 *
 * Created on 30 Январь 2005 г., 18:19
 */

package ui;
import ui.ImageList;
import javax.microedition.lcdui.*;
import java.util.*;


/**
 *
 * @author Eugene Stahov
 */
abstract public class IconTextList extends VirtualList 
{
    
    int itemHeight;
    int imageYOfs;
    int fontYOfs;
    int imgWidth;
    
    ImageList il;
    
    Font f; // шрифт для рисования
    
    protected Vector stringCache;
    
    public interface Element {
        int getImageIndex();
        int getColor();
        String toString();
        void onSelect();
    }

    Element atCursor;
    abstract public Element getItemRef(int Index);
    //private Element _callback;
    
    public void moveCursorTo(Object focused){
        int count=getItemCount();
        for (int index=0;index<count;index++){
            if (focused==getItemRef(index)) {
                moveCursorTo(index);
                break;
            }
        }
    }
    /*public void attachElements(Element e){
        _callback=e;
        redraw();
    }
     */
    
    public Element getSelectedObject(){
        return atCursor;
    };
    
    public int getBGndRGB(){ return 0xffffff;}
    public void drawItem(int index, Graphics g,int ofs,boolean sel){
        
        Element el=getItemRef(index);
        if (sel) atCursor=el;
        
       //Image img=il.getImage(_callback.getImage(index));
        String str=null;
        if (stringCache!=null){
            // обработка кэша строк
            try {
                str = (String)stringCache.elementAt(index);
            } catch (Exception e) { }
            if (str==null) {
                str=el.toString();
                int sz=stringCache.size(); if (index>=sz) stringCache.setSize(index+1);
                stringCache.setElementAt(str, index);
            }
            
        } else str=el.toString();
       
       g.setFont(f);
       g.setColor(el.getColor());
       il.drawImage(g, el.getImageIndex(), 2, imageYOfs);
       //g.drawImage(img,2, imageYOfs, Graphics.TOP|Graphics.LEFT);
       g.clipRect(4+imgWidth, 0, 255, height);
       g.drawString(str,4+imgWidth-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
    }
    public int getItemWidth(int index){ 
        try {
            return f.stringWidth(getItemRef(index).toString())+imgWidth+4;            
        } catch (Exception e) {
            return 0;
        }
    }
    //public int getItemCount()
    public int getItemHeight(int index){ return itemHeight;}
    
    public void eventOk(){
        if (atCursor!=null) atCursor.onSelect();
    }
/** Creates a new instance of IconTextList */
    public IconTextList(ImageList il) {
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
