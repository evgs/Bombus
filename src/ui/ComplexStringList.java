/*
 * ComplexIconTextList.java
 *
 * Created on 6 Февраль 2005 г., 17:56
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
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
public class ComplexStringList extends VirtualList
{

    protected Vector lines;
    //private ComplexStringDraw cld;
    
    /** Creates a new instance of ComplexIconTextList */
    public ComplexStringList(Display display) {
        super(display);
        cursor=-1;
        //cld=new ComplexStringDraw(il);
    }
    
    public void attachList(Vector list) {
        lines=list;
    }
    
    //public Font getFont() { return f;}
    // overriding base class methods
    protected int getItemCount(){
        if (lines==null) return 0;
        return lines.size();
    }
    
    protected VirtualElement getItemRef(int index){
       return getLine(index); 
    }
    
    protected int getItemHeight(int index){ 
        return getLine(index).getVHeight();
    }
    protected ComplexString cacheUpdate(int index) {return null;}
    
    protected int getItemWidth(int index){ 
        return getLine(index).getVWidth();
    }        

    protected void drawItem(int index, Graphics g, int ofs, boolean selected){
        getLine(index).drawItem(g, ofs, false);
    }

    private ComplexString getLine(int index){
        ComplexString line;
        try {
            line=(ComplexString)lines.elementAt(index);
        } catch (Exception e) { line=null; }
        if (line==null) line=cacheUpdate(index);
        return line;
    }
}
