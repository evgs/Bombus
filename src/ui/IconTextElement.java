/*
 * IconTextList.java
 *
 * Created on 30 январь 2005 г., 18:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
    
    abstract protected int getImageIndex();
    //abstract public void onSelect();

    public int getBGndRGB(){ return 0xffffff;}
    public int getFontIndex() { return 0;}
    private Font getFont() { 
        //return Font.getFont(Font.FACE_PROPORTIONAL, getFontIndex(), Font.SIZE_MEDIUM);
        return (getFontIndex()==0)?
            FontCache.getRosterNormalFont():
            FontCache.getRosterBoldFont();
    }
    public void drawItem(Graphics g,int ofs,boolean sel){
        
       //Image img=il.getImage(_callback.getImage(index));
        String str=null;
        str=toString();
       
       g.setFont(getFont());
       if (il!=null) il.drawImage(g, getImageIndex(), 2, imageYOfs);
       //g.drawImage(img,2, imageYOfs, Graphics.TOP|Graphics.LEFT);
       g.clipRect(4+imgWidth, 0, 255, itemHeight);
       g.drawString(str,4+imgWidth-ofs, fontYOfs, Graphics.TOP|Graphics.LEFT);
    }
    public int getVWidth(){ 
        try {
            return getFont().stringWidth(toString())+imgWidth+4;            
        } catch (Exception e) {
            return 0;
        }
    }
    //public int getItemCount()
    public int getVHeight(){ return itemHeight;}
    public int getColorBGnd(){ return Colors.LIST_BGND;}
    public void onSelect(){};
    
    /*public void eventOk(){
        if (atCursor!=null) atCursor.onSelect();
    }
     */
/** Creates a new instance of IconTextList */
    public IconTextElement(ImageList il) {
        super();
        this.il=il;
        //f=Font.getDefaultFont();
        int hf=FontCache.getRosterNormalFont().getHeight();
        int hi=0;
	if (il!=null){
	    hi=il.getHeight();
            imgWidth=il.getWidth();
	}
        itemHeight=(hi>hf)?hi:hf;
        imageYOfs=(itemHeight-hi)/2;
//#if ALCATEL_FONT
//#         fontYOfs=1+(itemHeight-hf)/2;
//#else
        fontYOfs=(itemHeight-hf)/2;
//#endif
    }
    
    public String getTipString() {
        return null;
    }
}
