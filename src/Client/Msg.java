/*
 * msg.java
 *
 * Created on 6.01.2005, 19:20
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import javax.microedition.lcdui.Font;
import ui.Colors;
import ui.Time;
import Client.ContactMessageList;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Eugene Stahov
 */
public class Msg //implements MessageList.Element
{
    // without signaling
    public final static int MESSAGE_TYPE_OUT=1;
    public final static int MESSAGE_TYPE_PRESENCE=2;
    public final static int MESSAGE_TYPE_HISTORY=3;
    // with signaling
    public final static int MESSAGE_TYPE_IN=10;
    public final static int MESSAGE_TYPE_HEADLINE=11;
    public final static int MESSAGE_TYPE_ERROR=11;
    public final static int MESSAGE_TYPE_SUBJ=12;
    public final static int MESSAGE_TYPE_AUTH=15;

    private boolean highlite;
    
    /** Creates a new instance of msg */
    public Msg(int messageType, String from, String subj, String body) {
        this.messageType=messageType;
        this.from=from;
        this.body=body;
        this.subject=subj;
        this.dateGmt=Time.localTime();
        if (messageType>=MESSAGE_TYPE_IN) unread=true;
        if (messageType==MESSAGE_TYPE_PRESENCE || messageType==MESSAGE_TYPE_HEADLINE)
            itemCollapsed=true;
    }
    
    public void onSelect(){}
    /*public String getMsgHeader(){
        return getTime()+from; 
    }*/
    public String getTime(){
        return Time.timeString(dateGmt); 
    }
    public String getDayTime(){
        return Time.dayString(dateGmt)+Time.timeString(dateGmt); 
    }
    //private TimeZone tz(){ return StaticData.getInstance().config.tz;}
    
    public int getColor() {
        if (highlite) 
            if (Config.getInstance().ghostMotor) return Colors.MSG_HIGHLIGHT;
        switch (messageType) {
            case MESSAGE_TYPE_IN: return Colors.MESSAGE_IN;
            case MESSAGE_TYPE_OUT: return Colors.MESSAGE_OUT;
            case MESSAGE_TYPE_PRESENCE: return Colors.MESSAGE_PRESENCE;
            case MESSAGE_TYPE_AUTH: return Colors.MESSAGE_AUTH;
            case MESSAGE_TYPE_HISTORY: return Colors.MESSAGE_HISTORY;
            case MESSAGE_TYPE_SUBJ:return Colors.MSG_SUBJ;
        }
        return 0;
    }
    public String toString(){
        StringBuffer time=new StringBuffer();
        if (messageType==MESSAGE_TYPE_PRESENCE) {
            time.append("[").append(getTime()).append("] ");
        }
        time.append(body);
        return time.toString(); 
    }
    
    public boolean isPresence() { return messageType==MESSAGE_TYPE_PRESENCE; }
    
    public int messageType;
    
    /** Отправитель сообщения */
    public String from;
    
    /** Тема сообщения */
    public String subject;

    /** Тело сообщения */
    private String body;

    /** Дата сообщения */
    public long dateGmt;
    
    public boolean unread = false;
    
    public boolean itemCollapsed;
    public int  itemHeight=-1;
    
    public void serialize(DataOutputStream os) throws IOException {
	os.writeUTF(from);
	os.writeUTF(body);
	os.writeLong(dateGmt);
	if (subject!=null) os.writeUTF(subject);
    }
    public Msg (DataInputStream is) throws IOException {
	from=is.readUTF();
	body=is.readUTF();
	dateGmt=is.readLong();
	try { subject=is.readUTF(); } catch (Exception e) { subject=null; }
    }

    public String getBody() { return body; }

    void setHighlite(boolean state) { highlite=state; }

    public boolean isHighlited() { return highlite; }
}
