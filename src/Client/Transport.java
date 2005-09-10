/*
 * Transport.java
 *
 * Created on 26 Февраль 2005 г., 21:49
 */

package Client;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class Transport {
    
    private Hashtable t;
    private static Transport instance;
    // Singleton
    public static Transport getInstance(){
        if (instance==null) instance=new Transport();
        return instance;
    }
    /** Creates a new instance of Transport */
    private Transport() {
        t=new Hashtable(5);
        t.put("icq", new Integer(1));
        t.put("yahoo", new Integer(2));
        t.put("msn", new Integer(3));
        t.put("aim", new Integer(4));
        t.put("rss", new Integer(5));
        t.put("conference", new Integer(7));
    }
    
    public int getTransportIndex(String name, boolean resource){
        Object o=t.get(name);
        int index=(o==null)?0:((Integer)o).intValue();
        if (resource) if (index==6) index=0;
        return index;
    }
}
