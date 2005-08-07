/*
 * SmilePicker.java
 *
 * Created on 6 Март 2005 г., 11:50
 */

package Client;
import ui.*;
import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 *
 * @author Eugene Stahov
 */
    
/**
 *
 * @author Eugene Stahov
 */
public class SmilePicker extends VirtualList implements CommandListener, VirtualElement{

/*#M55,M55_Release#*///<editor-fold>
//--    private final static int CURSOR_VOFFSET=1;
//--    private final static int CURSOR_HOFFSET=2;
/*$M55,M55_Release$*///</editor-fold>
/*#!M55,M55_Release#*///<editor-fold>
    private final static int CURSOR_HOFFSET=2;
    private final static int CURSOR_VOFFSET=2;
/*$!M55,M55_Release$*///</editor-fold>
    
    private int imgCnt;
    private int xCnt;
    private int xLastCnt;
    private int xCursor;
    private int lines;

    private int lineHeight;
    private int imgWidth;
    
    private ImageList il;
    
    private MessageEdit me;
    
    Command cmdBack=new Command("Back",Command.BACK,99);
    Command cmdOK=new Command("Select",Command.OK,1);

    /** Creates a new instance of SmilePicker */
    public SmilePicker(Display display, MessageEdit me) {
        super(display);
        this.me=me;
        
        il = StaticData.getInstance().smilesIcons;
        
        imgCnt=StaticData.getInstance().smileTable.size();
        //il.getCount();
        
        imgWidth=il.getWidth()+2*CURSOR_HOFFSET;
        lineHeight = il.getHeight()+2*CURSOR_VOFFSET;

        xCnt= getWidth() / imgWidth;
        
        lines=imgCnt/xCnt;
        xLastCnt=imgCnt-lines*xCnt;
        if (xLastCnt>0) lines++; else xLastCnt=xCnt;
        
        addCommand(cmdOK);
        addCommand(cmdBack);
        setCommandListener(this);
      
    }
    
    int smileIndex;
    //SmileItem si=new SmileItem();
    
    public int getItemCount(){ return lines; }
    public VirtualElement getItemRef(int index){ smileIndex=index; return this;}
    
    //private class SmileItem implements VirtualElement {
    public int getVWidth(){ return 0; }
    public int getVHeight() { return lineHeight; }
    public int getColor(){ return 0x000000; }
    public int getColorBGnd(){ return 0xFFFFFF; }
    public void onSelect(){
        try {
            me.addText(
                    (String)
                    StaticData.getInstance().
                    smileTable.elementAt(cursor*xCnt+xCursor)
                    );
        } catch (Exception e) { /*e.printStackTrace();*/  }
        destroyView();
    };
    
        
    public void drawItem(Graphics g, int ofs, boolean selected){
        int max=(smileIndex==lines-1)? xLastCnt:xCnt;
        for (int i=0;i<xCnt;i++) {
            il.drawImage(g, smileIndex*xCnt + i, i*imgWidth+CURSOR_HOFFSET, CURSOR_VOFFSET);
        }
    };
    
    //}
    public void drawCursor (Graphics g, int width, int height){
        int x=xCursor*imgWidth;
        g.translate(x,0);
        super.drawCursor(g, imgWidth, lineHeight);
        g.translate(-x,0);
    } 
    
    protected void keyLeft(){ 
        if (xCursor>0) xCursor--; 
        else {
            if (cursor==0) return;
            xCursor=xCnt-1;
            keyUp();
        }
    }
    protected void keyRight(){ 
        if ( xCursor < ( (cursor<lines-1)?(xCnt-1):(xLastCnt-1) ) ) 
            xCursor++; 
        else {
            if (cursor==lines-1) return;
            xCursor=0;
            keyDwn();
        }
    }
    protected void keyDwn(){
        super.keyDwn();
        if (cursor!=lines-1) return;
        if (xCursor >= xLastCnt) xCursor=xLastCnt-1;
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdBack) {
            destroyView();
            return;
        }
        if (c==cmdOK) { eventOk(); }
    }

    public void moveCursorEnd() {
        super.moveCursorEnd();
        xCursor=xLastCnt-1;
    }

    public void moveCursorHome() {
        super.moveCursorHome();
        xCursor=0;
    }
    
}
