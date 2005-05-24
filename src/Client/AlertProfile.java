/*
 * Profile.java
 *
 * Created on 28 Март 2005 г., 0:05
 */

package Client;

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
    /*private final static int[] alertCodes=
    { AUTO, ALL, VIBRA, SOUND, NONE };*/
    
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
        StaticData sd=StaticData.getInstance();
        setTitleImages(sd.rosterIcons);
        
        cf=sd.config;
        
        createTitle(1, "Alert Profile",null);
        
        addCommand(cmdOk);
        addCommand(cmdDef);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        int p=cf.profile;
        defp=cf.def_profile;
        
        moveCursorTo(p);
        attachDisplay(d);
    }
    
    int index;
    public VirtualElement getItemRef(int Index){ index=Index; return profile;}
    private class Profile extends IconTextElement {
        public Profile(){
            super(StaticData.getInstance().rosterIcons);
        }
        //public void onSelect(){}
        public int getColor(){ return 0; }
        public int getImageIndex(){return index+ImageList.ICON_PROFILE_INDEX;}
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
        Config cf=StaticData.getInstance().config;
        String message=cf.messagesnd;
        int profile=cf.profile;
        if (profile==AUTO) profile=ALL;
        
        EventProfile ep=null;
        
        boolean blFlashEn=!cf.ghostMotor;   // motorola e398 backlight bug
        
        switch (profile) {
            case ALL:   ep=new EventProfile(message, cf.vibraLen, blFlashEn); break;
            case NONE:  ep=new EventProfile(null,    0,           false    ); break;
            case VIBRA: ep=new EventProfile(null,    cf.vibraLen, blFlashEn); break;
            case SOUND: ep=new EventProfile(message, 0,           blFlashEn); break;
        }
        if (ep!=null) new EventNotify (display, ep).startNotify();
    }
}
