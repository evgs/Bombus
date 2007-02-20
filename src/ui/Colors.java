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

/**
 *
 * @author Evg_S
 */
public class Colors {
    public final static int BALLOON_INK          =0x000000;
    public final static int BALLOON_BGND         =0xffffe0;
    
//#if (!INVCOLORS)
    public static final int LIST_BGND           =0xFFFFFF;
    public static final int LIST_BGND_EVEN      =0xffeeff;
    public final static int LIST_INK            =0x000000;
    public final static int MSG_SUBJ            =0xa00000;
    public final static int MSG_HIGHLIGHT       =0x904090;
    
    public final static int DISCO_CMD           =0x000080;
    
    public static final int HEADER_BGND         =0x0033ff;
    public static final int HEADER_INK          =0x33ffff;
    
    public final static int CONTACT_DEFAULT     =0x000000;
    public final static int CONTACT_CHAT        =0x39358b;
    public final static int CONTACT_AWAY        =0x008080;
    public final static int CONTACT_XA          =0x535353;
    public final static int CONTACT_DND         =0x800000;
    
    public final static int GROUP_INK           =0x000080;
    
    public final static int BLK_INK             =0xffffff;
    public final static int BLK_BGND            =0x000000;

    public final static int MESSAGE_IN      =0x0000b0;
    public final static int MESSAGE_OUT     =0xb00000;
    public final static int MESSAGE_PRESENCE=0x006000;
    public final static int MESSAGE_AUTH    =0x400040;
    public final static int MESSAGE_HISTORY =0x535353;

    public final static int PGS_REMAINED        =0xffffff;
    public final static int PGS_COMPLETE        =0x0000ff;
    public final static int PGS_BORDER          =0x808080;
    public final static int PGS_BGND            =0x000000;
    
    public final static int HEAP_TOTAL          =0xffffff;
    public final static int HEAP_FREE           =0x00007f;
//#else
//#     public static final int LIST_BGND           =0x000000;
//#     public static final int LIST_BGND_EVEN      =0x111111;
//#     public final static int LIST_INK            =0xffffff;
//#     public final static int MSG_SUBJ            =0x00FFFF;
//#     public final static int MSG_HIGHLIGHT       =0xFF80FF;
//#     
//#     public final static int DISCO_CMD           =0x00ffff;
//#     
//#     public static final int HEADER_BGND         =0x000080;
//#     public static final int HEADER_INK          =0x33ffff;
//#     
//#     public final static int CONTACT_DEFAULT     =0xFFFFFF;
//#     public final static int CONTACT_CHAT        =0x39358b;
//#     public final static int CONTACT_AWAY        =0x808080;
//#     public final static int CONTACT_XA          =0x535353;
//#     public final static int CONTACT_DND         =0xff0000;
//#     
//#     public final static int GROUP_INK           =0x00ffff;
//#     
//#     public final static int BLK_INK             =0xffffff;
//#     public final static int BLK_BGND            =0x000000;
//# 
//#     public final static int MESSAGE_IN          =0x8080FF;
//#     public final static int MESSAGE_OUT         =0xFF8080;
//#     public final static int MESSAGE_PRESENCE    =0x00FF80;
//#     public final static int MESSAGE_AUTH        =0x3070FF;
//#     public final static int MESSAGE_HISTORY     =0xA0A0A0;
//# 
//#     public final static int PGS_REMAINED        =0xffffff;
//#     public final static int PGS_COMPLETE        =0x0000ff;
//#     public final static int PGS_BORDER          =0x808080;
//#     public final static int PGS_BGND            =0x000000;
//#     
//#     public final static int HEAP_TOTAL          =0xffffff;
//#     public final static int HEAP_FREE           =0x00007f;    
//#endif

//#if (INVCOLORS)
//#     public static final int CURSOR_BGND    =0x101030;
//#     public static final int CURSOR_OUTLINE =0x8080ff;
//#elif !(MIDP1) 
    public static final int CURSOR_BGND    =0xC8D7E6;
    public static final int CURSOR_OUTLINE =0x000066;
//#else
//#     public static final int CURSOR_BGND    =0x00ff00;
//#     public static final int CURSOR_OUTLINE =CURSOR_BGND;
//#endif

//#if !(MIDP1)
    public static final int SCROLL_BRD     =0x000000;
    public static final int SCROLL_BAR     =0xBBBBBB;
    public static final int SCROLL_BGND    =0xDDDDDD;
//#else
//#     public static final int SCROLL_BRD     =0x0033ff;
//#     public static final int SCROLL_BAR     =0xBBBBBB;
//#     public static final int SCROLL_BGND    =0xBBBBBB;
//#endif
    
}
