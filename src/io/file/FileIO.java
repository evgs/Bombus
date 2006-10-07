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
    private final static int NOT_DETECTED=0;
    private final static int NONE=-1;
    private final static int JSR75=1;
    private final static int COM_MOTOROLA=2;
    private final static int COM_SIEMENS=3;
    
    private static int fileSystemType;
    
    protected String fileName;
    
    public static FileIO createConnection(String fileName) {
        if (fileSystemType==NOT_DETECTED) {
            fileSystemType=NONE;
            try {
                Class.forName("javax.microedition.io.file.FileConnection");
                fileSystemType=JSR75;
            } catch (Exception e) {}
            try {
                Class.forName("com.motorola.io.FileConnection");
                fileSystemType=COM_MOTOROLA;
            } catch (Exception e) {}
            try {
                Class.forName("com.siemens.mp.io.File");
                fileSystemType=COM_SIEMENS;
            } catch (Exception e) {}
        }
        switch (fileSystemType) {
            case JSR75: return new FileJSR75(fileName);
            case COM_MOTOROLA: return new FileComMotorolaIo(fileName);
        }
        return null;
    }
    
    public Vector fileList(boolean directoriesOnly) throws IOException{
        if (fileName.equals("/")) return rootDirs();
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
