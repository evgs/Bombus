/*
 * ExtendedStatus.java
 *
 * Created on 27 Февраль 2005 г., 17:04
 */

package Client;
import java.io.*;
import java.util.Vector;
import ui.IconTextElement;
import ui.ImageList;
import com.alsutton.jabber.datablocks.Presence;
/**
 *
 * @author Eugene Stahov
 */
public class ExtendedStatus extends IconTextElement{
    
    private String name;    // status name
    private String status="";
    private int priority;
    int index;
    
    public static Vector createStatusList(){
        Vector v=new Vector(7);
        v.addElement(new ExtendedStatus(Presence.PRESENCE_ONLINE, Presence.PRS_ONLINE));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_CHAT, Presence.PRS_CHAT));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_AWAY, Presence.PRS_AWAY));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_XA, Presence.PRS_XA));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_DND, Presence.PRS_DND));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_INVISIBLE, Presence.PRS_INVISIBLE));
        v.addElement(new ExtendedStatus(Presence.PRESENCE_OFFLINE, "offline"));

        loadStatusFromStorage(v);
        return v;
    }
    public static void loadStatusFromStorage(Vector v){
        DataInputStream inputStream=NvStorage.ReadFileRecord("status", 0);
        if (inputStream!=null)
        try {
            for (int i=0;i<v.size();i++) {
                ExtendedStatus e=(ExtendedStatus)v.elementAt(i);
                e.priority=inputStream.readInt();
                e.status=inputStream.readUTF();
            }
            
            inputStream.close();
        } catch (Exception e) { 
            e.printStackTrace(); 
/*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(e, "ExS:51");
/*$USE_LOGGER$*///</editor-fold>
        }
        
    };
    public static void saveStatusToStorage(Vector v){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();

        try {
            for (int i=0;i<v.size();i++) {
                ExtendedStatus e=(ExtendedStatus)v.elementAt(i);
                outputStream.writeInt(e.priority);
                outputStream.writeUTF(e.status);
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
/*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(e, "ExS:68");
/*$USE_LOGGER$*///</editor-fold>
        }

        NvStorage.writeFileRecord(outputStream, "status", 0, true);
    }
    
    /** Creates a new instance of ExtendedStatus */
    private ExtendedStatus(int index, String name) {
        super(StaticData.getInstance().rosterIcons);
        this.index=index;
        this.name=name;
    }
    
    //public void onSelect(){}
    public String toString(){ 
        StringBuffer s=new StringBuffer(name);
        s.append(" (");
        s.append(priority);
        s.append(") ");
        if (status.length()>0) {
            s.append('"');
            s.append(status);
            s.append('"');
        }
        
        //return name+" ("+priority+") \""+status+"\""; 
        return s.toString();
    }
    public int getColor(){ return 0;}
    public int getImageIndex(){ return index;}

    public String getName() { return name; }
    public String getMessage() { return status; }
    public void setMessage(String s) { status=s; }

    public int getPriority() { return priority; }
    public void setPriority(int p) { priority=p; }
}
