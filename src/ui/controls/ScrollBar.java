/*
 * ScrollBar.java
 *
 * Created on 19 Ноябрь 2005 г., 21:26
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui.controls;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author EvgS
 */
public class ScrollBar {
    
    private static final int COLOR_SCROLL_PTR     =0x0033ff;
    private static final int COLOR_SCROLL_BGND    =0x888888;
    public static final int COLOR_BGND           =0xFFFFFF;
    private static final int WIDTH_SCROLL_1      =4;
    private static final int WIDTH_SCROLL_2      =10;
    
    private int size;
    private int windowSize;
    private int postion;
    
    private boolean hasPointerEvents;
    
    private int minimumHeight=3;
    private int scrollWidth=WIDTH_SCROLL_1;
    
    /** Creates a new instance of ScrollBar */
    public ScrollBar() {
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public void setHasPointerEvents(boolean hasPointerEvents) {
        this.hasPointerEvents = hasPointerEvents;
	scrollWidth=(hasPointerEvents)? WIDTH_SCROLL_2: WIDTH_SCROLL_1;
    }

    public int getScrollWidth() {
        return scrollWidth;
    }

    public void draw(Graphics g) {
	int drawHeight=g.getClipHeight();
	int drawWidth=g.getClipWidth();

	g.translate(drawWidth-scrollWidth, 0);

        g.setColor(COLOR_SCROLL_BGND);
	g.fillRect(1, 1, scrollWidth-2, drawHeight-2);
	
        g.setColor(COLOR_BGND);
        g.drawRect(0,0,scrollWidth-1,drawHeight-1);
            
	drawHeight-=minimumHeight;
        
	int scrollerSize=(drawHeight*windowSize)/size+minimumHeight;
	
	int scrollerPos=(drawHeight*postion)/size;
        g.setColor(COLOR_SCROLL_PTR);
        g.drawRect(0, scrollerPos, scrollWidth-1, scrollerSize);
    }
}
