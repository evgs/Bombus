/*
 * FileSiemens.java
 *
 * Created on 7 Октябрь 2006 г., 23:20
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */


package io.file;

import com.siemens.mp.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class FileSiemens extends FileIO{
    
    private File f;
    private int fd;

    public FileSiemens(String fileName) {
        //System.out.println("Siemens fs accessor for "+fileName);
        // Siemens requires backslashes
        this.fileName=fileName=fileName.replace('/', '\\').substring(1);
    }
    
    public void openFile() throws IOException{
	f = new File();
        fd = f.open(fileName);
    }
    
    public void close() throws IOException{
	f.close(fd);
	f = null;
    }
    
    public long fileSize() throws IOException {
	return f.length(fd);
    }

    protected Vector rootDirs() {
        //System.out.println("Siemens root");
        Vector rd = new Vector();
        rd.addElement("0:/");
        rd.addElement("1:/");
        rd.addElement("2:/");
        rd.addElement("3:/");
        rd.addElement("4:/");
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) throws IOException{
        String[] directory=File.list(fileName);
        Vector rd=new Vector();
        
        if (directory!=null) 
        for (int i = 0; i < File.list(fileName).length; i++) {
            if (directory[i].endsWith("/")) { // x75 feature? (excepting s75)
                rd.addElement(directory[i]);
            } else if (File.isDirectory(fileName+directory[i])) {
                rd.addElement(directory[i]+"/");
            } else {
                rd.addElement(directory[i]);
            }
        }
        return rd;
    }

    public OutputStream openOutputStream() throws IOException {
        return new FileSiemensOutputStream(f, fd);
    }

    public InputStream openInputStream() throws IOException {
        openFile();
        return new FileSiemensInputStream(f, fd);
    }  
}

class FileSiemensInputStream extends InputStream {
    private int fileDescriptor;
    private File f;

    public FileSiemensInputStream(File f, int fd) {
        this.f=f; this.fileDescriptor=fd;
    }
    
    public int read() throws IOException {
        byte buf[]=new byte[1];
        f.read(fileDescriptor, buf, 0, 1);
        return buf[0];
    }

    public int read(byte[] b, int off, int len) throws IOException {  return f.read(fileDescriptor, b, off, len); }

    public int read(byte[] b) throws IOException {  return f.read(fileDescriptor, b, 0, b.length);  }
}

class FileSiemensOutputStream extends OutputStream {
    private int fileDescriptor;
    private File f;

    public FileSiemensOutputStream(File f, int fd) {
        this.f=f; this.fileDescriptor=fd;
    }
    
    public void write(int i) throws IOException {
        byte buf[]=new byte[1];
        f.write(fileDescriptor, buf, 0, 1);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {  f.write(fileDescriptor, b, off, len); }

    public void write(byte[] b) throws IOException {  f.write(fileDescriptor, b, 0, b.length);  }
}