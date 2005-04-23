/*
 * IqVersionReply.java
 *
 * Created on 27 Февраль 2005 г., 18:31
 */

package com.alsutton.jabber.datablocks;
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
        setAttribute("type","result");
        setAttribute("id",request.getAttribute("id"));
        setAttribute("to",request.getAttribute("from"));
        JabberDataBlock query=new JabberDataBlock("query",this,null);
        addChild(query);
        query.setNameSpace("jabber:iq:version");
        query.addChild(new JabberDataBlock(query, "name","Bombus"));
        query.addChild(new JabberDataBlock(query, "version",Version.version));
/*#DefaultConfiguration,Release#*///<editor-fold>
        query.addChild(new JabberDataBlock(query, "os","J2ME MIDP2"));
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--        query.addChild(new JabberDataBlock(query, "os","J2ME M55"));
/*$M55,M55_Release$*///</editor-fold>
    }
    
    public String getTagName() {
        return "iq";
    }

    ///public static boolean 
    
}
