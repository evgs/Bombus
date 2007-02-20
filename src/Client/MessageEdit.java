/*
 * MessageEdit.java
 *
 * Created on 20.02.2005, 21:20
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package Client;
import Conference.AppendNick;
import archive.ArchiveList;
import javax.microedition.lcdui.*;
import locale.SR;
import ui.VirtualList;

/**
 *
 * @author Eugene Stahov
 */
public class MessageEdit 
        implements CommandListener, Runnable
{
    
    private Display display;
    private Displayable parentView;
    private TextBox t;
    private String body;
    private String subj;
    
    private Contact to;
    private Command cmdSuspend=new Command(SR.MS_SUSPEND, Command.BACK,90);
    private Command cmdCancel=new Command(SR.MS_CANCEL, Command.SCREEN,99);
    private Command cmdSend=new Command(SR.MS_SEND, Command.OK /*Command.SCREEN*/,1);
    private Command cmdSmile=new Command(SR.MS_ADD_SMILE, Command.SCREEN,2);
    private Command cmdInsNick=new Command(SR.MS_NICKNAMES,Command.SCREEN,3);
    private Command cmdInsMe=new Command(SR.MS_SLASHME, Command.SCREEN, 4); ; // /me
    private Command cmdSubj=new Command(SR.MS_SET_SUBJECT, Command.SCREEN, 10);
    private Command cmdPaste=new Command(SR.MS_ARCHIVE, Command.SCREEN, 5);
    private Command cmdABC=new Command("Abc", Command.SCREEN, 15);
    private Command cmdAbc=new Command("abc", Command.SCREEN, 15);
    
    private boolean composing=true;
    
    //private Command cmdSubject=new Command("Subject",Command.SCREEN,10);
    
    /** Creates a new instance of MessageEdit */
    public MessageEdit(Display display, Contact to, String body) {
        this.to=to;
        this.display=display;
        parentView=display.getCurrent();
        
        int maxSize=500;
        t=new TextBox(to.toString(), null, maxSize, TextField.ANY);
        try {
            //expanding buffer as much as possible
            maxSize=t.setMaxSize(4096); //must not trow

            if (body!=null) {
                //trim body to maxSize
                if (body.length()>maxSize)
                    body=body.substring(0, maxSize-1);
                t.setString(body);
            }
             
        } catch (Exception e) {
            t.setString("<send bugreport>");
        }
        
        t.addCommand(cmdSend);
        t.addCommand(cmdInsMe);
        t.addCommand(cmdSmile);
        if (to.origin>=Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdInsNick);
	t.addCommand(cmdPaste);
        t.addCommand(cmdSuspend);
        t.addCommand(cmdCancel);
        t.setCommandListener(this);
        
        if (to.origin==Contact.ORIGIN_GROUPCHAT)
            t.addCommand(cmdSubj);
        
        //t.setInitialInputMode("MIDP_LOWERCASE_LATIN");
        new Thread(this).start() ; // composing
        
        setInitialCaps(Config.getInstance().capsState);
        
        display.setCurrent(t);
    }
    
    public void insertText(String s, int caretPos) {

        String src=t.getString();

        StringBuffer sb=new StringBuffer(s);
        
        //if (caretPos<0) caretPos=src.length();
        
        if (caretPos>0) 
            if (src.charAt(caretPos-1)!=' ')   
                sb.insert(0, ' ');
        
        if (caretPos<src.length())
            if (src.charAt(caretPos)!=' ')
                sb.append(' ');
        
        if (caretPos==src.length()) sb.append(' ');
        
        try {
            int freeSz=t.getMaxSize()-t.size();
            if (freeSz<sb.length()) sb.delete(freeSz, sb.length());
        } catch (Exception e) {e.printStackTrace();}
       
        t.insert(sb.toString(), caretPos);
    }
    
    public void setParentView(Displayable parentView){
        this.parentView=parentView;
    }
    
    public void commandAction(Command c, Displayable d){
        body=t.getString();
        
        int caretPos=t.getCaretPosition();
        // +MOTOROLA STUB
        String platform=Info.Version.getPlatformName();
        if (platform.startsWith("Moto"))
            if (!platform.startsWith("Motorola-EZX")) caretPos=-1;
        // -MOTOROLA STUB
        
        if (caretPos<0) caretPos=body.length();
        
        if (body.length()==0) body=null;
        
        if (c==cmdInsMe) { t.insert("/me ", 0); return; }
        if (c==cmdSmile) { new SmilePicker(display, this, caretPos); return; }
        if (c==cmdInsNick) { new AppendNick(display, to, this, caretPos); return; }
        if (c==cmdAbc) {setInitialCaps(false); return; }
        if (c==cmdABC) {setInitialCaps(true); return; }
	if (c==cmdPaste) { new ArchiveList(display, this, caretPos); return; }
        
        if (c==cmdCancel) { 
            composing=false; 
            body=null; 
        }
        if (c==cmdSuspend) { 
            composing=false; 
            to.msgSuspended=body; 
            body=null;
        }
        if (c==cmdSend && body==null) return;
        if (c==cmdSubj) {
            if (body==null) return;
            subj=body;
            body=null; //"/me has set the topic to: "+subj;
        }
        // message/composing sending
        destroyView();
        new Thread(this).start();
        return; 
    }
    
    
    public void run(){
        Roster r=StaticData.getInstance().roster;
        int comp=0; // composing event off
        
        if (body!=null || subj!=null ) {
            String from=StaticData.getInstance().account.toString();
            Msg msg=new Msg(Msg.MESSAGE_TYPE_OUT,from,subj,body);
            // не добавляем в групчат свои сообщения
            // не шлём composing
            if (to.origin!=Contact.ORIGIN_GROUPCHAT) {
                to.addMessage(msg);
                comp=1; // composing event in message
            }
            
        } else if (to.acceptComposing) comp=(composing)? 1:2;
        
        if (!Config.getInstance().eventComposing) comp=0;
        
        try {
            if (body!=null || subj!=null || comp>0)
            r.sendMessage(to, body, subj, comp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((VirtualList)parentView).redraw();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }

    private void setInitialCaps(boolean state) {
//#if (!MIDP1)
        t.setConstraints(state? TextField.INITIAL_CAPS_SENTENCE: TextField.ANY);
        t.removeCommand(state? cmdABC: cmdAbc);
        t.addCommand(state? cmdAbc: cmdABC);
        Config.getInstance().capsState=state;
//#endif
    }

}
