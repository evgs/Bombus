/*
 * StaticData.java
 *
 * Created on 20 Февраль 2005 г., 17:10
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import Messages.MessageParser;
import java.util.Hashtable;
import ui.ImageList;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
/**
 *
 * @author Eugene Stahov
 */
public class StaticData {
    
    private static StaticData sd;
    
    //public MessageParser parser;
    //public Vector smileTable;
    public ImageList rosterIcons;
    public ImageList smilesIcons;
    public Roster roster;
    public Vector statusList;
    public MIDlet midlet;
    
    public Account account;
    
    public int account_index;
    
    public boolean isMinimized;
    
    private Hashtable transports;
    
    /** Creates a new instance of StaticData */
    private StaticData() { }
    
    public void initFields(MIDlet m) {
        midlet=m;
        rosterIcons=new ImageList("/images/skin.png",13,12);
        smilesIcons=new ImageList("/images/smiles.png",15,15);
        //smileTable=new Vector(32);
        //parser=new MessageParser("/images/smiles.txt", smileTable);
        //config.LoadFromStorage();
        //account_index=Config.getInstance().accountIndex;
        //account=Account.createFromRMS(account_index);
        statusList=ExtendedStatus.createStatusList();
        
        transports=new Hashtable();
        transports.put("icq", new Integer(1));
        transports.put("yahoo", new Integer(2));
        transports.put("msn", new Integer(3));
        transports.put("aim", new Integer(4));
        transports.put("rss", new Integer(5));
        transports.put("conference", new Integer(7));
    }
    
    public int getTransportIndex(String name){
        Object o=transports.get(name);
        int index=(o==null)?0:((Integer)o).intValue();
        //if (resource) if (index==6) index=0;
        return index;
    }
    
    public static StaticData getInstance(){
        if (sd==null) sd=new StaticData();
        return sd;
    }
}
