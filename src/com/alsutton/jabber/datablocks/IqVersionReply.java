/*
 * IqVersionReply.java
 *
 * Created on 27 Февраль 2005 г., 18:31
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
*
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
public class IqVersionReply extends Iq{
    
    /** Creates a new instance of IqVersionReply */
    public IqVersionReply(JabberDataBlock request) {
        super(request.getAttribute("from"), Iq.TYPE_RESULT, request.getAttribute("id") );
        JabberDataBlock query=addChild("query",null);
        query.setNameSpace("jabber:iq:version");
        query.addChild("name","Bombus");
        query.addChild("version",Version.getVersionLang());
        String os=Version.getOs();
                //+" Locale="
                //+System.getProperty("microedition.locale");
        query.addChild("os",os);
    }
    
    // constructs version request
    public IqVersionReply(String to) {
        super(to, Iq.TYPE_GET, "getver");
        addChild("query",null).setNameSpace("jabber:iq:version");
    }
    
    ///public static boolean 
    private final static String TOPFIELDS []={ "name",  "version",  "os"  }; 

  
    public static String dispatchVersion(JabberDataBlock data) {
        if (!data.isJabberNameSpace("jabber:iq:version")) return "unknown version namespace";
        StringBuffer vc=new StringBuffer();
        //vc.append((char)0x01);
        for (int i=0; i<TOPFIELDS.length; i++){
            String field=data.getChildBlockText(TOPFIELDS[i]);
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
