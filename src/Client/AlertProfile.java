/*
 * Profile.java
 *
 * Created on 28 Март 2005 г., 0:05
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import images.RosterIcons;
import ui.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class AlertProfile extends VirtualList implements CommandListener {
    public final static int AUTO=0;
    public final static int ALL=1;
    public final static int VIBRA=2;
    public final static int SOUND=3;
    public final static int NONE=4;
    
    private final static String[] alertNames=
    { "Auto", "All signals", "Vibra", "Sound", "No signals"};
    
    private Profile profile=new Profile();
    int defp;
    Config cf;
    
    /** Creates a new instance of Profile */
    
    private Command cmdOk=new Command("Select",Command.OK,1);
    private Command cmdDef=new Command("Set Default",Command.OK,2);
    private Command cmdCancel=new Command("Back",Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    public AlertProfile(Display d) {
        super();
        setTitleImages(RosterIcons.getInstance());
        
        cf=Config.getInstance();
        
        createTitleItem(1, "Alert Profile",null);
        
        addCommand(cmdOk);
        addCommand(cmdDef);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        int p=cf.profile;
        defp=cf.def_profile;
        
        moveCursorTo(p, true);
        attachDisplay(d);
    }
    
    int index;
    public VirtualElement getItemRef(int Index){ index=Index; return profile;}
    private class Profile extends IconTextElement {
        public Profile(){
            super(RosterIcons.getInstance());
        }
        //public void onSelect(){}
        public int getColor(){ return 0; }
        public int getImageIndex(){return index+RosterIcons.ICON_PROFILE_INDEX;}
        public String toString(){ 
            StringBuffer s=new StringBuffer(alertNames[index]);
            if (index==defp) s.append(" (default)");
            return s.toString();
        }
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdDef) { 
            cf.def_profile=defp=cursor;
	    cf.saveToStorage();
            redraw();
        }
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        cf.profile=cursor;
        destroyView();
    }
    
    public int getItemCount(){   return alertNames.length; }
    

    /** */
    public static void playNotify(Display display, int event) {
        Config cf=Config.getInstance();
        String message=cf.messagesnd;
	String type=cf.messageSndType;
	int volume=cf.soundVol;
        int profile=cf.profile;
        if (profile==AUTO) profile=ALL;
        
        EventNotify notify=null;
        
        boolean blFlashEn=cf.blFlash;   // motorola e398 backlight bug
        
        switch (profile) {
            case ALL:   notify=new EventNotify(display, type, message, cf.vibraLen, blFlashEn); break;
            case NONE:  notify=new EventNotify(display, null, null,    0,           false    ); break;
            case VIBRA: notify=new EventNotify(display, null, null,    cf.vibraLen, blFlashEn); break;
            case SOUND: notify=new EventNotify(display, type, message, 0,           blFlashEn); break;
        }
        if (notify!=null) notify.startNotify();
    }
}
