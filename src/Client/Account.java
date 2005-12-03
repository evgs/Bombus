/*
 * Account.java
 *
 * Created on 19 Март 2005 г., 21:52
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import com.alsutton.jabber.datablocks.Presence;
import images.RosterIcons;
import java.util.*;
import java.io.*;
import javax.microedition.midlet.MIDlet;
import midlet.Bombus;
import ui.IconTextElement;
import ui.ImageList;
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;
import Client.Roster;
import com.alsutton.jabber.*;

/**
 *
 * @author Eugene Stahov
 */
public class Account extends IconTextElement{
    
    public final static String storage="accnt_db";
            
    private String userName;
    private String password;
    private String server;
    private String hostAddr;
    private int port=5222;
    public boolean active;
    private boolean useSSL;
    private boolean plainAuth;
    
    private String nick="";
    private String resource="Bombus";
    
    //private String jid;
        
    /** Creates a new instance of Account */
    public Account() {
        super(RosterIcons.getInstance());
    }
    
    public static Account loadAccount(boolean launch){
	StaticData sd=StaticData.getInstance();
	Account a=sd.account=Account.createFromStorage(Config.getInstance().accountIndex);
        if (a!=null && launch){
            sd.roster.logoff();
            sd.roster.myStatus=Presence.PRESENCE_ONLINE;
            //sd.roster.querysign=true;
            new Thread(sd.roster).start();
        }
        return a;
    }

    public static Account createFromJad(){
        Account a=new Account();
        MIDlet m=Bombus.getInstance();
        try {
            a.userName=m.getAppProperty("def_user");
            a.password=m.getAppProperty("def_pass");
            a.server=m.getAppProperty("def_server");
            a.hostAddr=m.getAppProperty("def_ip");
        } catch (Exception e) { }
            if (a.server==null) return null;
        return a;
    }
    
    public static Account createFromDataInputStream(DataInputStream inputStream){
        
        int version=0;
        Account a=new Account();
        try {
            version    = inputStream.readByte();
            a.userName = inputStream.readUTF();
            a.password = inputStream.readUTF();
            a.server   = inputStream.readUTF();
            a.hostAddr = inputStream.readUTF();
            a.port     = inputStream.readInt();

            a.nick     = inputStream.readUTF();
            a.resource = inputStream.readUTF();
            if (version>=2) a.useSSL=inputStream.readBoolean();
            if (version>=3) a.plainAuth=inputStream.readBoolean();
            
        } catch (IOException e) { e.printStackTrace(); }
            
        return (a.userName==null)?null:a;
    }

    public String toString(){
        StringBuffer s=new StringBuffer();
        if (nick.length()!=0)
            s.append(nick);
        else {
            s.append(userName);
            s.append('@');
            s.append(server);
        }
        s.append('/');
        s.append(resource);
        return s.toString();
        //jid=userName+'@'+server+'/'+resource;
    }
    public String getJid(){
        return userName+'@'+server+'/'+resource;
    }
    /*public String getBareJid(){
        return userName+'@'+server;
    }*/
    
    public static Account createFromStorage(int index) {
        Account a=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {a=null; break;}
                a=createFromDataInputStream(is);
                //a.updateJidCache();
                index--;
            } while (index>-1);
            is.close();
        } catch (Exception e) { e.printStackTrace(); }
        return a;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        
        if (hostAddr==null) hostAddr="";
        
        try {
            outputStream.writeByte(3);
            outputStream.writeUTF(userName);
            outputStream.writeUTF(password);
            outputStream.writeUTF(server);
            outputStream.writeUTF(hostAddr);
            outputStream.writeInt(port);
            
            outputStream.writeUTF(nick);
            outputStream.writeUTF(resource);

            outputStream.writeBoolean(useSSL);
            outputStream.writeBoolean(plainAuth);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //public void onSelect(){ }
    //public String toString(){ return jid; }
    public int getColor(){ return 0x000000; }
    //public int getColorBGnd() {return 0xffffff;}
    protected int getImageIndex() {return active?0:5;}
    public void onSelect(){};

    public String getUserName() { return userName;  }
    public void setUserName(String userName) { this.userName = userName;  }

    public String getPassword() {  return password;  }
    public void setPassword(String password) { this.password = password;  }

    public String getServer() { return server; }
    public String getHostAddr() { return hostAddr; }
    
    public void setServer(String server) { this.server = server; }

    public void setHostAddr(String hostAddr) { this.hostAddr = hostAddr; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public boolean getUseSSL() { return useSSL; }
    public void setUseSSL(boolean ssl) { this.useSSL = ssl; }

    public boolean getPlainAuth() { return plainAuth; }
    public void setPlainAuth(boolean plain) { this.plainAuth = plain; }
    
    public String getResource() { return resource;  }
    public void setResource(String resource) { this.resource = resource;  }

    public String getNickName() { return (nick.length()==0)?null:nick;  }
    public void setNickName(String nick) { this.nick = nick;  }

    public JabberStream openJabberStream() throws java.io.IOException{
	StringBuffer url=new StringBuffer();
	    url.append((useSSL)?"ssl":"socket");
	    url.append("://");
	    if (hostAddr!=null) if (hostAddr.length()>0)
		url.append(hostAddr);
	    else
		url.append(server);
	    url.append(':');
	    url.append(port) ;
        return new JabberStream(  getServer(), url.toString(), null);    
    }
}
