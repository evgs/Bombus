/*
 * Time.java
 *
 * Created on 20 Февраль 2005 г., 13:03
 */

package ui;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class Time {
    
    /** Creates a new instance of Time */
    public Time() {
    }

    public static String lz2(int i){
        if (i<10) return "0"+i; else return String.valueOf(i);
    }
    public static String timeString(Date date){
        Calendar c=Calendar.getInstance();
        //System.out.println(TimeZone.getDefault().getID());
        c.setTime(date);
        return lz2(c.get(c.HOUR_OF_DAY))+":"+lz2(c.get(c.MINUTE));
    }
    
    public static String dayString(Date date){
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        return lz2(c.get(c.DAY_OF_MONTH))+"."+
               lz2(c.get(c.MONTH))+"."+
               lz2(c.get(c.YEAR) % 100)+" ";
    }
}
