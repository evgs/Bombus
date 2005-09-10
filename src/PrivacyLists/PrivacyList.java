/*
 * PrivacyList.java
 *
 * Created on 26 Август 2005 г., 23:08
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package PrivacyLists;
import java.util.*;
import com.alsutton.jabber.datablocks.Iq;
import ui.*;
import Client.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacyList extends IconTextElement{
    
    /** Creates a new instance of PrivacyList */
    boolean isActive;
    boolean isDefault;
    
    Vector rules=new Vector(); 
    
    public PrivacyList(String name) {
        super(StaticData.getInstance().rosterIcons);
        this.name=name;
    }
    
    public int getImageIndex() {return (isActive)?
        ImageList.ICON_PRIVACY_ACTIVE:
        ImageList.ICON_PRIVACY_PASSIVE; }
    public int getColor() {return 0; }
    
    private String name;
    public String toString() {
        StringBuffer result=new StringBuffer((name==null)? "<none>": name);
        result.append(' ');
        if (isDefault) result.append("(default)");
        return result.toString();
    }
    
    public void activate (String atr) {
        JabberDataBlock a=new JabberDataBlock(atr, null, null);
        a.setAttribute("name", name);
        privacyListRq(true, a);
    }
    
    public final static void privacyListRq(boolean out, JabberDataBlock child){
        JabberDataBlock pl=new JabberDataBlock("iq", null, null);
        pl.setTypeAttribute((out)?"set":"get");
        JabberDataBlock qry=new JabberDataBlock("query", null, null);
        qry.addChild(child);
        qry.setNameSpace("jabber:iq:privacy");
        pl.addChild(qry);
        
        //System.out.println(pl);
        StaticData.getInstance().roster.theStream.send(pl);
    }
}
