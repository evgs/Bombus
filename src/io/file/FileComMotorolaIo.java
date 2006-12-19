/*
 * FileComMotorolaIo.java
 *
 * Created on 1 Октябрь 2006 г., 20:54
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file;

import java.io.IOException;
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

    protected void openFile() throws IOException {
        String uri="file:///" + fileName;
        System.out.println("openFile: name="+fileName);
        System.out.println("  URI="+ uri);
        fileConnection = (com.motorola.io.FileConnection) Connector.open(uri);
    }

    public OutputStream openOutputStream() throws IOException {
        if (fileConnection==null) openFile();
        if (!fileConnection.exists()) {
            fileConnection.create();
        } else {
            fileConnection.delete();
            fileConnection.create();
        }
        return fileConnection.openOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        if (fileConnection==null) openFile();
        return fileConnection.openInputStream(); 
    }

    public void close() throws IOException {
        if (fileConnection!=null) fileConnection.close();
        fileConnection=null;
    }

    public long fileSize() {
        return (fileConnection == null)? 0: fileConnection.fileSize();
    }

    protected Vector rootDirs() {
        System.out.println("roots listing...");
        String[] roots = com.motorola.io.FileSystemRegistry.listRoots();
        Vector rd=new Vector(roots.length);
        for (int i = 0; i < roots.length; i++)
            rd.addElement(roots[i].substring(1));
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) throws IOException {
        System.out.println("dirs listing...");
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
