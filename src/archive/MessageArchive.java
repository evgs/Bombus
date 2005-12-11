/*
 * MessageArchive.java
 *
 * Created on 11 Декабрь 2005 г., 2:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package archive;

import Client.Msg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author EvgS
 */
public class MessageArchive {
    
    RecordStore rs;
    Vector indexes;
    /** Creates a new instance of MessageArchive */
    public MessageArchive() {
	try {
	    rs=RecordStore.openRecordStore("archive", true);
	    int size=rs.getNumRecords();
	    indexes=new Vector(size);
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    
	    while (re.hasNextElement() ){
		indexes.addElement(new Integer(re.nextRecordId() ));
	    }
	    
	} catch (Exception e) { e.printStackTrace();}
    }
    public int size(){
	return indexes.size();
    }
    public Msg msg(int index){
	try {
	    index=((Integer)indexes.elementAt(index)).intValue();
	    ByteArrayInputStream bais=new ByteArrayInputStream(rs.getRecord(index));
	    DataInputStream dis=new DataInputStream(bais);
	    Msg msg=new Msg(dis);
	    dis.close();
	    return msg;
	} catch (Exception e) {}
	return null;
    }

    public static void store(Msg msg) {
	try {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    DataOutputStream dout = new DataOutputStream( bout );
	    msg.serialize( dout );
	    dout.close();
	    byte b[]=bout.toByteArray();
	    
	    RecordStore rs=RecordStore.openRecordStore("archive", true);
	    rs.addRecord(b, 0, b.length);
	    //new MessageArchive()
	} catch (Exception e) { e.printStackTrace(); }
    }
}
