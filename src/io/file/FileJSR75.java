/*
 * FileJSR75.java
 *
 * Created on 1 Октябрь 2006 г., 20:53
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 *
 * @author evgs
 */
class FileJSR75 extends FileIO{
    private javax.microedition.io.file.FileConnection fileConnection;

    /** Creates a new instance of FileJSR75 */
    public FileJSR75(String fileName) {
        this.fileName=fileName;
    }
    
    protected void openFile() throws IOException{
        fileConnection = (javax.microedition.io.file.FileConnection) Connector.open("file://" + fileName);
    }

    public OutputStream openOutputStream() throws IOException{
        if (fileConnection==null) openFile();
        if (!fileConnection.exists()) {
            fileConnection.create();
        } else {
            fileConnection.delete();
            fileConnection.create();
        }
        return fileConnection.openOutputStream();
    }

    public InputStream openInputStream() throws IOException{
        if (fileConnection==null) openFile();
        return fileConnection.openInputStream(); 
    }

    public void close() throws IOException{
        if (fileConnection!=null) fileConnection.close();
        fileConnection=null;
    }

    public long fileSize() throws IOException {
        return (fileConnection == null)? 0: fileConnection.fileSize();
    }

    protected Vector rootDirs() {
        Vector rd = new Vector();
        if (fileSystemType==JSR75_SIEMENS) {
            rd.addElement("0:/");
            rd.addElement("4:/");
            return rd;
        }
        Enumeration roots = javax.microedition.io.file.FileSystemRegistry.listRoots();
        while (roots.hasMoreElements())
            rd.addElement(((String) roots.nextElement()));
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) throws IOException{
        openFile();
        Enumeration dirs=fileConnection.list();
        close();
        Vector rd=new Vector();
        while (dirs.hasMoreElements()) {
            rd.addElement((String)dirs.nextElement());
        }
        return rd;
    }
}
