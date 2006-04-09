/*
 * VirtualList.java
 *
 * Created on 30 ������ 2005 �., 14:46
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;
import java.util.*;
import Client.*;
import ui.controls.Balloon;
import ui.controls.ScrollBar;

/**
 * ������������ ������ ����������� ���������.
 * ����� ��������� ���������� �������, ���������,
 * ����� ��������� ������������ �� ������ ���������.
 * @author Eugene Stahov
 */
public abstract class VirtualList         
        extends Canvas 
{
    
    /**
     * ������� "������ ������� �������"
     * � ������ VirtualList ���������� ������� �� ��������� ��������, ����������
     * �������������� (override) ������� ��� ���������� ����������� ��������
     * @param index ������ ����������� ��������
     */
    public void focusedItem(int index) {}


    /**
     * ����� ��������� ������������ ������
     * ��� ������� �����������, ������ ���� �������������� ��� ������������
     * @return ����� ��������� ������, �������� ���������
     */
    abstract protected int getItemCount();

    /**
     * ������� ������������ ������
     * ��� ������� �����������, ������ ���� �������������� ��� ������������
     * @param index ����� �������� ������. �� ����������� ��������, ����������� getItemCount()
     * @return ������ �� ������� � ������� index.
     */
    abstract protected VirtualElement getItemRef(int index);
    
    /**
     * ���� ���� ���������
     * @return RGB-���� ���� ���������
     */
    protected int getTitleBGndRGB() {return Colors.HEADER_BGND;} 

    /**
     * ���� ������ ���������
     * @return RGB-���� ������ ���������
     */
    protected int getTitleRGB() {return Colors.HEADER_INK;} 
    
    /**
     * ������� "������� ������ ��"
     * ������� ���������� VirtualList �������� ������� onSelect ��� ���������� ��������; 
     * ���������� �������������� (override) ������� ��� ���������� �������� ��������
     */
    public void eventOk(){
        try {
            ((VirtualElement)getFocusedObject()).onSelect();
            //fitCursor();
        } catch (Exception e) { e.printStackTrace();} 
    }
    
    /**
     * ���������� �������������� ������. ���������� � ������, ���� ��� ������ 
     * �� ��� ��������� �������� key(keyCode)
     * ���������� �������������� (override) ������� ��� ���������� ����������� ��������     
     * @param keyCode ��� �������
     */
    public void userKeyPressed(int keyCode){}
    
    //////////////////////////////////

    public static final int SIEMENS_GREEN=-11;
    public static final int NOKIA_GREEN=-10;
    public static final int MOTOROLA_GREEN=-10;
    public static final int SE_GREEN=0;
    
    public static int greenKeyCode=SIEMENS_GREEN;
    public static boolean fullscreen=false;
    public static boolean memMonitor;
    
    /** ������� ������ */
    int width;
    int height;
    
    /** �������� ����� ��� ������� ���������. ������������, ���� ��������� 
     * �� ������������ ������� ����������� ������
     */
    private Image offscreen;
    
    /** ������� ��������� ������� � ����� ������ */
    protected boolean atEnd; //FIXME: ��������� ��������� � ������� keyRight();
    
    protected int cursor;
    
    /** 
     * ���� ��������� � ������� ������� 
     * �������������:
     *   ��� ������� ������ ����������� �������
     *   ��� ������ �������� �������� ������
     * ������������:
     *   ��� ������������� ���������
     */
    protected boolean stickyWindow=true;
    
    private int itemLayoutY[]=new int[1];
    private int listHeight;
    
    protected void updateLayout(){
        int size=getItemCount();
        if (size==0) return;
        int layout[]=new int[size+1];
        int y=0;
        for (int index=0; index<size; index++){
            y+=getItemRef(index).getVHeight();
            layout[index+1]=y;
        };
        listHeight=y;
        itemLayoutY=layout;
    }
    protected int getElementIndexAt(int yPos){
        // ������� �������
        int end=getItemCount()-1;
        if (end<0) return -1;
        int begin=0;
        while (end-begin>1) {
            int index=(end+begin)/2;
            if (yPos<itemLayoutY[index]) end=index; else begin=index;
        }
        return begin;
    }
    
    public int win_top;    // ������� ������� ���� ������������ ������
    private int winHeight;  // ������������ ������ ������
    //int full_items; // ��������� ���������� � ����
    protected int offset;     // ������ ��������������
    
    protected boolean showBalloon;
    
    protected VirtualElement title;
    
    private boolean wrapping = true;

    /** ������� ������� ��������� ������ - ���� ������������ touchscreen */
    private int itemBorder[];
    /** ��������� doubleclick */
    private int lastClickY;
    private int lastClickItem;
    private long lastClickTime;
    
    /**
     * ��������� ������������� ������ � ������ (������� ������� ����� ����� ������)
     * �� ��������� ���������� true
     * @param wrap ������ ���������� true, ��������� ������� ������� ����� ����� ������
     */
    public void enableListWrapping(boolean wrap) { this.wrapping=wrap; }
    
    /**
     * ������ �� ��������� ������
     * @return ������ ���� ComplexString
     */
    public ComplexString getTitleItem() {return (ComplexString)title;}
    public void setTitleItem(ComplexString title) { this.title=title; }
    
    /**
     * ���������� ������ �� ������ � ������. 
     * � ������ VirtualList ���������� VirtualElement, �� ������� ��������� ������,
     * ������, �������� �������������� ������� ��� ������������
     * @return ������ �� ������ � ������.
     */
    public Object getFocusedObject() { 
        try {
            return getItemRef(cursor);
        } catch (Exception e) { }
        return null;
    }    

    protected Display display;
    protected Displayable parentView;

    ScrollBar scrollbar;
    /** Creates a new instance of VirtualList */
    public VirtualList() {
        width=getWidth();
        height=getHeight();
        // rotator
        rotator=new TimerTaskRotate(0);
//#if !(MIDP1)
        //addCommand(cmdSetFullScreen);
        setFullScreenMode(fullscreen);
//#endif
	
	itemBorder=new int[32];
	
	scrollbar=new ScrollBar();
	scrollbar.setHasPointerEvents(hasPointerEvents());
    }

    /** Creates a new instance of VirtualList */
    public VirtualList(Display display) {
        this();

        attachDisplay(display);
    }
    
    /**
     * ����������� ����������� ������������� �������, ������������� � ���������
     * ������� � ����������� � ������� ������������ ������ (this) 
     * @param display �������� ������� ���������� ���������� {@link }
     */
    public void attachDisplay (Display display) {
        if (this.display!=null) return;
        this.display=display;
        parentView=display.getCurrent();
        display.setCurrent(this);
        redraw();
    }


    /** ������ ���������� ��������� ��������� Canvas */
    public void redraw(){
        //repaint(0,0,width,height);
        Displayable d=display.getCurrent();
        //System.out.println(d.toString());
        if (d instanceof Canvas) {
            ((Canvas)d).repaint();
        }
    }

    /** ���������� ����� ������� VirtualList. �������������� ����������� ����� 
     * Canvas.hideNotify(). �������� �� ��������� - ������������ ��������� 
     * ������ offscreen, ������������� ��� ������ ��� �������������� ������� �����������
     */
    protected void hideNotify() {
	offscreen=null;
    }
    
    /** ���������� ����� ������� ��������� VirtualList. �������������� ����������� ����� 
     * Canvas.showNotify(). �������� �� ��������� - �������� ��������� 
     * ������ offscreen, ������������� ��� ������ ��� �������������� ������� �����������
     */
    protected void showNotify() {
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
    }
    
    /** ���������� ��� ��������� ������� ������������ �������. �������������� ����������� ����� 
     * Canvas.sizeChanged(int width, int heigth). ��������� ����� ������� ������� ���������.
     * ����� ������ ����� �������� ����� offscreen, ������������ ��� ������ ��� �������������� 
     * ������� �����������
     */
//#if !(MIDP1)
    protected void sizeChanged(int w, int h) {
        width=w;
        height=h;
	if (!isDoubleBuffered()) 
	    offscreen=Image.createImage(width, height);
    }
//#endif
    
    /**
     * ������ ��������� ������.
     * ������� ���������� ����� ���������� ������, 
     * ����� ������ ����������� � ��������� ������.
     *
     * � ������ VirtualList ������� �� ��������� ������� ��������, ����������
     * �������������� (override) ������� ��� ���������� ����������� ��������
     */
    protected void beginPaint(){};
    
    /**
     * ���������
     */
    public void paint(Graphics graphics) {
	Graphics g=(offscreen==null)? graphics: offscreen.getGraphics();
        // ��������� ����
        
        beginPaint();
        
        int list_top=0; // ������� ������� ������
        if (title!=null) {
            list_top=title.getVHeight();
            g.setClip(0,0, width, list_top);
            g.setColor(getTitleBGndRGB());
            g.fillRect(0,0, width, list_top);
            g.setColor(getTitleRGB());
            title.drawItem(g,0,false);
        }

        drawHeapMonitor(g);
        winHeight=height-list_top;

        updateLayout(); //fixme: ������ ��� ��������� ������

        itemBorder[0]=list_top;
        
        int count=getItemCount(); // ������ ������
        
        boolean scroll=(listHeight>winHeight);

        if (count==0) {
            cursor=(cursor==-1)?-1:0; 
            win_top=0;
        }

        if (count>0 && stickyWindow) fitCursor();
        
        int itemMaxWidth=(scroll) ?(width-scrollbar.getScrollWidth()) : (width);
        // �������� ����
        // ���������
        int itemIndex=getElementIndexAt(win_top);
        int displayedIndex=0;
        int displayedBottom=list_top;
   
        int baloon=-1;
        atEnd=false;
        int itemYpos;
        try {
            // try ������ �������� �� ����� ������
            while ((itemYpos=itemLayoutY[itemIndex]-win_top)<winHeight) {
                
                VirtualElement el=getItemRef(itemIndex);
                
                boolean sel=(itemIndex==cursor);
                
                int lh=el.getVHeight();
                
                // ���� ������
                setAbsOrg(g, 0, list_top);
                g.setClip(0,0, itemMaxWidth, winHeight);    
                
                g.translate(0,itemYpos);
                
                g.setColor(el.getColorBGnd());
                if (sel) {
                    drawCursor(g, itemMaxWidth, lh); 
                    baloon=g.getTranslateY();
                } else
                    g.fillRect(0,0, itemMaxWidth, lh);

                g.setColor(el.getColor());
                
                g.clipRect(0, 0, itemMaxWidth, lh);
                el.drawItem(g, (sel)?offset:0, sel);
                
                itemIndex++;
		displayedBottom=itemBorder[++displayedIndex]=list_top+itemYpos+lh;
            }
        } catch (Exception e) { e.printStackTrace(); atEnd=true; }

        // ������� ������� ����
        int clrH=height-displayedBottom;
        if (clrH>0) {
            setAbsOrg(g, 0,displayedBottom);
            g.setClip(0, 0, itemMaxWidth, clrH);
            g.setColor(Colors.LIST_BGND);
            //g.setColor(VL_CURSOR_OUTLINE);
            g.fillRect(0, 0, itemMaxWidth, clrH);
        }

        // ��������� ����������
        //g.setColor(VL_BGND);
        if (scroll) {
	    
            setAbsOrg(g, 0, list_top);
            g.setClip(0, 0, width, winHeight);

	    scrollbar.setPostion(win_top);
	    scrollbar.setSize(listHeight);
	    scrollbar.setWindowSize(winHeight);
	    
	    scrollbar.draw(g);
        } else scrollbar.setSize(0);

        setAbsOrg(g, 0, 0);
        g.setClip(0,0, width, height);
        if (showBalloon) {
            String text=null;
            try {
                text=((VirtualElement)getFocusedObject()).getTipString();
            } catch (Exception e) { }
            if (text!=null)
                drawBalloon(g, baloon, text);
        }

	if (offscreen!=null) graphics.drawImage(offscreen, 0,0, Graphics.TOP | Graphics.LEFT );
	//full_items=fe;
    }

    protected void drawBalloon(final Graphics g, int balloon, final String text) {
        setAbsOrg(g,0,balloon);
        Balloon.draw(g, text);
    }

    private void drawHeapMonitor(final Graphics g) {
        if (memMonitor) {
            int ram=(int)((Runtime.getRuntime().freeMemory()*32)/Runtime.getRuntime().totalMemory());
            g.setColor(0xffffff);  g.fillRect(width-34,0,34,3);
            g.setColor(0x00007f);  g.fillRect(width-33,1,ram,2);
        }
    }
    
    
    /**
     * ������� ��������� (0.0) � ���������� ���������� (x,y)
     * @param g ����������� �������� ���������
     * @param x ���������� x-���������� ������ ������ ��������� 
     * @param y ���������� y-���������� ������ ������ ���������
     */
    private void setAbsOrg(Graphics g, int x, int y){
        g.translate(x-g.getTranslateX(), y-g.getTranslateY());
    }
    
   
    /**
     * ����������� ������� � ������ ������
     */
    public void moveCursorHome(){
        stickyWindow=true;
        //win_top=0;
        if (cursor>0) {
            cursor=0;
            //focusedItem(0);
        }
        setRotator();
    }

    /**
     * ����������� ������� � ����� ������
     */
    public void moveCursorEnd(){
        stickyWindow=true;
        int count=getItemCount();
        //win_top=count-visibleItemsCnt(count-1, -1);
        if (cursor>=0) {
            cursor=(count==0)?0:count-1;
            //focusedItem(cursor);
        }
        //win_top=(listHeight>winHeight)? listHeight-winHeight:0;
        setRotator();
    }

    /**
     * ����������� ������� � ��������������� �������
     * @param index ������� ������� � ������
     */
    public void moveCursorTo(int index, boolean force){
        int count=getItemCount();
        if (index>=count) index=count-1;    // ���� �� ��������� ���������, �� ����������� �� ����
        //else if ((!force) && stickyWindow) return;
        
        cursor=index;
        stickyWindow=true;
        
        repaint();
        //moveCursor(index-cursor, force); 
    }
    
    protected void fitCursor(){
        //�������� �� ������� �������
        try {
            int top=itemLayoutY[cursor];
            if (top<win_top) win_top=top;
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                // ������ ���������� �� ������ - �������� � ������ �������
                int bottom=itemLayoutY[cursor+1]-winHeight;
                if (bottom>win_top) win_top=bottom;
            }
            if (top>=win_top+winHeight) win_top=top;
        } catch (Exception e) { e.printStackTrace(); }
    }

    /*public void moveCursorTo(Object focused){
        int count=getItemCount();
        for (int index=0;index<count;index++){
            if (focused==getItemRef(index)) {
                moveCursorTo(index);
                break;
            }
        }
    }
     */
    /** ��� ������������ ������ */
    protected int kHold;
    
    protected void keyRepeated(int keyCode){ key(keyCode); }
    protected void keyReleased(int keyCode) { kHold=0; }
    protected void keyPressed(int keyCode) { kHold=0; key(keyCode);  }
    
    protected void pointerPressed(int x, int y) { 
	if (scrollbar.pointerPressed(x, y, this)) {
            stickyWindow=false;
            return;
        } 
	int i=0;
	while (i<32) {
	    if (y<itemBorder[i]) break;
	    i++;
	}
	if (i==0 || i==32) return;
	//System.out.println(i);
	if (cursor>=0) {
            moveCursorTo(getElementIndexAt(win_top)+i-1, true);
            setRotator();
        }
	
	long clickTime=System.currentTimeMillis();
	if (cursor==lastClickItem)
	    if (lastClickY-y<5 && y-lastClickY<5) 
		if (clickTime-lastClickTime<500){
		    y=0;    // ������ "�������� �����"
		    eventOk();
		}
	lastClickTime=clickTime;
	lastClickY=y;
	lastClickItem=cursor;
        
        // ������� ������� ����������� �������
        int il=itemLayoutY[cursor+1]-winHeight;
        if (il>win_top) win_top=il;
        il=itemLayoutY[cursor];
        if (il<win_top) win_top=il;
        
	repaint();
    }
    protected void pointerDragged(int x, int y) { 
        if (scrollbar.pointerDragged(x, y, this)) stickyWindow=false; 
    }
    protected void pointerReleased(int x, int y) { scrollbar.pointerReleased(x, y, this); }
    
    /**
     * ��������� ����� ������
     * @param keyCode ��� ������� ������
     */
    private void key(int keyCode) {
        switch (keyCode) {
            case 0: break;
            case KEY_NUM1:  { moveCursorHome();    break; }
            case KEY_NUM7:  { moveCursorEnd();     break; }
            case '5':{ eventOk(); break; }
            default:
                switch (getGameAction(keyCode)){
                    case UP:    { keyUp(); break; }
                    case DOWN:  { keyDwn(); break; }
                    case LEFT:  { keyLeft(); break; }
                    case RIGHT: { keyRight(); break; }
                    case FIRE:  { eventOk(); break; }
                    default: 
                        if (keyCode==greenKeyCode) { keyGreen(); break; }
                        userKeyPressed(keyCode);
                }
        }

        
        repaint();
    }
    
    /**
     * ������� "������� ������ UP"
     * � ������ VirtualList ������� ���������� ������ �� ���� ������� �����.
     * �������� �������������� (override) ������� ��� ���������� ����������� ��������
     */
    public void keyUp() {
	 
        if (cursor==0) {
            if (wrapping)  moveCursorEnd(); else itemPageUp();
            setRotator();
            return;
        }
        /*
        if (itemLayoutY[cursor]<win_top) {
            //������� ������� �������� �� �� �� ������
            win_top-=winHeight;
            if (win_top<0) win_top=0;
        } else {
            cursor--;
            if (getItemRef(cursor).getVHeight()>winHeight) {
                // ���� ������� �� ����� ����������� �� ������, ���������� �� bottom
                win_top=itemLayoutY[cursor+1]-winHeight;
            } else if (win_top>itemLayoutY[cursor]) {
                win_top=itemLayoutY[cursor];
            }
        }
         */
        if (itemPageUp()) return;
        stickyWindow=true;
        cursor--;
        setRotator();
    }
    
    /**
     * ������� "������� ������ DOWN"
     * � ������ VirtualList ������� ���������� ������ �� ���� ������� �����.
     * �������� �������������� (override) ������� ��� ���������� ����������� ��������
     */
    
    public void keyDwn() { 
	if (cursor==getItemCount()-1) 
        { 
            if (wrapping) moveCursorHome(); else itemPageDown();
            setRotator();
            return; 
        }
        /*if (itemLayoutY[cursor+1]>win_top+winHeight) {
            // ������ ������� �������� �������� �� �� �� ������
            win_top+=winHeight; // ���� - pagedown
        } else {
            cursor++;
            if (getItemRef(cursor).getVHeight()>winHeight) {
                // ���� ������� �� ����� ����������� �� ������, ���������� �� top
                win_top=itemLayoutY[cursor];
            } else if (win_top+winHeight<itemLayoutY[cursor+1]) {
                win_top=itemLayoutY[cursor+1]-winHeight;
            }
        }*/
        if (itemPageDown()) return;
        stickyWindow=true; 
        cursor++;
        setRotator();
    }
    
    private boolean itemPageDown() {
        try {
            stickyWindow=false;
            // ������ ���������� ��������� �� ������?
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }
            
            // ������ �� ������ ����? (�� ����� �� ����� ��������)
            if (itemLayoutY[cursor]>=win_top+winHeight) return false;
            
            int remainder=itemLayoutY[cursor+1]-win_top;
            // ����� ��������� ��� �� ������?
            if (remainder<=winHeight) return false;
            // ����� ��������� �� ��������� ������?
            if (remainder<=2*winHeight) {
                win_top=remainder-winHeight+win_top+8;
                return true;
            }
            win_top+=winHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    
    private boolean itemPageUp() {
        try {
            stickyWindow=false;
            // ������ ���������� ��������� �� ������?
            if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) {
                stickyWindow=true;
                return false;
            }
            
            // ������ �� ������ ����? (�� ����� �� ����� ��������)
            if (itemLayoutY[cursor+1]>=win_top+winHeight) return false;
            
            int remainder=win_top-itemLayoutY[cursor];
            // ����� ��������� ��� �� ������?
            if (remainder<0) return false;
            // ����� ��������� �� ��������� ������?
            if (remainder<=winHeight) {
                win_top=itemLayoutY[cursor];
                return true;
            }
            win_top-=winHeight;
            return true;
        } catch (Exception e) {}
        return false;
    }
    /**
     * ������� "������� ������ LEFT"
     * � ������ VirtualList ������� ���������� ������ �� ���� �������� �����.
     * �������� �������������� (override) ������� ��� ���������� ����������� ��������
     */
    public void keyLeft() {
        try {
            stickyWindow=false;
            win_top-=winHeight;
            if (win_top<0) {
                win_top=0;
                cursor=0;
            }
            if (!cursorInWindow()) {
                cursor=getElementIndexAt(itemLayoutY[cursor]-winHeight);
                if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) fitCursor();
            }
            setRotator();
        } catch (Exception e) {};
    }

    /**
     * ������� "������� ������ RIGHT"
     * � ������ VirtualList ������� ���������� ������ �� ���� �������� ����.
     * �������� �������������� (override) ������� ��� ���������� ����������� ��������
     */
    public void keyRight() { 
        try {
            stickyWindow=false;
            win_top+=winHeight;
            int endTop=listHeight-winHeight;
            if (endTop<win_top) {
                win_top=endTop;
                cursor=getItemCount()-1;
            } else
                if (!cursorInWindow()) {
                    cursor=getElementIndexAt(itemLayoutY[cursor]+winHeight);
                    if (((VirtualElement)getFocusedObject()).getVHeight()<=winHeight) fitCursor();
                }
            setRotator();
        } catch (Exception e) {};
    }
    
    public boolean cursorInWindow(){
        try {
            int y1=itemLayoutY[cursor]-win_top;
            int y2=itemLayoutY[cursor+1]-win_top;
            if (y1<0 && y2>0 && y2<winHeight) return true;
            if (y1>=0 && y1<winHeight) return true;
        } catch (Exception e) { }
        return false;
    }
    
    /**
     * ������� "������� ��˨��� ������"
     * � ������ VirtualList ������� ��������� ����� eventOk().
     * �������� �������������� (override) ������� ��� ���������� ����������� ��������
     */
    protected void keyGreen() { eventOk(); }
    
    /** ���������� ������� ��������� ������� ����� */
    protected  void setRotator(){
        focusedItem(cursor);
        rotator.destroyTask();
        if (getItemCount()<1) return;
        if (cursor>=0) {
            int itemWidth=getItemRef(cursor).getVWidth();
            if (itemWidth>=width-scrollbar.getScrollWidth() ) itemWidth-=width/2; else itemWidth=0;
            rotator=new TimerTaskRotate( itemWidth );
        }
    }
    // cursor rotator
    
    private class TimerTaskRotate extends TimerTask{
        private Timer t;
        private int Max;
        private int balloon;
        
        public TimerTaskRotate(int max){
            offset=0;
            balloon=6;
            //if (max<1) return;
            Max=max;
            t=new Timer();
            t.schedule(this, 2000, 300);
        }
        public void run() {
            // ��������� ������ ���
            //stickyWindow=false;
            
            if (Max==-1 && balloon==-1) cancel();
            if (offset>=Max) {
                Max=-1;
                offset=0;
            } else offset+=20;
            
            if (showBalloon=balloon>=0) balloon--;
            redraw();
            //System.out.println("Offset "+offset);
        }
        public void destroyTask(){
            offset=0;
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
    private TimerTaskRotate rotator;

    
    /**
     * ��������� �������������� �������
     * @param g ����������� �������� ���������
     * @param width ������ �������
     * @param height ������ �������
     */
    protected void drawCursor (Graphics g, int width, int height){
        //g.setColor(VL_CURSOR_SHADE);   g.drawRoundRect(x+2, y+2, width-1, height-1, 3,3);
        g.setColor(Colors.CURSOR_BGND);    g.fillRect(1, 1, width-1, height-1);
        g.setColor(Colors.CURSOR_OUTLINE); g.drawRect(0, 0, width-1, height-1);
        /*
        g.drawLine(1,0,width-2,0);
        g.drawLine(0,1,0,height-2);
        g.drawLine(0,width-1,0,height-2);
        g.drawLine(1,height-1,width-2,height-1);
         */
    }

    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    /**
     * ������������ �� ��������� ������� �������� ������������ ������, 
     * ������������� � ��������� ����������� Displayable
     */
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    public int getListWidth() {
        return width-scrollbar.getScrollWidth();
    }

}
