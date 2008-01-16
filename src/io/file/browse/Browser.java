/*
 * Browser.java
 *
 * Created on 26.09.2006, 23:42
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
import ui.AlertBox;
import ui.Colors;
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
    Command cmdRoot=new Command(SR.MS_ROOT, Command.SCREEN, 4);
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 98);
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.EXIT, 99);
    
    private String path;
    private BrowserListener browserListener;
    
    /** Creates a new instance of Browser */
    public Browser(String path, Display display, BrowserListener browserListener, boolean getDirectory) {
        super(display);
        
        this.browserListener=browserListener;
        this.path="";
        
        setTitleItem(new Title(2, null, null));
        
        addCommand(cmdOk);
        if (getDirectory) {
            addCommand(cmdSelect);
        } else {
            addCommand(cmdInfo);
        }
        addCommand(cmdBack);
        addCommand(cmdRoot);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        // test for empty path
        if (path==null) path="";
        //if (path.length()==0) path="/";
       
        // trim filename
        int l=path.lastIndexOf('/');
        if (l<0) {  path=""; 
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
        
        if (command==cmdRoot) {
            path="";
            chDir(path);
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
                
                new AlertBox(f, info, img, display, this);

            } catch (Exception e) { e.printStackTrace(); }
        }
        if (command==cmdCancel) { destroyView(); }
    }
    
    
    private boolean chDir(String relativePath) {
        String focus="";
        if (relativePath.startsWith("/")) {
            path=relativePath;
        } else if (relativePath.startsWith("../")) {
            if (path.length()==0) return false;
            if (path.length()==1) { 
                path="";
            } else {
                int remainderPos=path.lastIndexOf('/', path.length()-2) + 1;
                focus=path.substring(remainderPos);
                path=path.substring(0, 1+path.lastIndexOf('/', path.length()-2));
            }
        } else {
            //if (path.length()==0) path="/";
            path+=relativePath;
        }
        readDirectory(this.path);
        sort(dir);

        for (int i=0; i<dir.size(); i++) {
            if ( ((FileItem)dir.elementAt(i)).name.equals(focus) ) {
                moveCursorTo(i);
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
        
        public int getColor() { return Colors.LIST_INK; }
        
        public String toString() { return name; }
        
        public int compare(IconTextElement right){
            FileItem fileItem=(FileItem) right;
            
            int cpi=iconIndex-fileItem.iconIndex;
            if (cpi==0) cpi=name.compareTo(fileItem.name);
            return cpi;
        }
    }
}
