/*
 * FileComMotorolaIo.java
 *
 * Created on 1 Октябрь 2006 г., 20:54
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 *
 * @author evgs
 */
class FileComMotorolaIo extends FileIO{

    private com.motorola.io.FileConnection fileConnection;

    /** Creates a new instance of FileComMotorolaIo */
    public FileComMotorolaIo(String fileName) {
        this.fileName=fileName;
    }

    protected void openFile() {
        fileConnection = (com.motorola.io.FileConnection) Connector.open("file://" + fileName);
    }

    public OutputStream openOutputStream() {
        if (!fileConnection.exists()) {
            fileConnection.create();
        } else {
            fileConnection.delete();
            fileConnection.create();
        }
        return fileConnection.openOutputStream();
    }

    public InputStream openInputStream() { return fileConnection.openInputStream(); }

    public void close() {
        if (fileConnection!=null) fileConnection.close();
        fileConnection=null;
    }

    public long fileSize() {
        return (fileConnection == null)? 0: fileConnection.fileSize();
    }

    protected Vector rootDirs() {
        String[] roots = com.motorola.io.FileSystemRegistry.listRoots();
        Vector rd=new Vector(roots.length);
        for (int i = 0; i < roots.length; i++)
            rd.addElement(roots[i].substring(1));
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) {
        openFile();
        String[] list = fileConnection.list();
        close();
        
        Vector rd=new Vector(list.length + 1);
        for (int i = 0; i < list.length; i++) {
            if (directoriesOnly & !list[i].endsWith("/")) continue;
            rd.addElement(list[i].substring(fileName.length()));
        }
        return rd;
    }
}
