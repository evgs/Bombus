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
    
    private File F;
    private int fd;

    public FileSiemens(String fileName) {
        // Siemens requires backslashes
        this.fileName=fileName=fileName.replace('/', '\\').substring(1);
    }
    
    public void openFile() throws IOException{
	F = new File();
        fd = F.open(fileName);
    }
    
    public void close() throws IOException{
	F.close(fd);
	F = null;
    }
    
    public long fileSize() throws IOException {
	return F.length(fd);
    }

    protected Vector rootDirs() {
        Vector rd = new Vector();
        rd.addElement("/0:/");
        rd.addElement("/1:/");
        rd.addElement("/2:/");
        rd.addElement("/3:/");
        rd.addElement("/4:/");
        return rd;
    }

    protected Vector dirs(boolean directoriesOnly) throws IOException{
        String[] directory=File.list(fileName);
        Vector rd=new Vector(directory.length + 1);
        for (int i = 0; i < File.list(fileName).length; i++) {
            if (File.isDirectory(fileName+directory[i])) {
                rd.addElement(directory[i]+"/");
            } else {
                rd.addElement(directory[i]);
            }
        }
        return rd;
    }

    public OutputStream openOutputStream() throws IOException {
        return null;
    }

    public InputStream openInputStream() throws IOException {
        return null;
    }  
}
