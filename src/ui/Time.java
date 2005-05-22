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
    
    private static Calendar c=Calendar.getInstance( TimeZone.getTimeZone("GMT") );
    private static long offset=0; 
    /** Creates a new instance of Time */
    private Time() { }
    
    public static void setOffset(int gmtOffset){
        offset=60*60*10*gmtOffset;
    }

    public static String lz2(int i){
        if (i<10) return "0"+i; else return String.valueOf(i);
    }
    public static String timeString(Date date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.HOUR_OF_DAY))+":"+lz2(c.get(Calendar.MINUTE));
    }
    
    private static Calendar calDate(Date date){
        c.setTime(new Date(date.getTime()+offset));
        return c;
    }
    
    public static String dayString(Date date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.DAY_OF_MONTH))+"."+
               lz2(c.get(Calendar.MONTH))+"."+
               lz2(c.get(Calendar.YEAR) % 100)+" ";
    }

    
    private final static int[] calFields=
    {Calendar.YEAR,         Calendar.MONTH,     Calendar.DATE, 
     Calendar.HOUR_OF_DAY,  Calendar.MINUTE,    Calendar.SECOND};
     
    private final static int[] ofsFieldsA=
    { 0, 4, 6, 9, 12, 15 } ;

    //private final static int[] ofsFieldsB=
    //{ 0, 4, 6, 9, 12, 15 } ;
    
    public static Date dateIso8601(String sdate){
        try {
            int l=4;    // yearlen
            for (int i=0; i<calFields.length; i++){
                int begIndex=ofsFieldsA[i];
                int field=Integer.parseInt(sdate.substring(begIndex, begIndex+l));
                if (i==1) field--;
                l=2;
                c.set(calFields[i], field);
            }
        } catch (Exception e) {    }
        return c.getTime();
    }
}

