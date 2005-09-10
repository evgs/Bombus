/*
 * IqVersionReply.java
 *
 * Created on 27 Февраль 2005 г., 18:31
 */

package com.alsutton.jabber.datablocks;

import Info.Version;
import com.alsutton.jabber.*;
import java.util.*;
import Client.*;


/**
 *
 * @author Eugene Stahov
 */
public class IqVersionReply extends JabberDataBlock{
    
    /** Creates a new instance of IqVersionReply */
    public IqVersionReply(JabberDataBlock request) {
        super();
        setTypeAttribute("result");
        setAttribute("id",request.getAttribute("id"));
        setAttribute("to",request.getAttribute("from"));
        JabberDataBlock query=new JabberDataBlock("query",this,null);
        addChild(query);
        query.setNameSpace("jabber:iq:version");
        query.addChild("name","Bombus");
        query.addChild("version",Version.version);
        String os=Version.getOs();
                //+" Locale="
                //+System.getProperty("microedition.locale");
        query.addChild("os",os);
    }
    
    // constructs version request
    public IqVersionReply(String to) {
        super();
        setTypeAttribute("get");
        setAttribute("to",to);
        setAttribute("id","getver");
        JabberDataBlock query=new JabberDataBlock("query",this,null);
        addChild(query);
        query.setNameSpace("jabber:iq:version");
    }
    
    public String getTagName() {
        return "iq";
    }

    ///public static boolean 
    private final static String TOPFIELDS []={ "name",  "version",  "os"  }; 

  
    public static String dispatchVersion(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:version")) return "unknown version namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<TOPFIELDS.length; i++){
            String field=data.getTextForChildBlock(TOPFIELDS[i].toLowerCase());
            if (field.length()>0) {
                vc.append(TOPFIELDS[i]);
                vc.append((char)0xa0);
                vc.append(field);
                vc.append((char)'\n');
            }
        }
        return vc.toString();
    }
}
