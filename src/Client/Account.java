/*
 * Account.java
 *
 * Created on 19 Март 2005 г., 21:52
 */

package Client;
import java.util.*;
import java.io.*;
import javax.microedition.midlet.MIDlet;
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
    
    public final static String storage="accounts";
            
    private String userName;
    private String password;
    private String server;
    private String IP;
    private int port=5222;
    public boolean active;
        
    /** Creates a new instance of Account */
    public Account() {
        super(StaticData.getInstance().rosterIcons);
    }
    
    public static void launchAccount(){
        StaticData sd=StaticData.getInstance();
        sd.account=Account.createFromStorage(sd.account_index);
        new Thread(sd.roster).start();
    }

    public static Account createFromJad(){
        Account a=new Account();
        MIDlet m=StaticData.getInstance().midlet;
        try {
            a.userName=m.getAppProperty("def_user");
            a.password=m.getAppProperty("def_pass");
            a.server=m.getAppProperty("def_server");
            a.IP=m.getAppProperty("def_ip");
        } catch (Exception e) { }
            if (a.server==null) return null;
        return a;
    }
    
    public static Account createFromDataInputStream(DataInputStream inputStream){
        
        Account a=new Account();
        try {
            a.userName = inputStream.readUTF();
            a.password = inputStream.readUTF();
            a.server   = inputStream.readUTF();
            a.IP       = inputStream.readUTF();
            a.port     = inputStream.readInt();
        } catch (IOException e) { e.printStackTrace(); }
            
        return (a.userName==null)?null:a;
    }
    
    public static Account createFromStorage(int index) {
        Account a=null;
        DataInputStream is=NvStorage.ReadFileRecord(storage, 0);
        if (is==null) return null;
        try {
            do {
                if (is.available()==0) {a=null; break;}
                a=createFromDataInputStream(is);
                index--;
            } while (index>-1);
            is.close();
        } catch (IOException e) { e.printStackTrace(); }
        return a;
    }
    
    public void saveToDataOutputStream(DataOutputStream outputStream){
        
        if (IP==null) IP="";
        
        try {
            outputStream.writeUTF(userName);
            outputStream.writeUTF(password);
            outputStream.writeUTF(server);
            outputStream.writeUTF(IP);
            outputStream.writeInt(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //public void onSelect(){ }
    public String toString(){ return userName+'@'+server; }
    public int getColor(){ return 0x000000; }
    //public int getColorBGnd() {return 0xffffff;}
    protected int getImageIndex() {return active?0:5;}

    public String getUserName() { return userName;  }
    public void setUserName(String userName) { this.userName = userName;  }

    public String getPassword() {  return password;  }
    public void setPassword(String password) { this.password = password;  }

    public String getServer() {
        if (IP==null)  return server;
        if (IP.length()==0)  return server;
        return IP;
    }
    public String getServerN() { return server; }
    public String getServerI() { return IP; }
    
    public void setServer(String server) { this.server = server; }

    public void setIP(String IP) { this.IP = IP; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public JabberStream openJabberStream() throws java.io.IOException{
        return new JabberStream( 
                new meConnector( getServerN(), getServer(), getPort() ) );    
    }
}
