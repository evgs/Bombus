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
    
    /** Creates a new instance of StaticData */
    private StaticData() { }
    
    public static StaticData getInstance(){
        if (sd==null) sd=new StaticData();
        return sd;
    }

}
