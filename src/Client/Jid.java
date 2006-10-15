/*
 * Jid.java
 *
 * Created on 4 Март 2005 г., 1:25
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import util.strconv;

/**
 *
 * @author Eugene Stahov
 */
public class Jid {
    
    private String bareJid;
    private String resource;
    
    /** Creates a new instance of Jid */
    public Jid(String s) {
        setJid(s);
    }
    
    public void setJid(String s){
        int resourcePos=s.indexOf('/');
        if (resourcePos<0) resourcePos=s.length();
        resource=s.substring(resourcePos);
        bareJid=strconv.toLowerCase(s.substring(0,resourcePos));
    }
    /** Compares two Jids */
    public boolean equals(Jid j, boolean compareResource) {
        if (j==null) return false;
        
        if (!bareJid.equals(j.bareJid)) return false;

        if (!compareResource) return true;
        
        return (resource.equals(j.resource));
    }
    
    
    /** проверка jid на "транспорт" */
    public boolean isTransport(){
        return bareJid.indexOf('@')==-1;
    }
    /** проверка наличия ресурса */
    public boolean hasResource(){
        return (resource.length()!=0) ;
    }
    
    /** выделение транспорта */
    public String getTransport(){
        try {
            int beginIndex=bareJid.indexOf('@')+1;
            int endIndex=bareJid.indexOf('.',beginIndex);
            return bareJid.substring(beginIndex, endIndex);
        } catch (Exception e) {
            return "-";
        }
    }
    
    /** выделение ресурса со слэшем */
    public String getResource(){ return resource; }
    
    /** выделение username */
    /*public String getUser(){
        return substr(this,(char)0,'@');
    }*/
    
    /** выделение имени без ресурса */
    public String getBareJid(){ return bareJid; }
    
    /** выделение jid/resource */
    public String getJid(){
        if (resource.length()==0) return bareJid;
        return bareJid /** +'/' **/ +resource;
    }
    
    public static String toBareJid(String jid) { return new Jid(jid).getBareJid(); }
}
