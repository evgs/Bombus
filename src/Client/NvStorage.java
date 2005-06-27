/*
 * NvStorage.java
 *
 * Created on 22 Март 2005 г., 22:56
 */

package Client;
import com.alsutton.jabber.JabberDataBlock;
import java.io.*;
import ui.strconv;

/*#!USE_SIEMENS_FILES#*///<editor-fold>
import javax.microedition.rms.*;
/*$!USE_SIEMENS_FILES$*///</editor-fold>
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--import com.siemens.mp.io.File;
/*$USE_SIEMENS_FILES$*///</editor-fold>

/**
 *
 * @author Eugene Stahov
 */
public class NvStorage {
    
    private static final int PATH_CFG=0;
    private static final int PATH_MSG=1;
    
    /**
     * Opens RMS record from named store
     * and returns it as DataInputStream
     */
    static public DataInputStream ReadFileRecord(String name, int index){
        DataInputStream istream=null;
        
/*#!USE_SIEMENS_FILES#*///<editor-fold>
        RecordStore recordStore=null;
        try {
            
            recordStore = RecordStore.openRecordStore(name, false);
            byte[] b=recordStore.getRecord(index+1);
            
            if (b.length!=0)         
            istream=new DataInputStream( new ByteArrayInputStream(b) );
            
        } catch (Exception e) { }
        finally { 
            try { recordStore.closeRecordStore(); } catch (Exception e) {} }
/*$!USE_SIEMENS_FILES$*///</editor-fold>
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--
//--        try {
//--        File f=new File();
//--        
//--        String n=getPath(name, PATH_CFG);
//--        System.out.println("Read "+n);
//--        int descriptor=f.open(n);
//--        if (descriptor<0) return null;
//--        int len=f.length(descriptor);
//--        if (len<1) return null; 
//--        
//--        byte[] b=new byte[len];
//--        f.read(descriptor, b, 0, len);
//--        f.close(descriptor);
//--        
//--        istream=new DataInputStream( new ByteArrayInputStream(b) );
//--        } catch (Exception e) { e.printStackTrace(); }
//--        
/*$USE_SIEMENS_FILES$*///</editor-fold>
        
        return istream;
    }
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--    public static String getPath(String name, int path_index){
//--        
//--        String path=null;
//--        switch (path_index) {
//--            case PATH_CFG: path=StaticData.getInstance().config.siemensCfgPath; break;
//--            case PATH_MSG: path=StaticData.getInstance().config.msgPath; break;
//--        }
//--        //System.out.println("path="+path);
//--        //System.out.println("path="+path+name);
//--        // verification
//--        
//--        // временно заблокирована проверка валидности пути. для C55
//--        /*
//--        try {
//--            File f=new File();
//--            if (!f.isDirectory(path)) return name;
//--        } catch (Exception e) { 
//--            //e.printStackTrace(); 
//--            return name; 
//--        } 
//--         */
//--        return path+name;
//--    }
//--    
/*$USE_SIEMENS_FILES$*///</editor-fold>

    private static ByteArrayOutputStream baos;
    /** Creates DataOutputStream based on ByteOutputStream  */
    static public DataOutputStream CreateDataOutputStream(){
        if (baos!=null) return null;
        DataOutputStream ostream=new DataOutputStream( baos=new ByteArrayOutputStream());
        return ostream;
    }
    
    static public boolean writeFileRecord (
            DataOutputStream ostream, 
            String name, int index, 
            boolean rewrite)
    {
        ByteArrayOutputStream lbaos=baos;
        baos=null; // освободим для следующего
        byte[] b=lbaos.toByteArray();
        
/*#!USE_SIEMENS_FILES#*///<editor-fold>
        try {
            if (rewrite) RecordStore.deleteRecordStore(name);
        } catch (Exception e) {}

        RecordStore recordStore;
        try {
            recordStore = RecordStore.openRecordStore(name, true);
        } catch (Exception e) { return false;}
        
        try {
            try {
                recordStore.setRecord(index+1, b, 0, b.length);
            } catch (InvalidRecordIDException e) { recordStore.addRecord(b, 0, b.length); }
            recordStore.closeRecordStore();
            ostream.close();
        } catch (Exception e) { e.printStackTrace(); return false; }
/*$!USE_SIEMENS_FILES$*///</editor-fold>
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--        File f=new File();
//--        String n=getPath(name, PATH_CFG);
//--        System.out.println("Write "+n);
//--
//--        try {
//--            if (rewrite) f.delete(n);
//--        } catch (Exception e) {}
//--
//--        int descriptor;
//--        try {
//--            descriptor=f.open(n);
//--            f.write(descriptor, b, 0, b.length);
//--            f.close(descriptor);
//--        } catch (Exception e) { e.printStackTrace(); return false; }
/*$USE_SIEMENS_FILES$*///</editor-fold>
        return true;
    }
    
    public final static boolean appendFile(String URL, String append_data){
        try{
            /*System.out.println(
                    getPath(strconv.convUnicodeToAscii(URL), PATH_MSG)+" "+
                    strconv.convUnicodeToAscii(append_data));*/
/*#USE_SIEMENS_FILES#*///<editor-fold>
//--            File file1 = new File();
//--            int fd = file1.open(getPath(strconv.convUnicodeToAscii(URL+".txt"), PATH_MSG));
//--            byte abyte0[] = (strconv.convUnicodeToAscii(append_data)).getBytes();
//--            file1.seek(fd, file1.length(fd));
//--            file1.write(fd, abyte0, 0, abyte0.length);
//--            file1.close(fd);
/*$USE_SIEMENS_FILES$*///</editor-fold>

        } catch (Exception e) {
            e.printStackTrace();
            return false;   //облом
        }
        return true;
    }
    
/*#USE_LOGGER#*///<editor-fold>
//--    public final static void log(String logMsg){
//--        if (StaticData.getInstance().config.logMsg) { 
//--            logS("MSG="); 
//--            logS(logMsg); 
//--            logCrLf(); 
//--        }
//--    }
//--    public final static void log(JabberDataBlock data, boolean incoming){
//--        if (StaticData.getInstance().config.logStream)
//--        {
//--            logS(incoming?"RECV=":"SENT=");
//--            logCrLf();
//--            data.formatOut("");
//--        }
//--    }
//--    public final static void log(Exception e){
//--        if (StaticData.getInstance().config.logEx)
//--        {
//--            logS("EXCEPTION=");
//--            logS(e.getMessage());
//--            logCrLf();
//--        }
//--    }
//--    
//--    synchronized public final static void logS(String logMsg){
//--        appendFile("_syslog", logMsg);
//--    }
//--    public final static void logCrLf(){ logS("\n"); }
/*$USE_LOGGER$*///</editor-fold>
}
