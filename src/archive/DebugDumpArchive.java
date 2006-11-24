/*
 * DebugDumpArchive.java
 *
 * Created on 25 Ноябрь 2006 г., 1:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package archive;

import Client.Msg;
import io.file.FileIO;
import io.file.browse.Browser;
import io.file.browse.BrowserListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

/**
 *
 * @author Evg_S
 */
public class DebugDumpArchive implements BrowserListener{
    
    /** Creates a new instance of DebugDumpArchive */
    public DebugDumpArchive(Display display) {
        new Browser("",display, this, true);
    }

    public void BrowserFilePathNotify(String pathSelected) {
        String fileName=pathSelected+"archiveDump.txt";
        FileIO f=FileIO.createConnection(fileName);
        OutputStream os;
        try {
            os = f.openOutputStream();
        } catch (IOException ex) { return; }
        PrintStream ps=new PrintStream(os);
        
        ps.println("Creating archive dump");
        
        try {
            ps.println("Opening RecordStore...");
	    RecordStore rs=RecordStore.openRecordStore("archive", true);
            
            ps.println("\nGetting number of records...");
  	    int size=rs.getNumRecords();
            ps.print("Records count="); ps.println(size);
            
            ps.println("\nBuilding index...");
            int index[]=new int[size];
            
	    RecordEnumeration re=rs.enumerateRecords(null, null, false);
	    ps.println("Enum constructed"); ps.flush();
            int i=0;
	    while (re.hasNextElement() ){
                ps.print("id="); ps.flush();
                int id=re.nextRecordId();
		index[i++]=id;
                ps.println(id); ps.flush();

	    }
            
            ps.println("RAW dump:");
            
            for (i=0; i<size; i++) {
                int id=index[i];
                byte b[]=rs.getRecord(id);
                
                ps.print("record="); ps.print(i); ps.print(" index="); ps.println(id);
                dump(ps, b);
                ps.println();
            }

            ps.println("\nMSG dump:");
            
            for (i=0; i<size; i++) {
                int id=index[i];
                byte b[]=rs.getRecord(id);
                
                ByteArrayInputStream bais=new ByteArrayInputStream(b);
                DataInputStream dis=new DataInputStream(bais);
                Msg msg=new Msg(dis);
                dis.close();

                ps.print("record="); ps.print(i); ps.print(" index="); ps.println(id);
                ps.print("from="); ps.println(msg.from);
                ps.print("body="); ps.println(msg.getBody());
                ps.print("dateGmt="); ps.println(msg.dateGmt);
                ps.print("subj="); ps.println(msg.subject);
                //dump(ps, b);
                ps.println();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            ps.print("Exception caught: ");
            ps.println(e.toString());
        }
        ps.close();
        
        try {
            os.close();
            f.close();
        } catch (Exception ex) { }
    }

    private void dump(PrintStream ps, byte[] b) {
        int i=0;
        StringBuffer s=new StringBuffer("0123456789abcdef");
        while (i<b.length) {
            int p;
            for (p=0; p<16; p++) {
                byte c=0;
                if (p+i < b.length) {
                    c = (byte) ((b[p+i] >> 4) &0x0f);
                    if (c > 9)   c = (byte) ( c-10 + 'a');
                    else  c = (byte) (c + '0');
                    ps.print ((char)c);
                    
                    c = (byte) (b[p+i] &0x0f);
                    if (c > 9)   c = (byte) ( c-10 + 'a');
                    else  c = (byte) (c + '0');
                    ps.print ((char)c);
                    ps.print (" ");

                    c=b[p+i];
                } else {
                    ps.print ("   ");
                }
                if (c<0x20) c=0x20;
                s.setCharAt(p, (char) c);
            }
            ps.println(s.toString());
            i+=16;
        }
    }
    
}
