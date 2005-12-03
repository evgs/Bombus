/*
 * PrivacyList.java
 *
 * Created on 26 Август 2005 г., 23:08
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package PrivacyLists;
import com.alsutton.jabber.datablocks.Iq;
import java.util.*;
import com.alsutton.jabber.*;
import ui.*;
import Client.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacyList extends IconTextElement{
    
    String name;
    boolean isActive;
    boolean isDefault;
    
    Vector rules=new Vector(); 
    
    /** Creates a new instance of PrivacyList */
    public PrivacyList(String name) {
        super(StaticData.getInstance().getRosterIcons());
        this.name=name;
    }
    
    public int getImageIndex() {return (isActive)?
        ImageList.ICON_PRIVACY_ACTIVE:
        ImageList.ICON_PRIVACY_PASSIVE; }
    public int getColor() {return 0; }
    
    public String toString() {
        StringBuffer result=new StringBuffer((name==null)? "<none>": name);
        result.append(' ');
        if (isDefault) result.append("(default)");
        return result.toString();
    }
    
    
    public void generateList(){
        JabberDataBlock list = listBlock();
        for (Enumeration e=rules.elements(); e.hasMoreElements(); ) {
            JabberDataBlock item=((PrivacyItem)e.nextElement()).constructBlock();
            list.addChild(item);
        }
        PrivacyList.privacyListRq(true, list, "storelst");
    }

    private JabberDataBlock listBlock() {
        JabberDataBlock list=new JabberDataBlock("list", null, null);
        list.setAttribute("name", name);
        return list;
    }
    
    public void deleteList(){
        JabberDataBlock list=listBlock();
        PrivacyList.privacyListRq(true, list, "storelst");
    }
  
    public void activate (String atr) {
        JabberDataBlock a=new JabberDataBlock(atr, null, null);
        a.setAttribute("name", name);
        privacyListRq(true, a, "plset");
    }
    
    public void addRule(PrivacyItem rule) {
        int index=0;
        while (index<rules.size()) {
            if ( rule.order <= ((PrivacyItem)rules.elementAt(index)).order ) break;
            index++;
        }
        rules.insertElementAt(rule, index);
    }

    
    public final static void privacyListRq(boolean set, JabberDataBlock child, String id){
        JabberDataBlock pl=new Iq();
        pl.setTypeAttribute((set)?"set":"get");
        pl.setAttribute("id", id);
        JabberDataBlock qry=pl.addChild("query", null);
        qry.setNameSpace("jabber:iq:privacy");
        if (child!=null) qry.addChild(child);
        
        //System.out.println(pl);
        StaticData.getInstance().roster.theStream.send(pl);
    }
}
