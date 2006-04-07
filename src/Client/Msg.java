/*
 * msg.java
 *
 * Created on 6 Январь 2005 г., 19:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
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
    public final static int MESSAGE_TYPE_AUTH=15;
    
    /** Creates a new instance of msg */
    public Msg(int messageType, String from, String subj, String body) {
        this.messageType=messageType;
        this.from=from;
        this.body=body;
        this.subject=subj;
        this.dateGmt=Time.localTime();
        if (messageType>=MESSAGE_TYPE_IN) unread=true;
        if (messageType==MESSAGE_TYPE_PRESENCE)
            itemCollapsed=true;
    }
    
    public void onSelect(){}
    public String getMsgHeader(){
        return getTime()+from; 
    }
    public String getTime(){
        return '['+Time.timeString(dateGmt)+"] "; 
    }
    public String getDayTime(){
        return '['+Time.dayString(dateGmt)+Time.timeString(dateGmt)+"] "; 
    }
    //private TimeZone tz(){ return StaticData.getInstance().config.tz;}
    
    public int getColor() {
        switch (messageType) {
            case MESSAGE_TYPE_IN: return Colors.MESSAGE_IN;
            case MESSAGE_TYPE_OUT: return Colors.MESSAGE_OUT;
            case MESSAGE_TYPE_PRESENCE: return Colors.MESSAGE_PRESENCE;
            case MESSAGE_TYPE_AUTH: return Colors.MESSAGE_AUTH;
            case MESSAGE_TYPE_HISTORY: return Colors.MESSAGE_HISTORY;
        }
        return 0;
    }
    public String toString(){
        return (messageType==MESSAGE_TYPE_PRESENCE)?getTime()+body:body; 
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
    public int  itemHeight=14;
    
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

    public String getBody() {
        return body;
    }
}
