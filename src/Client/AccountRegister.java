/*
 * AccountRegister.java
 *
 * Created on 24 Апрель 2005 г., 2:36
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.*;

/**
 *
 * @author Evg_S
 */
public class AccountRegister 
        implements         
            JabberListener,
            CommandListener,
            Runnable
{
    
    private Display display;
    private Displayable parentView;
    
    private Account raccount;
    private JabberStream theStream ;
    private SplashScreen spl=SplashScreen.getInstance();
    //private Command cmdOK=new Command("Cancel",Command.BACK, 2);
    private Command cmdCancel=new Command(SR.MS_CANCEL,Command.BACK, 2);
    
    /** Creates a new instance of AccountRegister */
    public AccountRegister(Account account, Display display, Displayable parentView) {
        this.display=display;
        this.parentView=parentView;//display.getCurrent();
        
        
        raccount=account;
        spl.setProgress(SR.MS_STARTUP,5);
        display.setCurrent(spl);
        spl.addCommand(cmdCancel);
        spl.setCommandListener(this);
        
        new Thread(this).start();
    }
    public void run() {
        try {
            spl.setProgress(SR.MS_CONNECT_TO +raccount.getServer(),30);
            theStream= raccount.openJabberStream();
            theStream.setJabberListener( this );
        } catch( Exception e ) {
            e.printStackTrace();
            spl.setFailed();
        }

    }
    
    public void rosterItemNotify(){}
    
    public void connectionTerminated( Exception e ) {
        //l.setTitleImgL(0);
        //System.out.println( "Connection terminated" );
        if( e != null ) {
            e.printStackTrace();
        }
    }

    public void beginConversation(String SessionId) {
        spl.setProgress(SR.MS_REGISTERING,60);
        IqRegister iq=new IqRegister(raccount.getUserName(),raccount.getPassword(), "regac");
        //try {
            theStream.send(iq);
        //} catch (Exception e) {e.printStackTrace();}
    }
    public void blockArrived( JabberDataBlock data ) {
        theStream.close();
        //destroyView();
        if (data instanceof Iq) {
            int pgs=100;
            String type=data.getTypeAttribute();
            String title=SR.MS_DONE; 
            if (!type.equals("result")) {
                pgs=0;
                title=SR.MS_ERROR_ +((JabberDataBlock)
                    data.getChildBlock("error").
                        getChildBlocks().
                        firstElement()).getTagName();
            }
            spl.setProgress(title,pgs);
        }
    }
    
    public void commandAction(Command c, Displayable d) {
        spl.setCommandListener(null);
        spl.removeCommand(cmdCancel);
        try {
            theStream.close();
        } catch (Exception e) { 
            e.printStackTrace();
        }
        destroyView();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
