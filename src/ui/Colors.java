/*
 * Colors.java
 *
 * Created on 4.02.2006, 22:26
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

//#if COLOR_THEMES
import Client.Config;
import java.util.Hashtable;
import util.StringLoader;
//#endif
/**
 *
 * @author Evg_S
 */
public class Colors {

//#if COLOR_THEMES
    private static Hashtable theme;
    private static String themeFile;
//#endif
    
    private Colors() {}
    
    static int strong(int color) {
        if (color==MESSAGE_IN) return MESSAGE_IN_S;
        if (color==MESSAGE_OUT) return MESSAGE_OUT_S;
        if (color==MESSAGE_PRESENCE) return MESSAGE_PRESENCE_S;
        return color;
    }
    
//#if !(COLOR_THEMES)
//#     //37 colors total
//#     public final static int BALLOON_INK         =0x000000;
//#     public final static int BALLOON_BGND        =0xffffe0;
//#     
//#     public final static int LIST_BGND           =0xFFFFFF;
//#     public final static int LIST_BGND_EVEN      =0xffeeff;
//#     public final static int LIST_INK            =0x000000;
//#     public final static int MSG_SUBJ            =0xa00000;
//#     public final static int MSG_HIGHLIGHT       =0x904090;
//#     
//#     public final static int DISCO_CMD           =0x000080;
//#     
//#     public final static int HEADER_BGND         =0x0033ff;
//#     public final static int HEADER_INK          =0x33ffff;
//#     
//#     public final static int CONTACT_DEFAULT     =0x000000;
//#     public final static int CONTACT_CHAT        =0x39358b;
//#     public final static int CONTACT_AWAY        =0x008080;
//#     public final static int CONTACT_XA          =0x535353;
//#     public final static int CONTACT_DND         =0x800000;
//#     
//#     public final static int GROUP_INK           =0x000080;
//#     
//#     public final static int BLK_INK             =0xffffff;
//#     public final static int BLK_BGND            =0x000000;
//# 
//#     public final static int MESSAGE_IN          =0x0000b0;
//#     public final static int MESSAGE_IN_S        =0x0060ff;
//#     public final static int MESSAGE_OUT         =0xb00000;
//#     public final static int MESSAGE_OUT_S       =0xff4000;
//#     public final static int MESSAGE_PRESENCE    =0x006000;
//#     public final static int MESSAGE_PRESENCE_S  =0x00c040;
//#     public final static int MESSAGE_AUTH        =0x400040;
//#     public final static int MESSAGE_HISTORY     =0x535353;
//# 
//#     public final static int PGS_REMAINED        =0xffffff;
//#     public final static int PGS_COMPLETE        =0x0000ff;
//#     public final static int PGS_BORDER          =0x808080;
//#     public final static int PGS_BGND            =0x000000;
//#     
//#     public final static int HEAP_TOTAL          =0xffffff;
//#     public final static int HEAP_FREE           =0x00007f;
//#     
//#     public final static int CURSOR_BGND         =0xC8D7E6;
//#     public final static int CURSOR_OUTLINE      =0x000066;
//#     
//#     public final static int SCROLL_BRD          =0x000000;
//#     public final static int SCROLL_BAR          =0xBBBBBB;
//#     public final static int SCROLL_BGND         =0xDDDDDD;
//#     
//#     public static void initColors(){};
//#     
//#else
    public static int BALLOON_INK;
    public static int BALLOON_BGND;
    
    public static int LIST_BGND;
    public static int LIST_BGND_EVEN;
    public static int LIST_INK;
    public static int MSG_SUBJ;
    public static int MSG_HIGHLIGHT;
    
    public static int DISCO_CMD;
    
    public static int HEADER_BGND;
    public static int HEADER_INK;
    
    public static int CONTACT_DEFAULT;
    public static int CONTACT_CHAT;
    public static int CONTACT_AWAY;
    public static int CONTACT_XA;
    public static int CONTACT_DND;
    
    public static int GROUP_INK;
    
    public static int BLK_INK;
    public static int BLK_BGND;

    public static int MESSAGE_IN;
    public static int MESSAGE_IN_S;
    public static int MESSAGE_OUT;
    public static int MESSAGE_OUT_S;
    public static int MESSAGE_PRESENCE;
    public static int MESSAGE_PRESENCE_S;
    public static int MESSAGE_AUTH;
    public static int MESSAGE_HISTORY;
    
    public static int PGS_REMAINED;
    public static int PGS_COMPLETE;
    public static int PGS_BORDER;
    public static int PGS_BGND;
    
    public static int HEAP_TOTAL;
    public static int HEAP_FREE;

    public static int CURSOR_BGND;
    public static int CURSOR_OUTLINE;

    public static int SCROLL_BRD;
    public static int SCROLL_BAR;
    public static int SCROLL_BGND;
    
    public static void initColors(){
         BALLOON_INK = loadColor("BALLOON_INK", 0x000000);
         BALLOON_BGND = loadColor("BALLOON_BGND", 0xffffe0);
        
         LIST_BGND = loadColor("LIST_BGND", 0xFFFFFF);
         LIST_BGND_EVEN = loadColor("LIST_BGND_EVEN", 0xffeeff);
         LIST_INK = loadColor("LIST_INK", 0x000000);
         MSG_SUBJ = loadColor("MSG_SUBJ", 0xa00000);
         MSG_HIGHLIGHT = loadColor("MSG_HIGHLIGHT", 0x904090);
        
         DISCO_CMD = loadColor("DISCO_CMD", 0x000080);
        
         HEADER_BGND = loadColor("HEADER_BGND", 0x0033ff);
         HEADER_INK = loadColor("HEADER_INK", 0x33ffff);
        
         CONTACT_DEFAULT = loadColor("CONTACT_DEFAULT", 0x000000);
         CONTACT_CHAT = loadColor("CONTACT_CHAT", 0x39358b);
         CONTACT_AWAY = loadColor("CONTACT_AWAY", 0x008080);
         CONTACT_XA = loadColor("CONTACT_XA", 0x535353);
         CONTACT_DND = loadColor("CONTACT_DND", 0x800000);
        
         GROUP_INK = loadColor("GROUP_INK", 0x000080);
        
         BLK_INK = loadColor("BLK_INK", 0xffffff);
         BLK_BGND = loadColor("BLK_BGND", 0x000000);
        
         MESSAGE_IN = loadColor("MESSAGE_IN", 0x0000b0);
         MESSAGE_IN_S = loadColor("MESSAGE_IN_S", 0x0060ff);
         MESSAGE_OUT = loadColor("MESSAGE_OUT", 0xb00000);
         MESSAGE_OUT_S = loadColor("MESSAGE_OUT_S", 0xff4000);
         MESSAGE_PRESENCE = loadColor("MESSAGE_PRESENCE", 0x006000);
         MESSAGE_PRESENCE_S = loadColor("MESSAGE_PRESENCE_S", 0x00c040);
         MESSAGE_AUTH = loadColor("MESSAGE_AUTH", 0x400040);
         MESSAGE_HISTORY = loadColor("MESSAGE_HISTORY", 0x535353);
        
         PGS_REMAINED = loadColor("PGS_REMAINED", 0xffffff);
         PGS_COMPLETE = loadColor("PGS_COMPLETE", 0x0000ff);
         PGS_BORDER = loadColor("PGS_BORDER", 0x808080);
         PGS_BGND = loadColor("PGS_BGND", 0x000000);
        
         HEAP_TOTAL = loadColor("HEAP_TOTAL", 0xffffff);
         HEAP_FREE = loadColor("HEAP_FREE", 0x00007f);
        
         CURSOR_BGND = loadColor("CURSOR_BGND", 0xC8D7E6);
         CURSOR_OUTLINE = loadColor("CURSOR_OUTLINE", 0x000066);
        
         SCROLL_BRD = loadColor("SCROLL_BRD", 0x000000);
         SCROLL_BAR = loadColor("SCROLL_BAR", 0xBBBBBB);
         SCROLL_BGND = loadColor("SCROLL_BGND", 0xDDDDDD);
         
         theme=null;
    }
    
   
    private static int loadColor(String key, int defaultColor) {
        if (theme==null) {
            String themeFile=Config.getInstance().themeFileName();
            if (themeFile==null) theme=new Hashtable();
            else theme=new StringLoader().hashtableLoader(themeFile);
            System.out.println("Loading color theme "+themeFile);
        }
        try {
            String value=(String)theme.get(key);
            return Integer.parseInt(value.substring(2),16);
        } catch (Exception e) { 
            System.out.println("Can't find:"+key); //colors debug
            return defaultColor; 
        }
    }
//#endif
}
