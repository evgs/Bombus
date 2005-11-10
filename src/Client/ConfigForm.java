/*
 * ConfigForm.java
 *
 * Created on 2 Май 2005 г., 18:19
 */

package Client;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;
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
    ChoiceGroup application;
    
    ChoiceGroup sndFile;
    //Gauge sndVol;
    
    TextField fieldGmt;
    TextField fieldLoc;
    
    
    Command cmdOk=new Command("OK",Command.OK,1);
    Command cmdSign=new Command("- (Sign)",Command.ITEM,2);
    Command cmdPlaySound=new Command("Test sound",Command.ITEM,10);
    Command cmdCancel=new Command("Cancel",Command.BACK,99);
    
    Config cf;
    boolean ra[];
    boolean mv[];
    boolean ap[];
    Vector files[];
    
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
				ConstMIDP.TEXTFIELD_DECIMAL       );
        fieldLoc=new TextField("Clock offset", String.valueOf(cf.locOffset), 4, 
				ConstMIDP.TEXTFIELD_DECIMAL       );
        
        
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
	
//#if !(MIDP1)
        f.append(application);
//#endif
        
        f.append("Time settings (hours)\n");
        
        f.append(fieldGmt);
        f.append(fieldLoc);

//#if !(MIDP1)
	fieldGmt.addCommand(cmdSign);
        fieldGmt.setItemCommandListener(this);
	fieldLoc.addCommand(cmdSign);
        fieldLoc.setItemCommandListener(this);
//#endif
        
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
            cf.showOfflineContacts=ra[0];
            cf.selfContact=ra[1];
            cf.showTransports=ra[2];
            cf.ignore=ra[3];
            cf.notInList=ra[4];

            cf.smiles=mv[0];
            cf.msgLog=mv[1];
            cf.eventComposing=mv[2];
            
            VirtualList.fullscreen=cf.fullscreen=ap[0];
            
	    try {
		cf.gmtOffset=Integer.parseInt(fieldGmt.getString());
		cf.locOffset=Integer.parseInt(fieldLoc.getString());
	    } catch (Exception e) { return; }
	    
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
	    return;
	}
	TextField field=(TextField) item;
	StringBuffer body=new StringBuffer( field.getString() );
	if ( body.charAt(0)=='-' ) 
	    body.deleteCharAt(0);
	else
	    body.insert(0,'-');
	field.setString(body.toString());
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
