/*
 * MessageItem.java
 *
 * Created on 21 январь 2006 г., 23:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Messages;

import Client.Msg;
import images.SmilesIcons;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import ui.ComplexString;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class MessageItem implements 
	VirtualElement,
	MessageParser.MessageParserNotify
{
    
    private Msg msg;
    private Vector msgLines;
    private int msgHeight;
    private VirtualList view;
    
    /** Creates a new instance of MessageItem */
    public MessageItem(Msg msg, VirtualList view) {
	this.msg=msg;
	this.view=view;
	MessageParser.getInstance().parseMsg(msg, SmilesIcons.getInstance(), view.getWidth(), false, this);
    }

	public int getVHeight() { return msgHeight; }

	public int getVWidth() { return 0; }

	public int getColorBGnd() { return 0xffffff; }

	public int getColor() { return msg.getColor(); }

	public void drawItem(Graphics g, int ofs, boolean selected) {
	    if (msgLines==null) return;
	    int y=0;
	    for (Enumeration e=msgLines.elements(); e.hasMoreElements(); ) {
		ComplexString line=(ComplexString) e.nextElement();
		int h=line.getVHeight();
		if (y>=0 && y<g.getClipHeight()) 
		    line.drawItem(g, 0, selected);
		g.translate(0, h);
	    }
	}

	public void onSelect() {
	}

	public void notifyRepaint(Vector v, Msg parsedMsg) {
	    msgLines=v;
	    int height=0;
	    for (Enumeration e=msgLines.elements(); e.hasMoreElements(); ) {
		ComplexString line=(ComplexString) e.nextElement();
		height+=line.getVHeight();
	    }
	    msgHeight=height;
	    view.redraw();
	}

	public void notifyUrl(String url) { }
}
