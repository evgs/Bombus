/*
 * Config.java
 *
 * Created on 19 Март 2005 г., 18:37
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import Info.Version;
import java.io.*;
import java.util.*;
import util.StringLoader;
import ui.Time;
import ui.VirtualElement;
import ui.VirtualList;
//import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    
    public final int vibraLen=getProperty("vibra_len",500);
    
    public int keepAlive=getProperty("keep_alive",200);

    public boolean ghostMotor=getProperty("moto_e398",false);
    public boolean blFlash=true;
    
    public boolean msgLog=getProperty("msg_log",false);
    
    public boolean muc119=getProperty("muc_119",true);	// before muc 1.19 use muc#owner instead of muc#admin
    
    public int sounsMsgIndex=0;

    public String messagesnd;
    public String messageSndType;
    
    public int soundVol=100;
    
//#if !(MIDP1)
    public char keyLock=getProperty("key_lock",'*');
    public char keyVibra=getProperty("key_vibra",'#');
//#else
//--    public boolean msgLogPresence=getProperty("msg_log_presence",false);
//--    public boolean msgLogConfPresence=getProperty("msg_log_conf_presence",false);
//--    public boolean msgLogConf=getProperty("msg_log_conf",false);
//--    public final String msgPath=getProperty("msg_log_path","");
//--    public final String siemensCfgPath=getProperty("cfg_path","");
//--    public char keyLock=getProperty("key_lock",'#');
//--    public char keyVibra=getProperty("key_vibra",'*');
//#endif
    
    public char keyHide=getProperty("key_hide",'9');
    public char keyOfflines=getProperty("key_offlines",'0');
    
//#if USE_LED_PATTERN
//--    public int m55LedPattern=0;
//#endif
    
    public String defGcRoom=getProperty("gc_room","bombus");
    
    public String xmlLang=getProperty("xml_lang",null);
    
    // non-volatile values
    //public TimeZone tz=new RuGmt(0);
    public int accountIndex=-1;
    public boolean fullscreen=false;
    public int def_profile=0;
    public boolean smiles=true;
    public boolean showOfflineContacts=true;
    public boolean showTransports=true;
    public boolean selfContact=false;
    public boolean notInList=true;
    public boolean ignore=false;
    public boolean eventComposing=false;
    
    public boolean autoLogin=true;
    public boolean autoJoinConferences=false;
    
    public int gmtOffset;
    public int locOffset;
    
    // runtime values
    public boolean allowMinimize=false;
    public int profile=0;
    
    
    public void LoadFromStorage(){
	
	DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
	if (inputStream!=null)
	    try {
		accountIndex = inputStream.readInt();
		showOfflineContacts=inputStream.readBoolean();
		fullscreen=inputStream.readBoolean();
		def_profile = inputStream.readInt();
		smiles=inputStream.readBoolean();
		showTransports=inputStream.readBoolean();
		selfContact=inputStream.readBoolean();
		notInList=inputStream.readBoolean();
		ignore=inputStream.readBoolean();
		eventComposing=inputStream.readBoolean();
		
		gmtOffset=inputStream.readInt();
		locOffset=inputStream.readInt();
		
		sounsMsgIndex=inputStream.readInt();
		soundVol=inputStream.readInt();
		
		autoLogin=inputStream.readBoolean();
		autoJoinConferences=inputStream.readBoolean();
		
		keepAlive=inputStream.readInt();

		inputStream.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	
	loadSoundName();
	
	profile=def_profile;
	updateTime();
	VirtualList.fullscreen=fullscreen;
    }
    
    public void loadSoundName(){
	Vector files[]=new StringLoader().stringLoader("/sounds/res.txt", 3);
	messageSndType=(String) files[0].elementAt(sounsMsgIndex);
	messagesnd=(String) files[1].elementAt(sounsMsgIndex);
    }
    
    public void saveToStorage(){
	
	DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
	
	try {
	    outputStream.writeInt(accountIndex);
	    outputStream.writeBoolean(showOfflineContacts);
	    outputStream.writeBoolean(fullscreen);
	    outputStream.writeInt(def_profile);
	    outputStream.writeBoolean(smiles);
	    outputStream.writeBoolean(showTransports);
	    outputStream.writeBoolean(selfContact);
	    outputStream.writeBoolean(notInList);
	    outputStream.writeBoolean(ignore);
	    outputStream.writeBoolean(eventComposing);
	    
	    outputStream.writeInt(gmtOffset);
	    outputStream.writeInt(locOffset);
	    
	    outputStream.writeInt(sounsMsgIndex);
	    outputStream.writeInt(soundVol);
	    
	    outputStream.writeBoolean(autoLogin);
	    outputStream.writeBoolean(autoJoinConferences);
	    
	    outputStream.writeInt(keepAlive);
	    
	} catch (IOException e) { e.printStackTrace(); }
	
	NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }
    
    
    public void updateTime(){
	Time.setOffset(gmtOffset, locOffset);
    }
    
    /** Creates a new instance of Config */
    public Config() {
	int gmtloc=TimeZone.getDefault().getRawOffset()/3600000;
	locOffset=getProperty( "time_loc_offset", 0);
	gmtOffset=getProperty("time_gmt_offset", gmtloc);
	
	int greenKeyCode=VirtualList.SIEMENS_GREEN;
	
	String platform=Version.platform();
	
	if (platform.startsWith("SonyE")) {
	    allowMinimize=true;
	}
	if (platform.startsWith("Nokia")) {
	    blFlash=false;
	    greenKeyCode=VirtualList.NOKIA_GREEN;
	}
	if (platform.startsWith("Moto")) {
	    ghostMotor=true;
	    blFlash=false;
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	}
	if (platform.startsWith("j2me")) {
	    greenKeyCode=VirtualList.MOTOROLA_GREEN;
	}
	
	VirtualList.greenKeyCode=greenKeyCode;
	//System.out.println(locOffset);
//#if USE_LED_PATTERN
//--        if (platform.startsWith("M55"))
//--        m55LedPattern=getProperty("led_pattern",5);
//#endif
    }
    
    public final String getProperty(final String key, final String defvalue) {
	try {
	    String s=StaticData.getInstance().midlet.getAppProperty(key);
	    return (s==null)?defvalue:s;
	} catch (Exception e) {
	    return defvalue;
	}
    }
    
    public final int getProperty(final String key, final int defvalue) {
	try {
	    String s=StaticData.getInstance().midlet.getAppProperty(key);
	    return (s==null)?defvalue:Integer.parseInt(s);
	} catch (Exception e) {
	    return defvalue;
	}
    }
    
    public final char getProperty(final String key, final char defvalue) {
	try {
	    String s=StaticData.getInstance().midlet.getAppProperty(key);
	    return (s==null)?defvalue:s.charAt(0);
	} catch (Exception e) {
	    return defvalue;
	}
    }
    
    public final boolean getProperty(final String key, final boolean defvalue) {
	try {
	    String s=StaticData.getInstance().midlet.getAppProperty(key);
	    if (s==null) return defvalue;
	    if (s.equals("true")) return true;
	    if (s.equals("yes")) return true;
	    if (s.equals("1")) return true;
	    return false;
	} catch (Exception e) {
	    return defvalue;
	}
    }
    
}
