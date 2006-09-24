/*
 * ArrayLoader.java
 *
 * Created on 24 Сентябрь 2006 г., 1:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

/*
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
*/

/**
 *
 * @author evgs
 */
public class ArrayLoader {
    
    /** Creates a new instance of ArrayLoader */
    public ArrayLoader() {
    }
    public int[] readIntArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            int[] arrayInt=new int[len];
            
            for (int i=0; i<len;i++) {
                arrayInt[i]=is.readInt();
            }
            return arrayInt;
        } catch (Exception ex) {}
        
        return null;
    }
    
    public short[] readShortArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            short[] arrayShort=new short[len];
            
            for (int i=0; i<len;i++) {
                arrayShort[i]=is.readShort();
            }
            return arrayShort;
        } catch (Exception ex) {}
        
        return null;
    }
    public byte[] readByteArray(String name) {
        try {
            InputStream in = this.getClass().getResourceAsStream(name);
            DataInputStream is=new DataInputStream(in);
            int len=is.readInt();
            byte[] arrayByte=new byte[len];

            is.read(arrayByte, 0, len);
            return arrayByte;
        } catch (Exception ex) {}
        
        return null;
    }

/*
    public static void writeIntArray(String name, int[] intArray) {
        try {
            for (Enumeration e=FileSystemRegistry.listRoots(); e.hasMoreElements(); ){
                String root = (String) e.nextElement();
                System.out.println(root);
            }
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(intArray.length);
            
            for (int i=0; i<intArray.length; i++) os.writeInt(intArray[i]);
            
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        

    }

    public static void writeShortArray(String name, short[] shortArray) {
        try {
            for (Enumeration e=FileSystemRegistry.listRoots(); e.hasMoreElements(); ){
                String root = (String) e.nextElement();
                System.out.println(root);
            }
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(shortArray.length);
            
            for (int i=0; i<shortArray.length; i++) os.writeShort(shortArray[i]);
            
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void writeByteArray(String name, byte[] byteArray) {
        try {
            FileConnection fc=(FileConnection)Connector.open("file:///root1/" + name);
            if (fc.exists()) return;
            fc.create();
            DataOutputStream os=fc.openDataOutputStream();
            os.writeInt(byteArray.length);
            
            os.write(byteArray, 0, byteArray.length);
           
            os.close();
            fc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/
}
