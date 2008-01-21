/*
 * Time.java
 *
 * Created on 20.02.2005, 13:03
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class Time {
    
    private static Calendar c=Calendar.getInstance( TimeZone.getTimeZone("GMT") );
    private static long utcToLocalOffsetMillis=0; 
    private static long fixupLocalOffsetMills=0;
    private static int tzo=0;
    /** Creates a new instance of Time */
    private Time() { }
    
    public static void setOffset(int tzOffset, int locOffset){
        utcToLocalOffsetMillis=((long)tzOffset)*60*60*1000;
        fixupLocalOffsetMills=((long)locOffset)*60*60*1000;
        tzo=tzOffset;
    }

    public static String lz2(int i){
        if (i<10) return "0"+i; else return String.valueOf(i);
    }
    public static String timeLocalString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE));
    }
    
    private static Calendar calDate(long date){
        c.setTime(new Date(date+utcToLocalOffsetMillis));
        return c;
    }
    
    public static String dayLocalString(long date){
        Calendar c=calDate(date);
        return lz2(c.get(Calendar.DAY_OF_MONTH))+'.'+
               lz2(c.get(Calendar.MONTH)+1)+'.'+
               lz2(c.get(Calendar.YEAR) % 100)+" ";
    }

    public static long utcTimeMillis(){
        return System.currentTimeMillis()+fixupLocalOffsetMills;
    }
    
    public static String Xep0082UtcTime(){
        long date=utcTimeMillis();
        c.setTime(new Date(date));
        return String.valueOf(c.get(Calendar.YEAR))+
                lz2(c.get(Calendar.MONTH)+1)+
                lz2(c.get(Calendar.DAY_OF_MONTH))+
                'T' + lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE))+':'+lz2(c.get(Calendar.SECOND));
    }

    public static String utcTime() {
        long date=utcTimeMillis();
        c.setTime(new Date(date));
        return String.valueOf(c.get(Calendar.YEAR)) +
                '-' + lz2(c.get(Calendar.MONTH)+1) + 
                '-' + lz2(c.get(Calendar.DAY_OF_MONTH)) +
                'T' + lz2(c.get(Calendar.HOUR_OF_DAY))+':'+lz2(c.get(Calendar.MINUTE))+':'+lz2(c.get(Calendar.SECOND)) +
                'Z';
    }
    
    public static String tzOffset(){
        StringBuffer tz=new StringBuffer();
        int tzi=tzo;
        char sign='+';
        if (tzo<0) { sign='-'; tzi=-tzo; }
        tz.append(sign);
        tz.append(lz2(tzi));
        tz.append(":00");
        return tz.toString();
    }
    
    public static String dispLocalTime(){
        long utcDate=utcTimeMillis();
        //Calendar c=calDate(date);
        return dayLocalString(utcDate)+timeLocalString(utcDate);
    }
    
    private final static int[] calFields=
    {Calendar.YEAR,         Calendar.MONTH,     Calendar.DATE, 
     Calendar.HOUR_OF_DAY,  Calendar.MINUTE,    Calendar.SECOND};
     
    private final static int[] ofsFieldsA=
    { 0, 4, 6, 9, 12, 15 } ; //XEP-0091 - DEPRECATED

    private final static int[] ofsFieldsB=
    { 0, 5, 8, 11, 14, 17 } ;//XEP-0203
    
    public static long dateIso8601(String sdate){
        int[] ofs=ofsFieldsA;
        if (sdate.endsWith("Z")) ofs=ofsFieldsB;
        
        try {
            int l=4;    // yearlen
            for (int i=0; i<calFields.length; i++){
                int begIndex=ofsFieldsA[i];
                int field=Integer.parseInt(sdate.substring(begIndex, begIndex+l));
                if (i==1) field+=Calendar.JANUARY-1;
                l=2;
                c.set(calFields[i], field);
            }
        } catch (Exception e) {    }
        return c.getTime().getTime(); 
    }
}

