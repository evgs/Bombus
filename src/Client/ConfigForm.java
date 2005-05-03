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
        roster.append("self-contact",null);
        roster.append("offline contacts",null);
        roster.append("transports",null);
        roster.append("Hidden group -",null);
        roster.append("Not in list -",null);
        roster.append("Clock -",null);
        
        ra=new boolean[6];
        ra[0]=cf.selfContact;
        ra[1]=cf.showOfflineContacts;
        ra[2]=cf.showTransports;
        ra[3]=false;
        ra[4]=false;
        ra[5]=false;
        roster.setSelectedFlags(ra);

        message=new ChoiceGroup("Messages", Choice.MULTIPLE);
        message.append("smiles",null);
        message.append("history -",null);
        mv=new boolean[2];
        mv[0]=cf.smiles;
        mv[1]=cf.msgLog;
        message.setSelectedFlags(mv);
        
        application=new ChoiceGroup("Application", Choice.MULTIPLE);
        application.append("fullscreen",null);
        ap=new boolean[1];
        ap[0]=cf.fullscreen;
        application.setSelectedFlags(ap);
        
        //if (newaccount)
        f.append(roster);
        f.append(message);
/*#DefaultConfiguration,Release#*///<editor-fold>
        f.append(application);
/*$DefaultConfiguration,Release$*///</editor-fold>
        
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
            cf.selfContact=ra[0];
            cf.showOfflineContacts=ra[1];
            cf.showTransports=ra[2];
            //ra[3]=false;
            //ra[4]=false;

            cf.smiles=mv[0];
            cf.msgLog=mv[1];
            
            cf.fullscreen=ap[0];
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
        
        if (c==cmdCancel) destroyView();
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
/*#DefaultConfiguration,Release#*///<editor-fold>
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
/*$DefaultConfiguration,Release$*///</editor-fold>
    }
}
