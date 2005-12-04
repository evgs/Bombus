/*
 * Version.java
 *
 * Created on 23 јпрель 2005 г., 22:44
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Info;

import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public class Version {
    public final static String version="$BOMBUSVERSION$";
    // this string will be patched by build.xml/post-preprocess
    
    public final static String url="http://bombus.jrudevels.org";


    public static String platform() {
        String platform=System.getProperty("microedition.platform");
        return (platform==null)? "Motorola-generic":platform;
    }

    public static String getOs() {
        return ConstMIDP.MIDP + " Platform=" +Version.platform()+"; extInfo= "+extInfo();
    }

    private static String extInfo() {
        return "manufacturer: "+System.getProperty("device.manufacturer")+"  model:"+System.getProperty("device.model");
    }
}
