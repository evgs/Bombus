/*
 * SubscriptionEdit.java
 *
 * Created on 10 Май 2005 г., 19:09
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import javax.microedition.lcdui.*;
import locale.SR;

/**
 *
 * @author Evg_S
 */
public class SubscriptionEdit extends Form implements CommandListener{
    
    private Display display;
      
    Command cmdSubscrTo=new Command(SR.MS_ASK_SUBSCRIPTION, Command.SCREEN, 1);
    Command cmdSubscrFrom=new Command(SR.MS_GRANT_SUBSCRIPTION, Command.SCREEN, 2);
    Command cmdSubscrDel=new Command(SR.MS_SUBSCR_REMOVE, Command.SCREEN, 3);
    Command cmdBack=new Command(SR.MS_CANCEL, Command.BACK, 99);
    
    Roster r=StaticData.getInstance().roster;
    String to;
    /** Creates a new instance of YesNoAlert */
    public SubscriptionEdit(Display display, Contact c) {
        super(SR.MS_SUBSCRIPTION);
        to=c.getBareJid();
        StringBuffer s=new StringBuffer(c.getNickJid());
        s.append('\n');
        s.append("subscr:");
        s.append(c.subscr);
        if (c.ask_subscribe) s.append(",ask");
        
//#if !(MIDP1)
        append("\n");
//#endif
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
        
        if (presence!=null) r.sendPresence(to,presence, null);
        
        destroyView();
    }

    public void destroyView(){
        if (display!=null)   display.setCurrent(r);
    }

}
