/*
 * Jid.java
 *
 * Created on 4 Март 2005 г., 1:25
 */

package Client;

/**
 *
 * @author Eugene Stahov
 */
public class Jid {
    
    private String fullJid;
    private int resourcePos;
    
    private static String substr(Jid j, char begin, char end){
        int beginIndex=j.fullJid.indexOf(begin)+1;
        int endIndex=j.fullJid.indexOf(end,beginIndex);
        return j.fullJid.substring(beginIndex, endIndex);
    }
    
    /** Creates a new instance of Jid */
    public Jid(String s) {
        setJid(s);
    }
    
    public void setJid(String s){
        fullJid=s;
        resourcePos=fullJid.indexOf('/');
        if (resourcePos<0) resourcePos=fullJid.length();
    }
    /** Compares two Jids */
    public boolean equals(Jid j, boolean compareResource) {
        if (j==null) return false;
        
        String cj=j.fullJid;
        // игнорируем регистр jid, 
        if (!fullJid.regionMatches(true,0,cj,0,resourcePos)) return false;
        if (!compareResource) return true;
        
        //учитываем регистр ресурсов и длину
        int compareLen=fullJid.length();
        if (compareLen!=j.fullJid.length()) return false;

        return fullJid.regionMatches(true,0,cj,0,compareLen);
        //int compareLen=(compareResource)?(j.getJidFull().length()):resourcePos;
        //return fullJid.regionMatches(true,0,j.fullJid,0,compareLen);
    }
    
    
    /** проверка jid на "транспорт" */
    public boolean isTransport(){
        return fullJid.indexOf('@')==-1;
    }
    
    /** выделение транспорта */
    public String getTransport(){
        return substr(this,'@','.');
    }
    
    /** выделение ресурса */
    public String getResource(){
        return fullJid.substring(resourcePos);
    }
    
    /** выделение username */
    public String getUser(){
        return substr(this,(char)0,'@');
    }
    
    /** выделение имени без ресурса */
    public String getJid(){
        return fullJid.substring(0,resourcePos);
    }
    
    /** выделение jid/resource */
    public String getJidFull(){
        return fullJid;
    }
}
