/*
 * ConfigForm.java
 *
 * Created on 2 ��� 2005 �., 18:19
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
import locale.SR;
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
    
    ChoiceGroup font1;
    ChoiceGroup font2;
    
    NumberField keepAlive;
    NumberField fieldLoc;
    NumberField fieldGmt;
    
    Command cmdOk=new Command(SR.MS_OK,Command.OK,1);
    //Command cmdSign=new Command("- (Sign)",Command.ITEM,2);
    Command cmdPlaySound=new Command(SR.MS_TEST_SOUND, Command.ITEM,10);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK,99);
    
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
        
        f=new Form(SR.MS_OPTIONS);
        roster=new ChoiceGroup(SR.MS_ROSTER_ELEMENTS, Choice.MULTIPLE);
        roster.append(SR.MS_OFFLINE_CONTACTS, null);
        roster.append(SR.MS_SELF_CONTACT, null);
        roster.append(SR.MS_TRANSPORTS, null);
        roster.append(SR.MS_IGNORE_LIST, null);
        roster.append(SR.MS_NOT_IN_LIST, null);
        //roster.append("Clock -",null);
        
        ra=new boolean[5];
        ra[0]=cf.showOfflineContacts;
        ra[1]=cf.selfContact;
        ra[2]=cf.showTransports;
        ra[3]=cf.ignore;
        ra[4]=cf.notInList;
        //ra[5]=false;
        roster.setSelectedFlags(ra);

        message=new ChoiceGroup(SR.MS_MESSAGES, Choice.MULTIPLE);
        message.append(SR.MS_SMILES, null);
        message.append(SR.MS_HISTORY, null);
        message.append(SR.MS_COMPOSING_EVENTS, null);
        mv=new boolean[3];
        mv[0]=cf.smiles;
        mv[1]=cf.msgLog;
        mv[2]=cf.eventComposing;
        message.setSelectedFlags(mv);

	startup=new ChoiceGroup(SR.MS_STARTUP_ACTIONS, Choice.MULTIPLE);
        startup.append(SR.MS_AUTOLOGIN, null);
        startup.append(SR.MS_JOIN_CONFERENCE,null);
        su=new boolean[2];
        su[0]=cf.autoLogin;
        su[1]=cf.autoJoinConferences;
        startup.setSelectedFlags(su);
        
        ap=new boolean[4];
	int apctr=0;
        application=new ChoiceGroup(SR.MS_APPLICATION, Choice.MULTIPLE);
//#if !(MIDP1)
        ap[apctr++]=cf.fullscreen;
        application.append(SR.MS_FULLSCREEN,null);
//#endif
        application.append(SR.MS_HEAP_MONITOR,null);
	if (!cf.ghostMotor)
            application.append(SR.MS_FLASHBACKLIGHT,null);
	if (cf.allowMinimize)
	    application.append(SR.MS_ENABLE_POPUP,null);
	ap[apctr++]=cf.memMonitor;
	ap[apctr++]=cf.blFlash;
	ap[apctr++]=cf.popupFromMinimized;
	
        application.setSelectedFlags(ap);
        
	keepAlive=new NumberField(SR.MS_KEEPALIVE_PERIOD, cf.keepAlive, 20, 600 );
	fieldGmt=new NumberField(SR.MS_GMT_OFFSET, cf.gmtOffset, -12, 12); 
        fieldLoc=new NumberField(SR.MS_CLOCK_OFFSET, cf.locOffset, -12, 12 );
        
	files=new StringLoader().stringLoader("/sounds/res.txt",3);
        sndFile=new ChoiceGroup(SR.MS_SOUND, ConstMIDP.CHOICE_POPUP);
	
	for (Enumeration f=files[2].elements(); f.hasMoreElements(); ) {
	    sndFile.append( (String)f.nextElement(), null );
	}
	
	sndFile.setSelectedIndex(cf.sounsMsgIndex, true);

        String fnts[]={"Normal", "Small", "Large"};
        font1=new ChoiceGroup(SR.MS_ROSTER_FONT, ConstMIDP.CHOICE_POPUP, fnts, null);
        font2=new ChoiceGroup(SR.MS_MESSAGE_FONT, ConstMIDP.CHOICE_POPUP, fnts, null);
        font1.setSelectedIndex(cf.font1/8, true);
        font2.setSelectedIndex(cf.font2/8, true);

        f.append(roster);
        f.append(font1);

        f.append(message);
        f.append(font2);
	
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
	
        f.append(SR.MS_TIME_SETTINGS);
        
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
            
            FontCache.rosterFontSize=cf.font1=font1.getSelectedIndex()*8;
            FontCache.msgFontSize=cf.font2=font2.getSelectedIndex()*8;
            FontCache.resetCache();
	    
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
