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
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
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
    Command cmdInfo=new Command(SR.MS_INFO, Command.SCREEN, 3);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.CANCEL, 99);
    
    private String path;
    private BrowserListener browserListener;
    
    /** Creates a new instance of Browser */
    public Browser(String path, Display display, BrowserListener browserListener, boolean getDirectory) {
        super(display);
        
        this.browserListener=browserListener;
        
        setTitleItem(new Title(2, null, null));
        
        addCommand(cmdOk);
        if (getDirectory) {
            addCommand(cmdSelect);
        } else {
            addCommand(cmdInfo);
        }
        addCommand(cmdBack);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        // test for empty path
        if (path==null) path="/";
        if (path.length()==0) path="/";
       
        // trim filename
        int l=path.lastIndexOf('/');
        if (l<0) {  path="/"; 
        } else path=path.substring(0,l+1);

        chDir(path);
    }
    
    protected int getItemCount() { return dir.size(); }
    
    protected VirtualElement getItemRef(int index) { return (VirtualElement) dir.elementAt(index); }
    
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdBack) {
            if (!chDir("../")) {
                destroyView();
                return;
            }
            redraw();
        }
        
        
        if (command==cmdOk) eventOk();
        if (command==cmdSelect) {
            String f=((FileItem)getFocusedObject()).name;
            if (f.endsWith("/")) {
                if (f.startsWith("../")) f="";
                if (browserListener==null) return;
                destroyView();
                browserListener.BrowserFilePathNotify(path+f);
                return;
            }
            //todo: choose directory here, drop ../
        }
        if (command==cmdInfo) {
            String f=((FileItem)getFocusedObject()).name;
            if (f.endsWith("/")) return;
            String ext=f.substring(f.lastIndexOf('.')+1).toLowerCase();
            Image img=null;
            try {
                FileIO fio=FileIO.createConnection(path+f);
                InputStream is=fio.openInputStream();
                String info="Size="+String.valueOf(fio.fileSize());
                String imgs="png.jpg.jpeg.gif";
                if (imgs.indexOf(ext)>=0) {
                    if (fio.fileSize()<65536) 
                        img=Image.createImage(is);
                }
                is.close();
                fio.close();
                
                Alert finfo=new Alert(f, info, img, null);
                finfo.setTimeout(15*1000);
                display.setCurrent(finfo, this);
                
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (command==cmdCancel) { destroyView(); }
    }
    
    
    private boolean chDir(String relativePath) {
        String focus="";
        if (relativePath.startsWith("/")) {
            path=relativePath;
        } else if (relativePath.startsWith("../")) {
            if (path.length()<2) return false;
            int remainderPos=path.lastIndexOf('/', path.length()-2) + 1;
            focus=path.substring(remainderPos);
            path=path.substring(0, 1+path.lastIndexOf('/', path.length()-2));
        } else {
            path+=relativePath;
        }
        readDirectory(this.path);
        sort(dir);

        for (int i=0; i<dir.size(); i++) {
            if ( ((FileItem)dir.elementAt(i)).name.equals(focus) ) {
                moveCursorTo(i, true);
                return true;
            }
        }
        moveCursorHome();
        return true;
    }
    
    private void readDirectory(String name) {
        getTitleItem().setElementAt(path, 0);
        
        dir=new Vector();
        
        try {
            FileIO f=FileIO.createConnection(name);
            
            Enumeration files=f.fileList(false).elements();
            
            while (files.hasMoreElements() )
                dir.addElement( new FileItem((String) files.nextElement()) );
            
        } catch (Exception ex) {
            dir.addElement( new FileItem("../(Restricted Access)"));
            //dir.addElement( new FileItem("../ Ex: "+ex.getClass().getName()+" "+ex.toString()));
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
        redraw();
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
        
        public int compare(IconTextElement right){
            FileItem fileItem=(FileItem) right;
            
            int cpi=iconIndex-fileItem.iconIndex;
            if (cpi==0) cpi=name.compareTo(fileItem.name);
            return cpi;
        }
    }
}
