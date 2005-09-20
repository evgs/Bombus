/*
 * ConfigForm.java
 *
 * Created on 2 Май 2005 г., 18:19
 */

package Client;
import javax.microedition.lcdui.*;
import ui.*;

/**
 *
 * @author Evg_S
 */

/*
 * roster elements:
 *  [] self-contact
 *  [] offline contacts
 *  [] transports
 *  [] hidden group
 *  [] not-in-list
 *  [] clock
 *
 * message
 *  [] show smiles
 *  [] history
 *  [] composing
 *
 * application
 *  [] fullscreen
 */

public class ConfigForm implements CommandListener{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup roster;
    ChoiceGroup message;
    ChoiceGroup application;
    TextField fieldGmt;
    TextField fieldLoc;
    
    
    Command cmdOk=new Command("OK",Command.OK,1);
    Command cmdCancel=new Command("Cancel",Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=StaticData.getInstance().config;
        
        f=new Form("Options");
        roster=new ChoiceGroup("Roster elements", Choice.MULTIPLE);
        roster.append("offline contacts",null);
        roster.append("self-contact",null);
        roster.append("transports",null);
        roster.append("Ignore-List",null);
        roster.append("Not-in-list",null);
        //roster.append("Clock -",null);
        
        ra=new boolean[5];
        ra[0]=cf.showOfflineContacts;
        ra[1]=cf.selfContact;
        ra[2]=cf.showTransports;
        ra[3]=cf.ignore;
        ra[4]=cf.notInList;
        //ra[5]=false;
        roster.setSelectedFlags(ra);

        message=new ChoiceGroup("Messages", Choice.MULTIPLE);
        message.append("smiles",null);
        message.append("history -",null);
        message.append("composing events",null);
        mv=new boolean[3];
        mv[0]=cf.smiles;
        mv[1]=cf.msgLog;
        mv[2]=cf.eventComposing;
        message.setSelectedFlags(mv);
        
        application=new ChoiceGroup("Application", Choice.MULTIPLE);
        application.append("fullscreen",null);
        ap=new boolean[1];
        ap[0]=cf.fullscreen;
        application.setSelectedFlags(ap);
        
        fieldGmt=new TextField("GMT offset", String.valueOf(cf.gmtOffset), 4, 
/*#!MIDP1#*///<editor-fold>
                TextField.DECIMAL
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--                TextField.ANY
/*$MIDP1$*///</editor-fold>
                );
        fieldLoc=new TextField("Clock offset", String.valueOf(cf.locOffset), 4, 
/*#!MIDP1#*///<editor-fold>
                TextField.DECIMAL
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--                TextField.ANY
/*$MIDP1$*///</editor-fold>
                );
        
        
        //if (newaccount)
        f.append(roster);
        f.append(message);
/*#!MIDP1#*///<editor-fold>
        f.append(application);
/*$!MIDP1$*///</editor-fold>
        
        f.append("Time settings (hours)\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);
        
        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];

            cf.smiles=mv[0];
            cf.msgLog=mv[1];
            cf.eventComposing=mv[2];
            
            cf.fullscreen=ap[0];
            
            cf.gmtOffset=Integer.parseInt(fieldGmt.getString());
            cf.locOffset=Integer.parseInt(fieldLoc.getString());
            
            cf.updateTime();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        
        if (c==cmdCancel) destroyView();
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
/*#!MIDP1#*///<editor-fold>
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
/*$!MIDP1$*///</editor-fold>
    }
}
