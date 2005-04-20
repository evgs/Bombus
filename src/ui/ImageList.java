/*
 * ImageListC.java
 *
 * Created on 31 январь 2005 г., 0:06
 */

/**
 *
 * @author Eugene Stahov
 */

package ui;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class ImageList {
    
    public final static int ICON_INVISIBLE_INDEX=5*7+1;
    public final static int ICON_ERROR_INDEX=5*7+2;
    public final static int ICON_TRASHCAN_INDEX=5*7+3;
    public final static int ICON_RECONNECT_INDEX=5*7+4;
    
    public final static int ICON_MESSAGE_INDEX=6*7;
    public final static int ICON_GROUPCHAT_INDEX=6*7+1;
    public final static int ICON_EXPANDED_INDEX=6*7+2;
    public final static int ICON_COLLAPSED_INDEX=6*7+3;
    public final static int ICON_PROFILE_INDEX=7*7;
    
    Image resImage;
    int height,width,count;
    int total;
    /** Creates a new instance of ImageListC */
    public ImageList(String resource, int sizeX, int sizeY) {
        loadMulty(resource, sizeX, sizeY);
    }
    
    private void loadMulty
            (
            String resName, //!< Name of image in resouce
            int sizeX, int sizeY 
            ) 
    {
        try {
            resImage = Image.createImage(resName);
        } catch (Exception e) {
        }
        height = sizeY; //resImage.getHeight()/countY;
        width = sizeX;  //resImage.getWidth()/countX;
        this.count=resImage.getWidth()/width;
        total=count*(resImage.getHeight()/height);
    }
    public void drawImage(Graphics g, int index, int x, int y){
        int ho=g.getClipHeight();
        int wo=g.getClipWidth();
        int xo=g.getClipX();
        int yo=g.getClipY();
        
        int iy=y-height*(int)(index/count);
        int ix=x-width*(index%count);
        g.setClip(x,y, width,height);
        g.drawImage(resImage,ix,iy,Graphics.TOP|Graphics.LEFT);
        g.setClip(xo,yo, wo, ho);
    };
    
    public int getHeight() {return height;}
    public int getWidth() {return width;}
    public int getCount() {return total;}
}
