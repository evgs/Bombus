/*
 * Config.java
 *
 * Created on 19 Март 2005 г., 18:37
 */

package Client;

import java.io.*;
//import javax.microedition.rms.*;

/**
 *
 * @author Eugene Stahov
 */
public class Config {
    
    public final int keepAlive=getProperty("keep_alive",200);
    public final String version=getProperty("MIDlet-Version","---");
    public int profile=getProperty("alert_profile",1);
    public boolean ghostMotor=getProperty("moto_e398",false);
    
/*#DefaultConfiguration,Release#*///<editor-fold>
    public String messagesnd=getProperty("msg_snd","/sounds/message.amr");
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--    public boolean msgLog=getProperty("msg_log",false);
//--    public boolean msgLogPresence=getProperty("msg_log_presence",false);
//--    public final String msgPath=getProperty("msg_log_path","");
//--    public String messagesnd=getProperty("msg_snd","/sounds/message.wav");
//--    public final String m55cfgpath=getProperty("cfg_path","");
//--    public final int m55_led_pattern=getProperty("led_pattern",0);
/*$M55,M55_Release$*///</editor-fold>
    
    public int accountIndex=-1;
    public boolean showOfflineContacts=true;
    
    public void LoadFromStorage(){
        
        DataInputStream inputStream=NvStorage.ReadFileRecord("config", 0);
        if (inputStream!=null)
        try {
            accountIndex = inputStream.readInt();
            showOfflineContacts=inputStream.readBoolean();
            
            inputStream.close();
        } catch (Exception e) { e.printStackTrace(); }
            //return null;
    }
    
    public void saveToStorage(){
        
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();

        try {
            outputStream.writeInt(accountIndex);
            outputStream.writeBoolean(showOfflineContacts);
        } catch (IOException e) { e.printStackTrace(); }

        NvStorage.writeFileRecord(outputStream, "config", 0, true);
    }

    
    
    /** Creates a new instance of Config */
    public Config() {
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
