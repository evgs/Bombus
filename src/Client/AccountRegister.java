/*
 * AccountRegister.java
 *
 * Created on 24 јпрель 2005 г., 2:36
 */

package Client;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Evg_S
 */
public class AccountRegister 
        implements         
            JabberListener,
            Runnable
{
    
    private Display display;
    private Displayable parentView;
    private Form f;
    
    private Account raccount;
    private JabberStream theStream ;
    
    /** Creates a new instance of AccountRegister */
    public AccountRegister(Account account, Display display) {
        this.display=display;
        parentView=display.getCurrent();
        
        
        raccount=account;
        f=new Form("Registering");
        display.setCurrent(f);
        
        new Thread(this).start();
    }
    public void run() {
        try {
            f.append("Connecting to"+raccount.getServerN());
            theStream= new JabberStream( new meConnector( raccount.getServer(), raccount.getPort() ) );
            theStream.setJabberListener( this );
        } catch( Exception e ) {
            e.printStackTrace();
        }

    }
    
    public void connectionTerminated( Exception e ) {
        //l.setTitleImgL(0);
        //System.out.println( "Connection terminated" );
        if( e != null )
            e.printStackTrace();
    }

    public void beginConversation(String SessionId) {
        f.append("Registering");
        IqRegister iq=new IqRegister(raccount.getUserName(),raccount.getPassword());
        try {
            theStream.send(iq);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void blockArrived( JabberDataBlock data ) {
        theStream.close();
        destroyView();
        if (data instanceof Iq) {
            String type=data.getAttribute("type");
            String title="Done";
            String result="Registered successfully";
            AlertType at=AlertType.CONFIRMATION;
            if (!type.equals("result")) {
                at=AlertType.ERROR;
                title="Error";
                result=((JabberDataBlock)
                    data.getChildBlock("error").
                        getChildBlocks().
                        firstElement()).getTagName();
            }
            Alert alert=new Alert(title,type,null,at);
            alert.setTimeout(10);
            display.setCurrent(alert, parentView);
        }
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

}
