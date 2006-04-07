/*
 * StaticData.java
 *
 * Created on 20 Февраль 2005 г., 17:10
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;
import java.util.Hashtable;
import ui.ImageList;
import java.util.Vector;
/**
 *
 * @author Eugene Stahov
 */
public class StaticData {
    
    private static StaticData sd;
    
    public Roster roster;
    
    public Account account;
    
    public int account_index;
    
    private Hashtable transports;
    
    /** Creates a new instance of StaticData */
    private StaticData() { }
    
    public void initFields() {
        
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
