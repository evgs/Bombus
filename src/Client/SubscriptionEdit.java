/*
 * SubscriptionEdit.java
 *
 * Created on 10 Май 2005 г., 19:09
 */

package Client;
import javax.microedition.lcdui.*;

/**
 *
 * @author Evg_S
 */
public class SubscriptionEdit extends Form implements CommandListener{
    
    private Display display;
      
    Command cmdSubscrTo=new Command("Send \"to\"", Command.SCREEN, 1);
    Command cmdSubscrFrom=new Command("Request \"from\"", Command.SCREEN, 2);
    Command cmdSubscrDel=new Command("Remove subscription", Command.SCREEN, 3);
    Command cmdBack=new Command("Cancel", Command.BACK, 99);
    
    Roster r=StaticData.getInstance().roster;
    String to;
    /** Creates a new instance of YesNoAlert */
    public SubscriptionEdit(Display display, Contact c) {
        super("Subscription");
        to=c.getJidNR();
        StringBuffer s=new StringBuffer(c.getNickJid());
        s.append('\n');
        s.append("subscr:");
        s.append(c.subscr);
        if (c.ask_subscribe) s.append(",ask");
        
        append("\n");
        append(s.toString());
        //setString(s.toString());

        addCommand(cmdSubscrFrom);
        addCommand(cmdSubscrTo);
        addCommand(cmdSubscrDel);
        addCommand(cmdBack);
        
        setCommandListener(this);

        this.display=display;
        display.setCurrent(this);
    }
    public void commandAction(Command c, Displayable d ){
        String presence=null;
        if (c==cmdSubscrFrom) { presence="subscribe"; }
        if (c==cmdSubscrTo) { presence="subscribed"; }
        if (c==cmdSubscrDel) { presence="unsubscribed"; }
        
        if (presence!=null) r.sendPresence(to,presence);
        
        destroyView();
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(r);
    }

}
