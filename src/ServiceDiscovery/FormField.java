/*
 * FormField.java
 *
 * Created on 5 Июнь 2005 г., 20:30
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;
import javax.microedition.lcdui.*;
import com.alsutton.jabber.*;
import java.util.*;
import ui.ConstMIDP;
/**
 *
 * @author Evg_S
 */
public class FormField {
    
    public String label;
    public String type;
    public String name;
    public Item formItem;
    boolean hidden;
    //TODO: boolean required;
    public boolean instructions;
    private Vector optionsList;
    private boolean numericBoolean;
    private boolean registered;
    /** Creates a new instance of FormField */
    public FormField(JabberDataBlock field) {
        name=field.getTagName();
        label=name;
        String body=field.getText();
        if (name.equals("field")) {
            // x:data
            type=field.getAttribute("type");
            name=field.getAttribute("var");
            label=field.getAttribute("label");
            if (label==null) label=name;
            body=field.getChildBlockText("value");
	    hidden= type.equals("hidden"); 
            if (type.equals("fixed")) formItem=new StringItem(label, body); 
            else if (type.equals("boolean")) {
                ChoiceGroup ch=new ChoiceGroup(null,ChoiceGroup.MULTIPLE);
                formItem=ch;
                ch.append(label, null);
                boolean set=false;
                if (body.equals("1")) set=true;
                if (body.equals("true")) set=true;
                numericBoolean=body.length()==1;
                ch.setSelectedIndex(0, set);
            }
            else if (type.equals("list-single") || type.equals("list-multi")) {
                
                int choiceType=(type.equals("list-single"))? 
                    ConstMIDP.CHOICE_POPUP : ChoiceGroup.MULTIPLE;
                ChoiceGroup ch=new ChoiceGroup(label, choiceType);
                formItem=ch;
                
                optionsList=new Vector();
                for (Enumeration e=field.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock option=(JabberDataBlock)e.nextElement();
                    if (option.getTagName().equals("option")) {
                        String value=option.getChildBlockText("value");
                        String label=option.getAttribute("label");
                        if (label==null) label=value;
                        optionsList.addElement(value);
                        int index=ch.append(label, null);
                        if (body.equals(value)) ch.setSelectedIndex(index, true);
                    }
                }
            }
	    // text-single, text-private
            else {
                if (body.length()>=200) {
                    body=body.substring(0,198);
                }
                int constrains=(type.equals("text-private"))? TextField.PASSWORD: TextField.ANY;
                formItem=new TextField(label, body, 200, constrains);
            }
        } else {
            // not x-data
            if ( instructions=name.equals("instructions") )
                formItem=new StringItem("Instructions", body);
            else if ( name.equals("title") )
                formItem=new StringItem(null, body);
            else if ( name.equals("registered") ) {
                ChoiceGroup cg=new ChoiceGroup("Registration", ChoiceGroup.MULTIPLE);
                cg.append("Remove registration", null);
                formItem=cg;
                registered=true;
            }
            else
                formItem=new TextField(label, body, 64, 0);
        }
        
        if (name!=null)
        if ( name.equals("key") ) hidden=true; 
    }
    JabberDataBlock constructJabberDataBlock(){
        JabberDataBlock j=null;
        if (formItem instanceof TextField) {
            String value=((TextField)formItem).getString();
            if (type==null) {
                j=new JabberDataBlock(null, name, value);
            } else {
                // x:data
                j=new JabberDataBlock("field", null, null);
                j.setAttribute("var", name);
                j.setAttribute("type", type);
                j.addChild("value", value);
            }
        }
        if (formItem instanceof ChoiceGroup) {
            if (registered) {
                boolean unregister=((ChoiceGroup)formItem).isSelected(0);
                if (unregister) return new JabberDataBlock("remove", null, null);
                return null;
            }
                
            //only x:data
                j=new JabberDataBlock("field", null, null);
                j.setAttribute("var", name);
                j.setAttribute("type", type);
                if (optionsList==null) {
                    boolean set=((ChoiceGroup)formItem).isSelected(0);
                    String result=String.valueOf(set);
                    if (numericBoolean) result=set?"1":"0";
                    j.addChild("value", result);
                } else 
                if (type.equals("list-multi")) {
                    ChoiceGroup ch=(ChoiceGroup) formItem;
                    int count=ch.size();
                    for (int i=0; i<count; i++) {
                        if (ch.isSelected(i))  
                            j.addChild("value", (String)optionsList.elementAt(i));                    
                    }
                } else /* list-single */ {
                    int index=((ChoiceGroup) formItem).getSelectedIndex();
                    if (index>=0)  j.addChild("value", (String)optionsList.elementAt(index));
                }
        }
        return j;
    }
}
