package Client;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;
import ui.ConstMIDP;
import ui.controls.NumberField;

class AccountForm implements CommandListener, ItemStateListener {
    
    private final AccountSelect accountSelect;
    
    private Display display;
    private Displayable parentView;
    
    private Form f;
    private TextField userbox;
    private TextField passbox;
    private TextField servbox;
    private TextField ipbox;
    private NumberField portbox;
    private TextField resourcebox;
    private TextField nickbox;
    private ChoiceGroup register;
    
    Command cmdOk = new Command("OK", Command.OK, 1);
    Command cmdCancel = new Command("Back", Command.BACK, 99);
    
    Account account;
    
    boolean newaccount;
    
    public AccountForm(AccountSelect accountSelect, Display display, Account account) {
	this.accountSelect = accountSelect;
	this.display=display;
	parentView=display.getCurrent();
	
	newaccount= account==null;
	if (newaccount) account=new Account();
	this.account=account;
	
	String title = (newaccount)?
	    "New Account":
	    (account.toString());
	f = new Form(title);
	userbox = new TextField("Username", account.getUserName(), 32, TextField.URL);				f.append(userbox);
	passbox = new TextField("Password", account.getPassword(), 32, TextField.URL | TextField.PASSWORD);	f.append(passbox);		passStars();
	servbox = new TextField("Server", account.getServer(), 32, TextField.URL);				f.append(servbox);
	ipbox = new TextField("Server Addr/IP", account.getHostAddr(), 32, TextField.URL);			f.append(ipbox);
	portbox = new NumberField("Port", account.getPort(), 0, 65535);						f.append(portbox);
	register = new ChoiceGroup(null, Choice.MULTIPLE);
	register.append("use SSL",null);
	register.append("plain-text password",null);
	register.append("Register Account",null);
	boolean b[] = {account.getUseSSL(), account.getPlainAuth(), false};
	register.setSelectedFlags(b);
	f.append(register);
	resourcebox = new TextField("Resource", account.getResource(), 32, TextField.ANY);			f.append(resourcebox);
	nickbox = new TextField("Account name", account.getNickName(), 32, TextField.ANY);			f.append(nickbox);
	
	f.addCommand(cmdOk);
	f.addCommand(cmdCancel);
	
	f.setCommandListener(this);
	f.setItemStateListener(this);
	
	display.setCurrent(f);
    }
    
    private void passStars() {
	if (passbox.size()==0)
	    passbox.setConstraints(TextField.URL | ConstMIDP.TEXTFIELD_SENSITIVE);
    }
    
    public void itemStateChanged(Item item) {
	if (item==userbox) {
	    String user = userbox.getString();
	    int at = user.indexOf('@');
	    if (at==-1) return;
	    //userbox.setString(user.substring(0,at));
	    servbox.setString(user.substring(at+1));
	}
	if (item==passbox) passStars();
    }
    
    public void commandAction(Command c, Displayable d) {
	if (c==cmdCancel) {
	    destroyView();
	    return;
	}
	if (c==cmdOk) {
	    boolean b[] = new boolean[3];
	    register.getSelectedFlags(b);
	    String user = userbox.getString();
	    int at = user.indexOf('@');
	    if (at!=-1) user=user.substring(0, at);
	    account.setUserName(user.trim());
	    account.setPassword(passbox.getString());
	    account.setServer(servbox.getString().trim());
	    account.setHostAddr(ipbox.getString());
	    account.setResource(resourcebox.getString());
	    account.setNickName(nickbox.getString());
	    account.setUseSSL(b[0]);
	    account.setPlainAuth(b[1]);
	    //account.updateJidCache();
	    
	    account.setPort(portbox.getValue());
	    
	    if (newaccount) accountSelect.accountList.addElement(account);
	    accountSelect.rmsUpdate();
	    accountSelect.commandState();
	    
	    if (b[2])
		new AccountRegister(account, display, parentView); 
	    else destroyView();
	}
    }
    
    public void destroyView()	{
	if (display!=null)   display.setCurrent(parentView);
    }
}
