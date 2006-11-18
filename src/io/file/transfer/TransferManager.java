/*
 * TransferManager.java
 *
 * Created on 28 Октябрь 2006 г., 17:00
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io.file.transfer;

import Client.Title;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import locale.SR;
import ui.VirtualElement;
import ui.VirtualList;

/**
 *
 * @author Evg_S
 */
public class TransferManager extends VirtualList implements CommandListener{
    
    private Vector taskList;
    
    Command cmdBack=new Command(SR.MS_BACK, Command.BACK, 99);
    Command cmdClrF=new Command(SR.MS_HIDE_FINISHED, Command.SCREEN, 10);
    
    /** Creates a new instance of TransferManager */
    public TransferManager(Display display) {
        super(display);
        
        addCommand(cmdBack);
        addCommand(cmdClrF);
        setCommandListener(this);
        setTitleItem(new Title(2, null, SR.MS_TRANSFERS));
        
        taskList=TransferDispatcher.getInstance().getTaskList();
    }

    protected int getItemCount() { return taskList.size(); }

    protected VirtualElement getItemRef(int index) { return (VirtualElement) taskList.elementAt(index); }

    public void eventOk() {
        TransferTask t=(TransferTask) getFocusedObject();
        if (t!=null)
            if (t.isAcceptWaiting()) new TransferAcceptFile(display, t);
    }

    public void commandAction(Command c, Displayable d) {
        if (c==cmdClrF) {
            synchronized (taskList) {
                int i=0;
                while (i<taskList.size()) {
                    TransferTask task=(TransferTask) taskList.elementAt(i);
                    if (task.isStopped()) taskList.removeElementAt(i);
                    else i++;
                }
            }
            redraw();
        }
        if (c==cmdBack) {
            TransferDispatcher.getInstance().eventNotify();
            destroyView();
        }
        
    }
}
