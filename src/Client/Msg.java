/*
 * msg.java
 *
 * Created on 6 Январь 2005 г., 19:20
 */

package Client;
import java.util.*;
import ui.Time;
import Client.MessageList;

/**
 *
 * @author Eugene Stahov
 */
public class Msg //implements MessageList.Element
{
    
    public final static int MESSAGE_TYPE_IN=0;
    public final static int MESSAGE_TYPE_OUT=1;
    public final static int MESSAGE_TYPE_PRESENCE=2;
    
    /** Creates a new instance of msg */
    public Msg(int messageType, String from, String body) {
        this.messageType=messageType;
        this.from=from;
        this.body=body;
        this.date=new Date(System.currentTimeMillis());
        if (messageType==MESSAGE_TYPE_IN) unread=true;
    }
    
    public void onSelect(){}
    public String getMsgHeader(){
        return getTime()+from; 
    }
    public String getTime(){
        return '['+Time.timeString(date)+"] "; 
    }
    public String getDayTime(){
        return '['+Time.dayString(date)+Time.timeString(date)+"] "; 
    }
    public int getColor1() {
        switch (messageType) {
            case MESSAGE_TYPE_IN: return 0x0000B0;
            case MESSAGE_TYPE_OUT: return 0xB00000;
            case MESSAGE_TYPE_PRESENCE: return 0x006000;
        }
        return 0;
    }
    public int getColor2(){ return 0; }
    public String toString(){ 
        return (messageType==MESSAGE_TYPE_PRESENCE)?getTime()+body:body; 
    }
    
    public boolean isPresence() { return messageType==MESSAGE_TYPE_PRESENCE; }
    

    /** 0=in, 1=out, 2=presence */
    public int messageType=0;
    
    /** Отправитель сообщения */
    public String from;
    
    /** Тело сообщения */
    public String body;

    /** Дата сообщения */
    public Date date;

    public boolean unread = false;
    
}
