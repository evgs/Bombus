/*
 * IconTextList.java
 *
 * Created on 30.01.2005, 18:19
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
    
    protected ImageList il;
    
    abstract protected int getImageIndex();
    //abstract public void onSelect();

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
       g.clipRect(4+imgWidth, 0, g.getClipWidth(), itemHeight);
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
    
    public int compare(IconTextElement right) { return 0; /* stub */ }
}