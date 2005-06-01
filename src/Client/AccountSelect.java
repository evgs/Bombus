/*
 * AccountPicker.java
 *
 * Created on 19 Март 2005 г., 23:26
 */

package Client;
import ui.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;


/**
 *
 * @author Eugene Stahov
 */
public class AccountSelect extends VirtualList implements CommandListener{

    Vector accountList;
    int activeAccount;
    
    Command cmdSelect=new Command("Select",Command.OK,1);
    Command cmdAdd=new Command("New Account",Command.SCREEN,2);
    Command cmdEdit=new Command("Edit",Command.ITEM,3);
    Command cmdDel=new Command("Delete",Command.ITEM,4);
    Command cmdCancel=new Command("Back",Command.BACK,99);
    
    /** Creates a new instance of AccountPicker */
    public AccountSelect(Display display) {
        super();
        setTitleImages(StaticData.getInstance().rosterIcons);
        //this.display=display;

        createTitle(1, "Accounts",null);
        
        accountList=new Vector();
        Account a;
        
        int index=0;
        activeAccount=StaticData.getInstance().config.accountIndex;
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
                a.updateJidCache();
                accountList.addElement(a);
                rmsUpdate();
            }
        }
        attachDisplay(display);
        addCommand(cmdAdd);
        
        commandState();
        setCommandListener(this);
    }
    
    private void commandState(){
        if (accountList.isEmpty()) {
            removeCommand(cmdEdit);
            removeCommand(cmdDel);
            removeCommand(cmdSelect);
            removeCommand(cmdCancel);
        } else {
            addCommand(cmdEdit);
            addCommand(cmdDel);
            addCommand(cmdSelect);
            if (activeAccount>=0)
                addCommand(cmdCancel);  // нельзя выйти без активного аккаунта
        }
    }

    public VirtualElement getItemRef(int Index) { return (VirtualElement)accountList.elementAt(Index); }
    protected int getItemCount() { return accountList.size();  }

    public void commandAction(Command c, Displayable d){
        if (c==cmdCancel) {
            destroyView();
            //Account.launchAccount();
            //StaticData.getInstance().account_index=0;
        }
        if (c==cmdSelect) eventOk();
        if (c==cmdEdit) new AccountForm(display,(Account)getSelectedObject(),false);
        if (c==cmdAdd) {
            Account a=new Account();
            accountList.addElement(a);
            new AccountForm(display,a,true);
        }
        if (c==cmdDel) {
            accountList.removeElement(getSelectedObject());
            rmsUpdate();
            moveCursorHome();
            commandState();
            redraw();
        }
        
    }
    public void eventOk(){
        destroyView();
        StaticData sd=StaticData.getInstance();
        sd.config.accountIndex=cursor;
        sd.config.saveToStorage();
        sd.account_index=cursor;
        Account.launchAccount();
    }

    private void rmsUpdate(){
        DataOutputStream outputStream=NvStorage.CreateDataOutputStream();
        for (int i=0;i<accountList.size();i++) 
            ((Account)accountList.elementAt(i)).saveToDataOutputStream(outputStream);
        NvStorage.writeFileRecord(outputStream, Account.storage, 0, true);
    }
    
    
    class AccountForm implements CommandListener, ItemStateListener{
        private Display display;
        private Displayable parentView;
        Form f;
        TextField userbox;
        TextField passbox;
        TextField servbox;
        TextField ipbox;
        TextField portbox;
        TextField resourcebox;
        TextField nickbox;
        
        ChoiceGroup register;
        
        
        Command cmdOk=new Command("OK",Command.OK,1);
        Command cmdCancel=new Command("Back",Command.BACK,99);
        
        Account account;
        boolean newaccount;
        
        public AccountForm(Display display, Account account, boolean newAccount) {
            this.display=display;
            parentView=display.getCurrent();
            
            this.account=account;
            newaccount=newAccount;
            
            String title=(newaccount)?
                "New Account":
                (account.toString());
            
            f=new Form(title);
            userbox=new TextField("Username",account.getUserName(),32,TextField.URL);  f.append(userbox);
            passbox=new TextField("Password",account.getPassword(),32,
                    TextField.URL|TextField.PASSWORD);  f.append(passbox); passStars();
                    
            servbox=new TextField("Server",account.getServerN(),32,TextField.URL);    f.append(servbox);
            ipbox=new TextField("Server IP",account.getServerI(),32,TextField.URL);   f.append(ipbox);
            portbox=new TextField("Port",String.valueOf(account.getPort()),32,TextField.NUMERIC);   f.append(portbox);
            register=new ChoiceGroup(null, Choice.MULTIPLE);
            register.append("Register Account",null);
            //TODO: if (newaccount) 
            f.append(register);
                
            resourcebox=new TextField("Resource",account.getResource(),32,TextField.ANY);  f.append(resourcebox);
            nickbox=new TextField("Account name",account.getNickName(),32,TextField.ANY);  f.append(nickbox);
                        
            f.addCommand(cmdOk);
            f.addCommand(cmdCancel);
            
            f.setCommandListener(this);
            f.setItemStateListener(this);
            
            display.setCurrent(f);
        }

        private void passStars(){
            if (passbox.size()==0) passbox.setConstraints(TextField.URL);
        }
        
        public void itemStateChanged(Item item) {
            
            if (item==userbox) {
                // test for userbox has user@server
                String user=userbox.getString();
                int at=user.indexOf('@');
                if (at==-1) return;
                //userbox.setString(user.substring(0,at));
                servbox.setString(user.substring(at+1));
            }
            if (item==passbox) passStars();
        }
        public void commandAction(Command c, Displayable d){
            if (c==cmdCancel) {
                if (newaccount) accountList.removeElement(accountList.lastElement());
                destroyView(); 
                return; 
            }
            if (c==cmdOk)   {
                String user=userbox.getString();
                int at=user.indexOf('@');
                if (at!=-1) user=user.substring(0, at);
                account.setUserName(user);
                account.setPassword(passbox.getString());
                account.setServer(servbox.getString());
                account.setIP(ipbox.getString());
                account.setResource(resourcebox.getString());
                account.setNickName(nickbox.getString());
                account.updateJidCache();
                
                try {
                    account.setPort(Integer.parseInt(portbox.getString()));
                } catch (Exception e) {
                    account.setPort(5222);
                }
                boolean b[]=new boolean[1];
                register.getSelectedFlags(b);
                
                rmsUpdate();
                commandState();
                if (b[0]) new AccountRegister(account,display, parentView); 
                else destroyView();
                    
            }
        }
        
        public void destroyView(){
            if (display!=null)   display.setCurrent(parentView);
        }
        
    }

}

