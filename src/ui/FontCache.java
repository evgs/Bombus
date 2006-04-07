/*
 * FontCache.java
 *
 * Created on 5 Февраль 2006 г., 3:15
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import javax.microedition.lcdui.Font;

/**
 *
 * @author Evg_S
 */
public class FontCache {

    private static Font normal;
    private static Font bold;
    private static Font msgFont;
    private static Font balloonFont;
    private static Font clockFont;
    
    public static int rosterFontSize=Font.SIZE_MEDIUM;
    public static int msgFontSize=Font.SIZE_MEDIUM;
    public static int balloonFontSize=Font.SIZE_SMALL;
    public static int clockFontSize=Font.SIZE_LARGE;

    public static Font getRosterNormalFont() {
        if (normal==null) {
            normal=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, rosterFontSize);
        }
        return normal;
    }
    
    public static Font getRosterBoldFont() {
        if (bold==null) {
            bold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, rosterFontSize);
        }
        return bold;
    }

    public static Font getMsgFont() {
        if (msgFont==null) {
            msgFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, msgFontSize);
        }
        return msgFont;
    }

    public static Font getBalloonFont() {
        if (balloonFont==null) {
            balloonFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, balloonFontSize);
        }
        return balloonFont;
    }

    public static void resetCache() {
        normal=bold=msgFont=balloonFont=null;
    }
    

    public static Font getClockFont() {
        if (clockFont==null) {
            clockFont=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, clockFontSize);
        }
        return clockFont;
    }
}
