/*
 * Version.java
 *
 * Created on 23 Апрель 2005 г., 22:44
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Info;

import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public class Version {
    public final static String version="$BOMBUSVERSION$";
    // this string will be patched by build.xml/post-preprocess
    
    public final static String url="http://bombus.jrudevels.org";


    private static String platformName;
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            if (platformName==null) platformName="Motorola-generic/null";
            
            if (platformName.equals("j2me")) {
                try {
                    Class.forName("com.motorola.multimedia.Lighting");
                    // this phone is Motorola if we still here ;)
                    platformName="Motorola-generic/j2me";
                } catch (Exception e) {/* no specific classes found*/ }
            }
        }
        return platformName;
    }

    public static String getOs() {
        return ConstMIDP.MIDP + " Platform=" +Version.getPlatformName();
    }
    
    public static String getVersionLang() { return version+" ("+SR.MS_IFACELANG+")"; }

}
