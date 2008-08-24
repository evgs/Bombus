/*
 * Modules.java
 *
 * Created on 24 Август 2008 г., 17:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package modules;

import java.util.Vector;

/**
 *
 * @author evgs
 */
public class Modules {
    
    /** Creates a new instance of Modules */
    private Modules() {
    }
    
    private static Vector modules;
    
    public static boolean modprobe(final String modid) {
        if (modules==null) {
            modules=new util.StringLoader().stringLoader("/modules.txt", 1)[0];
        }
        
        for (int i=0; i<modules.size(); i++) {
            if (modid.equals(modules.elementAt(i))) return true;
        }
        
        return false;
    }
}
