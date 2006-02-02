/*
 * ImageListC.java
 *
 * Created on 31 январь 2005 г., 0:06
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

/**
 *
 * @author Eugene Stahov
 */

package ui;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class ImageList {

    //public final static int ICON_ASK_INDEX=0x06;
    
    
    protected Image resImage;
    protected int height,width;
    //int count,total;
    /** Creates a new instance of ImageListC */
    public ImageList(String resource) {
        try {
            resImage = Image.createImage(resource);
        } catch (Exception e) { }
        height = 1;
        width = 1;
    }
    
    public void drawImage(Graphics g, int index, int x, int y){
        int ho=g.getClipHeight();
        int wo=g.getClipWidth();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int iy=y-height*(int)(index>>4);
        int ix=x-width*(index&0x0f);
        g.setClip(x,y, width,height);
        g.drawImage(resImage,ix,iy,Graphics.TOP|Graphics.LEFT);
        g.setClip(xo,yo, wo, ho);
    };
    
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    //public int getCount() {return total;}
}
