/*
 * FontCache.java
 *
 * Created on 5 Февраль 2006 г., 3:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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

    public static Font getNormalFont() {
        if (normal==null) {
            normal=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        }
        return normal;
    }
    public static Font getBoldFont() {
        if (bold==null) {
            bold=Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        }
        return bold;
    }
}
