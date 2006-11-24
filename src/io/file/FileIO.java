/*
 * File.java
 *
 * Created on 1 Октябрь 2006 г., 20:52
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 *
 * @author evgs
 */
public abstract class FileIO {
    protected final static int NOT_DETECTED=0;
    protected final static int NONE=-1;
    protected final static int JSR75=1;
    protected final static int COM_MOTOROLA=2;
    protected final static int COM_SIEMENS=3;
    protected final static int JSR75_SIEMENS=4;
    
    protected static int fileSystemType;
    
    protected String fileName;
    
    public static FileIO createConnection(String fileName) {
        if (fileSystemType==NOT_DETECTED) {
            fileSystemType=NONE;
            try {
                Class.forName("javax.microedition.io.file.FileConnection");
                fileSystemType=JSR75;
                if (Info.Version.getPlatformName().startsWith("SIE")) fileSystemType=JSR75_SIEMENS;
            } catch (Exception e) {}
            try {
                Class.forName("com.motorola.io.FileConnection");
                fileSystemType=COM_MOTOROLA;
            } catch (Exception e) {}
            try {
                Class.forName("com.siemens.mp.io.File");
                fileSystemType=COM_SIEMENS;
            } catch (Exception e) {}
            //System.out.println("Detected fs:"+fileSystemType );
        }
        switch (fileSystemType) {
            case JSR75_SIEMENS:
            case JSR75: return new FileJSR75(fileName);
            case COM_MOTOROLA: return new FileComMotorolaIo(fileName);
            case COM_SIEMENS: return new FileSiemens(fileName);
        }
        return null;
    }
    
    public Vector fileList(boolean directoriesOnly) throws IOException{
        if (fileName.length()==0) return rootDirs();
        Vector dir=dirs(directoriesOnly);
        dir.addElement("../");
        return dir;
    }
    
    public abstract OutputStream openOutputStream() throws IOException;
   
    public abstract InputStream openInputStream() throws IOException;
    
    public abstract void close() throws IOException;
    
    public abstract long fileSize() throws IOException;

    protected abstract Vector rootDirs();
    protected abstract Vector dirs(boolean directoriesOnly) throws IOException;
}
