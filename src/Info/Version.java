/*
 * Version.java
 *
 * Created on 23.04.2005, 22:44
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

package Info;

import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author Evg_S
 */
public class Version {
//#if (!ZLIB)
//#     public final static String version="$BOMBUSVERSION$";
//#else
    public final static String version="$BOMBUSVERSION$-Zlib";
//#endif
    // this string will be patched by build.xml/post-preprocess
    
    public final static String BOMBUS_SITE_URL="http://bombus-im.org";

    private static String platformName;
    
    public static String getPlatformName() {
        if (platformName==null) {
            platformName=System.getProperty("microedition.platform");
            
            String device=System.getProperty("device.model");
            String firmware=System.getProperty("device.software.version");
            
            if (platformName==null) platformName="Motorola";
            
            if (platformName.startsWith("j2me")) {
                if (device.startsWith("wtk-emulator")) {
                    platformName=device;
                    return platformName;
                }
                
                if (device!=null && firmware!=null)
                    platformName="Motorola"; // buggy v360
		else {
		    // Motorola EZX phones
		    String hostname=System.getProperty("microedition.hostname");
		    if (hostname!=null) {
		        platformName="Motorola-EZX";
		        if (device!=null) {
		    	    // Motorola EZX ROKR
			    hostname=device;
                        }
                    
                        if (hostname.indexOf("(none)")<0)
                        platformName+="/"+hostname;
                        return platformName;
                    }
		}
            }
	    //else
            if (platformName.startsWith("Moto")) {
                if (device==null) device=System.getProperty("funlights.product");
                if (device!=null) platformName="Motorola-"+device;
                String devicesoftware=System.getProperty("device.software.version");
                if (devicesoftware!=null) platformName=platformName+"//"+devicesoftware;
            }
            
//#if (!MIDP1)
            if (platformName.indexOf("SIE") > -1) {
                platformName=System.getProperty("microedition.platform")+" (NSG)";
            } else if (System.getProperty("com.siemens.OSVersion")!=null) {
                platformName="SIE-"+System.getProperty("microedition.platform")+"/"+System.getProperty("com.siemens.OSVersion");
            }
//#endif
        }
        return platformName;
    }

    public static String getOs() {
        return ConstMIDP.MIDP + " Platform=" +Version.getPlatformName();
    }
    
    public static String getVersionLang() { return version+" ("+SR.MS_IFACELANG+")"; }

}
