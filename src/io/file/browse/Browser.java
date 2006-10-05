/*
 * Browser.java
 *
 * Created on 26 Сентябрь 2006 г., 23:42
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file.browse;

import Client.Title;
import images.RosterIcons;
import io.file.FileIO;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.IconTextElement;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author evgs
 */
public class Browser extends VirtualList implements CommandListener{
 
    private Vector dir;
    
    Command cmdOk=new Command(SR.MS_BROWSE, Command.OK, 1);
    Command cmdSelect=new Command(SR.MS_SELECT, Command.SCREEN, 2);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.CANCEL, 99);
    
    private String path;
    private BrowserListener browserListener;
    
    /** Creates a new instance of Browser */
    public Browser(Display display, BrowserListener browserListener, boolean getDirectory) {
        super(display);
        
        this.browserListener=browserListener;
        
        setTitleItem(new Title(2, null, null));
        
        addCommand(cmdOk);
        if (getDirectory) addCommand(cmdSelect);
        addCommand(cmdBack);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        path="/";
        readDirectory(path);
        sort(dir);
    }
    
    protected int getItemCount() { return dir.size(); }
    
    protected VirtualElement getItemRef(int index) { return (VirtualElement) dir.elementAt(index); }
    
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) {
            if (!chDir("../")) {
                destroyView();
                return;
            }
            readDirectory(path);
            sort(dir);
        }
        
        
        if (command==cmdOk) eventOk();
        if (command==cmdSelect) {
            String f=((FileItem)getFocusedObject()).name;
            if (f.endsWith("/")) {
                if (browserListener==null) return;
                destroyView();
                browserListener.BrowserFilePathNotify(path+f);
                return;
            }
            //todo: choose directory here
        }
        if (command==cmdCancel) { destroyView(); }
    }
    
    
    private boolean chDir(String relativePath) {
        if (relativePath.equals("../")) {
            if (path.length()<2) return false;
            path=path.substring(0, 1+path.lastIndexOf('/', path.length()-2));
        } else {
            path+=relativePath;
        }
        return true;
    }
    
    private void readDirectory(String name) {
        getTitleItem().setElementAt(path, 0);
        
        dir=new Vector();
        
        try {
            FileIO f=io.file.FileIO.createConnection(name);
            
            Enumeration files=f.fileList(false).elements();
            
            while (files.hasMoreElements() )
                dir.addElement( new FileItem((String) files.nextElement()) );
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void eventOk() {
        String f=((FileItem)getFocusedObject()).name;
        if (!f.endsWith("/")) {
            if (browserListener==null) return;
            destroyView();
            browserListener.BrowserFilePathNotify(path+f);
            return;
        }
        if (!chDir(f)) { 
            destroyView(); 
            return; 
        }
        readDirectory(path);
        sort(dir);
        redraw();
    }
    
    
    public final void sort(Vector sortVector){
        synchronized (sortVector) {
            int f, i;
            FileItem temp, temp2;
            
            for (f = 1; f < sortVector.size(); f++) {
                temp=(FileItem)sortVector.elementAt(f);
                temp2=(FileItem)sortVector.elementAt(f-1);
                if ( temp.compare(temp2) >=0 ) continue;
                i = f-1;
                while (i>=0){
                    temp2=(FileItem)sortVector.elementAt(i);
                    if (temp2.compare(temp) <0) break;
                    sortVector.setElementAt(temp2,i+1);
                    i--;
                }
                sortVector.setElementAt(temp,i+1);
            }
        }
    }
    
    private class FileItem extends IconTextElement {
        
        public String name;
        private int iconIndex;
        
        public FileItem(String name) {
            super(RosterIcons.getInstance());
            this.name=name;
            //TODO: file icons
            iconIndex=name.endsWith("/")? RosterIcons.ICON_COLLAPSED_INDEX: RosterIcons.ICON_PRIVACY_ACTIVE;
        }
        protected int getImageIndex() { return iconIndex; }
        
        public int getColor() { return 0; }
        
        public String toString() { return name; }
        
        private int compare(FileItem fileItem) {
            int cpi=iconIndex-fileItem.iconIndex;
            if (cpi==0) cpi=name.compareTo(fileItem.name);
            return cpi;
        }
        
        
    }
}
