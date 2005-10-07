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
        if (resourcePos!=j.resourcePos) return false;
        if (!fullJid.regionMatches(true,0,cj,0,resourcePos)) return false;
        if (!compareResource) return true;
        
        //учитываем регистр ресурсов и длину
        int compareLen=fullJid.length();
        if (compareLen!=j.fullJid.length()) return false;

        // сравнение только ресурсов
        compareLen-=resourcePos;
        return fullJid.regionMatches(false,resourcePos,cj,resourcePos,compareLen);
        //int compareLen=(compareResource)?(j.getJidFull().length()):resourcePos;
        //return fullJid.regionMatches(true,0,j.fullJid,0,compareLen);
    }
    
    
    /** проверка jid на "транспорт" */
    public boolean isTransport(){
        return fullJid.indexOf('@')==-1;
    }
    /** проверка наличия ресурса */
    public boolean hasResource(){
        return fullJid.length()!=resourcePos;
    }
    
    /** выделение транспорта */
    public String getTransport(){
        try {
            int beginIndex=fullJid.indexOf('@')+1;
            int endIndex=fullJid.indexOf('.',beginIndex);
            return fullJid.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }
    
    /** выделение ресурса со слэшем */
    public String getResource(){
        return fullJid.substring(resourcePos);
    }
    
    /** выделение username */
    /*public String getUser(){
        return substr(this,(char)0,'@');
    }*/
    
    /** выделение имени без ресурса */
    public String getJid(){
        return fullJid.substring(0,resourcePos);
    }
    
    /** выделение jid/resource */
    public String getJidFull(){
        return fullJid;
    }
}
