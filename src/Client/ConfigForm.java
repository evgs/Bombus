/*
 * ConfigForm.java
 *
 * Created on 2 Май 2005 г., 18:19
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;
import util.StringLoader;
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
 * startup actions
 *  [] login
 *  [] Join conferences
 *
 * application
 *  [] fullscreen
 */

public class ConfigForm implements
	CommandListener 
//#if !(MIDP1)
	,ItemCommandListener
//#endif
	//,ItemStateListener
{
    private Display display;
    private Displayable parentView;

    Form f;
    ChoiceGroup roster;
    ChoiceGroup message;
    ChoiceGroup startup;
    ChoiceGroup application;
    
    ChoiceGroup sndFile;
    //Gauge sndVol;
    
    NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    Command cmdOk=new Command("OK",Command.OK,1);
    //Command cmdSign=new Command("- (Sign)",Command.ITEM,2);
    Command cmdPlaySound=new Command("Test sound",Command.ITEM,10);
    Command cmdCancel=new Command("Cancel",Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    boolean su[];
    Vector files[];
    
    /** Creates a new instance of ConfigForm */
    public ConfigForm(Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        cf=Config.getInstance();
        
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

	startup=new ChoiceGroup("Startup actions", Choice.MULTIPLE);
        startup.append("autologin",null);
        startup.append("join conferences",null);
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        ap=new boolean[4];
	int apctr=0;
        application=new ChoiceGroup("Application", Choice.MULTIPLE);
//#if !(MIDP1)
        ap[apctr++]=cf.fullscreen;
        application.append("fullscreen",null);
//#endif
        application.append("heap monitor",null);
	if (!cf.ghostMotor)
            application.append("flash backlight",null);
	if (cf.allowMinimize)
	    application.append("popup from background",null);
	ap[apctr++]=cf.memMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	
        application.setSelectedFlags(ap);
        
	keepAlive=new NumberField("Keep-Alive period", cf.keepAlive, 30, 600 );
	fieldGmt=new NumberField("GMT offset", cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField("Clock offset", cf.locOffset, -12, 12 );
        
	files=new StringLoader().stringLoader("/sounds/res.txt",3);
        sndFile=new ChoiceGroup("Sound", ConstMIDP.CHOICE_POPUP);
	
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    sndFile.append( (String)f.nextElement(), null );
	}
	
	sndFile.setSelectedIndex(cf.sounsMsgIndex, true);

	f.append(roster);
        f.append(message);
	
	f.append(sndFile);
	
	//sndVol=new Gauge("Sound volume", true, 10,  cf.soundVol/10);
	//f.append(sndVol);

//#if !(MIDP1)
	sndFile.addCommand(cmdPlaySound);
	sndFile.setItemCommandListener(this);
	//sndVol.addCommand(cmdPlaySound);
	//sndVol.setItemCommandListener(this);
//#else
//--	f.addCommand(cmdPlaySound);
//#endif
	
	f.append(startup);

	f.append(application);

	f.append(keepAlive);
	
        f.append("Time settings (hours)\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);

        f.addCommand(cmdOk);
        f.addCommand(cmdCancel);
        
        f.setCommandListener(this);
	//f.setItemStateListener(this);
        
        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdOk) {
            roster.getSelectedFlags(ra);
            message.getSelectedFlags(mv);
            application.getSelectedFlags(ap);
	    startup.getSelectedFlags(su);
	    
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];

            cf.smiles=mv[0];
            cf.msgLog=mv[1];
            cf.eventComposing=mv[2];
	    
	    cf.autoLogin=su[0];
	    cf.autoJoinConferences=su[1];
            
	    int apctr=0;
//#if !(MIDP1)
            VirtualList.fullscreen=cf.fullscreen=ap[apctr++];
//#endif
	    VirtualList.memMonitor=cf.memMonitor=ap[apctr++];
	    cf.blFlash=ap[apctr++];
	    cf.popupFromMinimized=ap[apctr++];
            
	    cf.gmtOffset=fieldGmt.getValue();
	    cf.locOffset=fieldLoc.getValue();
	    cf.keepAlive=keepAlive.getValue();
	    
	    cf.sounsMsgIndex=sndFile.getSelectedIndex();
	    
	    //cf.soundVol=sndVol.getValue()*10;
	    
	    cf.loadSoundName();
            
            cf.updateTime();
            
            cf.saveToStorage();
            
            StaticData.getInstance().roster.reEnumRoster();
            destroyView();
        }
//#if MIDP1
//--        if (c==cmdPlaySound) testSound();
//#endif
        if (c==cmdCancel) destroyView();
    }

//#if !(MIDP1)
    public void commandAction(Command command, Item item) {
	if (command==cmdPlaySound) {
	    testSound();
	}
    }
//#endif
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
//#if !(MIDP1)
        ((Canvas)parentView).setFullScreenMode(cf.fullscreen);
//#endif
    }

    /*public void itemStateChanged(Item item) {
	if (item==sndVol || item==soundFile) 
     */
    private void testSound(){
	int sound=sndFile.getSelectedIndex();
	String soundFile=(String)files[1].elementAt(sound);
	String soundType=(String)files[0].elementAt(sound);
	new EventNotify(display, soundType, soundFile, 0, false).startNotify();
    }

}
