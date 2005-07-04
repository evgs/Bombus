/*
 * StaticData.java
 *
 * Created on 20 Февраль 2005 г., 17:10
 */

package Client;
import Messages.MessageParser;
import ui.ImageList;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
/**
 *
 * @author Eugene Stahov
 */
public class StaticData {
    
    private static StaticData sd;
    
    public MessageParser parser;
    public Vector smileTable;
    public ImageList rosterIcons;
    public ImageList smilesIcons;
    public Roster roster;
    public Vector statusList;
    public MIDlet midlet;
    
    public Account account;
    
    public Config config;
    
    public int account_index;
    
    public boolean isMinimized;
    
    /** Creates a new instance of StaticData */
    private StaticData() {
        account_index=-1;
    }
    
    public void initFields(MIDlet m) {
        midlet=m;
        rosterIcons=new ImageList("/images/skin.png",13,12);
        smilesIcons=new ImageList("/images/smiles.png",15,15);
        smileTable=new Vector(32);
        parser=new MessageParser("/images/smiles.txt", smileTable);
        config=new Config();
        config.LoadFromStorage();
        account_index=config.accountIndex;
        //account=Account.createFromRMS(account_index);
        statusList=ExtendedStatus.createStatusList();
    }
    
    public static StaticData getInstance(){
        if (sd==null) sd=new StaticData();
        return sd;
    }
}
