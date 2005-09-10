/*
 * AccountRegister.java
 *
 * Created on 24 јпрель 2005 г., 2:36
 */

package Client;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.*;
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
    private Command cmdCancel=new Command("Cancel",Command.BACK, 2);
    
    /** Creates a new instance of AccountRegister */
    public AccountRegister(Account account, Display display, Displayable parentView) {
        this.display=display;
        this.parentView=parentView;//display.getCurrent();
        
        
        raccount=account;
        spl.setProgress("Startup",5);
        display.setCurrent(spl);
        spl.addCommand(cmdCancel);
        spl.setCommandListener(this);
        
        new Thread(this).start();
    }
    public void run() {
        try {
            spl.setProgress("Connect to"+raccount.getServerN(),30);
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
        spl.setProgress("Registering",60);
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
            String title="Done";
            if (!type.equals("result")) {
                pgs=0;
                title="Error: "+((JabberDataBlock)
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
/*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(e, "AccountRegister:108");
/*$USE_LOGGER$*///</editor-fold>
        }
        destroyView();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
