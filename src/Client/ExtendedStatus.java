/*
 * ExtendedStatus.java
 *
 * Created on 27 Февраль 2005 г., 17:04
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package Client;

import images.RosterIcons;
import java.util.Enumeration;
import java.io.*;
import java.util.Vector;
import ui.IconTextElement;
import ui.ImageList;
import com.alsutton.jabber.datablocks.Presence;
/**
 *
 * @author Eugene Stahov
 */
public class ExtendedStatus extends IconTextElement{
    
    private String name;    // status name
    private String status="";
    private int priority;
    int index;
    
    /** Creates a new instance of ExtendedStatus */
    public ExtendedStatus(int index, String name) {
        super(RosterIcons.getInstance());
        this.index=index;
        this.name=name;
    }
    
    //public void onSelect(){}
    public String toString(){ 
        StringBuffer s=new StringBuffer(name);
        s.append(" (");
        s.append(priority);
        s.append(") ");
        if (status.length()>0) {
            s.append('"');
            s.append(status);
            s.append('"');
        }
        
        //return name+" ("+priority+") \""+status+"\""; 
        return s.toString();
    }
    public int getColor(){ return 0;}
    public int getImageIndex(){ return index;}

    public String getName() { return name; }
    public String getMessage() { return status; }
    public void setMessage(String s) { status=s; }

    public int getPriority() { return priority; }
    public void setPriority(int p) { priority=p; }

}
