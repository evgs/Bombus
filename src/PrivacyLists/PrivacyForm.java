/*
 * PrivacyForm.java
 *
 * Created on 11 �������� 2005 �., 2:32
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package PrivacyLists;
import Client.*;
import javax.microedition.lcdui.*;
import java.util.*;
import locale.SR;
import ui.ConstMIDP;

/**
 *
 * @author EvgS
 */
public class PrivacyForm
        implements
        CommandListener,
        ItemStateListener {
    
    private Display display;
    private Displayable parentView;
    private PrivacyItem item;
    
    private PrivacyList targetList;
    
    Form form=new Form(SR.MS_PRIVACY_RULE);
    ChoiceGroup choiceAction=new ChoiceGroup(SR.MS_PRIVACY_ACTION, ConstMIDP.CHOICE_POPUP, PrivacyItem.actions, null);
    ChoiceGroup choiseType=new ChoiceGroup(SR.MS_PRIVACY_TYPE, ConstMIDP.CHOICE_POPUP, PrivacyItem.types, null);
    ChoiceGroup choiseStanzas=new ChoiceGroup(SR.MS_STANZAS, ChoiceGroup.MULTIPLE, PrivacyItem.stanzas, null);
    TextField textValue;
    //TextField textOrder;
    ChoiceGroup choiceSubscr=new ChoiceGroup(SR.MS_SUBSCRIPTION, ConstMIDP.CHOICE_POPUP, PrivacyItem.subscrs, null);
    
    Command cmdCancel=new Command(SR.MS_CANCEL, Command.BACK, 99);
    Command cmdOk=new Command(SR.MS_OK, Command.OK, 1);
    /** Creates a new instance of PrivacyForm */
    public PrivacyForm(Display display, PrivacyItem item, PrivacyList plist) {
        this.display=display;
        parentView=display.getCurrent();
        this.item=item;
        targetList=plist;
        
        textValue=new TextField(null, item.value, 64, TextField.URL);
        
        form.append(choiceAction);
        choiceAction.setSelectedIndex(item.action, true);
        
        form.append(choiseType);
        
        form.append(textValue);
        choiseType.setSelectedIndex(item.type, true);
        switchType();
        
        //textOrder=new TextField("Order", String.valueOf(item.order), 64, TextField.NUMERIC);
        //form.append(textOrder);
        
        form.append(choiseStanzas);
        choiseStanzas.setSelectedFlags(item.stanzasSet);
        
        form.setItemStateListener(this);
        form.setCommandListener(this);
        form.addCommand(cmdOk);
        form.addCommand(cmdCancel);
        display.setCurrent(form);
        
    }
    
    private void switchType() {
        int index=choiseType.getSelectedIndex();
        try {
            Object rfocus=StaticData.getInstance().roster.getFocusedObject();
            switch (index) {
                case 0: //jid
                    if (targetList!=null) if (rfocus instanceof Contact) {
                        textValue.setString(((Contact)rfocus).getBareJid());
                    }
                    form.set(2, textValue);
                    break;
                case 1: //group
                    if (targetList!=null) textValue.setString( ( (rfocus instanceof Group)?
                        (Group)rfocus : 
                        StaticData.getInstance().roster.
                            groups.getGroup(((Contact)rfocus).group)
                        ).getName());

                    form.set(2, textValue);
                    break;
                case 2: //subscription
                    form.set(2, choiceSubscr);
                    break;
                    
                case 3:
                    form.set(2, new StringItem(null,"(ANY)"));
            }
            /*if (index==2) {
                form.set(2, choiceSubscr);
            } else {
                textValue.setLabel(PrivacyItem.types[index]);
                form.set(2, textValue);
            }
             */
        } catch (Exception e) {/* ��� ����� �� ������ ���� */ }
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) { destroyView(); return; }
        if (c==cmdOk) {
            try {
                int type=choiseType.getSelectedIndex();
                String value=textValue.getString();
                if (type==2) value=PrivacyItem.subscrs[choiceSubscr.getSelectedIndex()];
                if (type!=PrivacyItem.ITEM_ANY) 
                if (value.length()==0) return;
                //int order=Integer.parseInt(textOrder.getString());
                
                item.action=choiceAction.getSelectedIndex();
                item.type=type;
                item.value=value;
                //item.order=order;
                choiseStanzas.getSelectedFlags(item.stanzasSet);
                
                if (targetList!=null) 
                    if (!targetList.rules.contains(item)) {
                        targetList.addRule(item);
                        item.order=targetList.rules.indexOf(item)*10;
                    }
                destroyView();
            } catch (Exception e) {e.printStackTrace();}
        }
    }
    
    public void itemStateChanged(Item item){
        if (item==choiseType) switchType();
    }
    
    public void destroyView(){
        if (display!=null)   display.setCurrent(parentView);
    }
}
