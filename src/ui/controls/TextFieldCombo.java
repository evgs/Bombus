/*
 * TextFieldCombo.java
 *
 * Created on 9 Ноябрь 2006 г., 22:41
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui.controls;

import io.NvStorage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 *
 * @author Evg_S
 */
//#if (!MIDP1)
public class TextFieldCombo 
        extends TextField
        implements ItemCommandListener, CommandListener
{

    private Command cmdRecent;
    private Command cmdBack;
    private Command cmdSelect;
    
    private Display display;
    private Displayable parentView;
    private List list;
    private String label;
    
    private Vector recentList;

    private String id;

    /** Creates a new instance of TextFieldCombo */
    public TextFieldCombo(String label, String value, int maxlen, int constraints, String id, Display display) {
        super(label, "", maxlen, constraints);
        
        this.display=display;
        this.label=label;
        this.id="mru-"+id;
        
        loadRecentList();
        if (value==null) value="";
        if (value.length()==0) value=(String) recentList.elementAt(0);
        setString(value);
        
        cmdRecent=new Command("Recent", Command.ITEM, 1);

        addCommand(cmdRecent);
        setItemCommandListener(this);
    }

    public String getString() {
        String result=super.getString();
        int i=0;
        if (result.length()==0) return result;
        while (i<recentList.size()) {
            if ( result.equals((String)recentList.elementAt(i)) || i>9 ) recentList.removeElementAt(i);
            else i++;
        }
        recentList.insertElementAt(result, 0);
        saveRecentList();
        return result;
    }

    public void commandAction(Command command, Item item) {
        if (recentList.isEmpty()) return;
        parentView=display.getCurrent();
        
        cmdBack=new Command("Back", Command.BACK, 99);
        cmdSelect=new Command("Select", Command.OK, 1);
        
        list=new List(label, List.IMPLICIT);
        list.addCommand(cmdBack);
        list.setSelectCommand(cmdSelect);
        
        for (Enumeration e=recentList.elements(); e.hasMoreElements();)
            list.append((String)e.nextElement(), null);
        
        list.setCommandListener(this);
        display.setCurrent(list);
    }

    public void commandAction(Command command, Displayable displayable) {
        display.setCurrent(parentView);
        if (command==cmdSelect) {        
            setString( list.getString(list.getSelectedIndex()));
        }
    }

    private void saveRecentList() {
        DataOutputStream os=NvStorage.CreateDataOutputStream();
        try {
            for (Enumeration e=recentList.elements(); e.hasMoreElements(); ) {
                String s=(String)e.nextElement();
                os.writeUTF(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        NvStorage.writeFileRecord(os, id, 0, true);
        
    }
    private void loadRecentList() {
        recentList=new Vector(10);
        try {
            DataInputStream is=NvStorage.ReadFileRecord(id, 0);
            
            while (is.available()>0)
                recentList.addElement(is.readUTF());
            is.close();
        } catch (Exception e) { }
    }
}
//#else
//# public class TextFieldCombo 
//#         extends TextField
//# {
//# 
//#     /** Creates a new instance of TextFieldCombo */
//#     public TextFieldCombo(String label, String value, int maxlen, int constraints, String id, Display display) {
//#         super(label, value, maxlen, constraints);
//#     }
//# 
//# }
//#endif
