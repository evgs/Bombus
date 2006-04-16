/*
 * AccountPicker.java
 *
 * Created on 19 Март 2005 г., 23:26
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import images.RosterIcons;
import locale.SR;
import midlet.Bombus;
import ui.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import ui.controls.NumberField;


/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect 
        extends VirtualList 
        implements CommandListener{

    Vector accountList;
    int activeAccount;
    
    Command cmdLogin=new Command(SR.MS_LOGIN, Command.OK,1);
    Command cmdSelect=new Command(SR.MS_NOLOGIN, Command.SCREEN,2);
    Command cmdAdd=new Command(SR.MS_NEW_ACCOUNT, Command.SCREEN,3);
    Command cmdEdit=new Command(SR.MS_EDIT,Command.ITEM,3);
    Command cmdDel=new Command(SR.MS_DELETE,Command.ITEM,4);
    Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99);
    Command cmdQuit=new Command(SR.MS_APP_QUIT,Command.SCREEN,10);
    
    /** Creates a new instance of AccountPicker */
    public AccountSelect(Display display, boolean enableQuit) {
        super();
        //this.display=display;

        setTitleItem(new Title(SR.MS_ACCOUNTS));
        
        accountList=new Vector();
        Account a;
        
        int index=0;
        activeAccount=Config.getInstance().accountIndex;
        do {
            a=Account.createFromStorage(index);
            if (a!=null) {
                accountList.addElement(a);
                a.active=(activeAccount==index);
                index++;
             }
       } while (a!=null);
        if (accountList.isEmpty()) {
            a=Account.createFromJad();
            if (a!=null) {
                //a.updateJidCache();
                accountList.addElement(a);
                rmsUpdate();
            }
        }
        attachDisplay(display);
        addCommand(cmdAdd);
        
        if (enableQuit) addCommand(cmdQuit);
        
        commandState();
        setCommandListener(this);
    }
    
    void commandState(){
        if (accountList.isEmpty()) {
            removeCommand(cmdEdit);
            removeCommand(cmdDel);
            removeCommand(cmdSelect);
            removeCommand(cmdLogin);
            removeCommand(cmdCancel);
        } else {
            addCommand(cmdEdit);
            addCommand(cmdDel);
            addCommand(cmdLogin);
            addCommand(cmdSelect);
            if (activeAccount>=0)
                addCommand(cmdCancel);  // нельзя выйти без активного аккаунта
        }
    }

    public VirtualElement getItemRef(int Index) { return (VirtualElement)accountList.elementAt(Index); }
    protected int getItemCount() { return accountList.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdQuit) {
            destroyView();
            Bombus.getInstance().notifyDestroyed();
        }
        if (c==cmdCancel) {
            destroyView();
            //Account.launchAccount();
            //StaticData.getInstance().account_index=0;
        }
        if (c==cmdLogin) switchAccount(true);
        if (c==cmdSelect) switchAccount(false);
        if (c==cmdEdit) new AccountForm(this, display,(Account)getFocusedObject());
        if (c==cmdAdd) {
            new AccountForm(this, display, null);
        }
        if (c==cmdDel) {
            accountList.removeElement(getFocusedObject());
            rmsUpdate();
            moveCursorHome();
            commandState();
            redraw();
        }
        
    }
    
    private void switchAccount(boolean login){
        destroyView();
	Config cf=Config.getInstance();
        cf.accountIndex=cursor;
        cf.saveToStorage();
        Account.loadAccount(login);
    }
    
    public void eventOk(){ switchAccount(true); }
    
    void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        for (int i=0;i<accountList.size();i++) 
            ((Account)accountList.elementAt(i)).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, Account.storage, 0, true);
    }

}

