/*
 * vCard.java
 *
 * Created on 24 Сентябрь 2005 г., 1:24
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package vcard;
import Client.StaticData;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.Iq;
import java.util.*;
import java.io.*;
import javax.microedition.lcdui.Image;
import util.StringLoader;
import util.strconv;

/**
 *
 * @author EvgS
 */
public class VCard {

    public final static int NICK_INDEX=1;
    
    public static Vector vCardFields;
    public static Vector vCardFields2;
    public static Vector vCardLabels;
    
    private Vector vCardData;
    private String jid;
    
    byte photo[];
    
    /** Creates a new instance of vCard */
    public VCard() {
        if (vCardFields==null) fieldsLoader();
    }
    
    public VCard(JabberDataBlock data) {
        this();
        jid=data.getAttribute("from");
        int itemsCount=getCount();
        vCardData=new Vector(itemsCount);
        vCardData.setSize(itemsCount);
        
        if (data==null) return; //"No vCard available";
        JabberDataBlock vcard=data.findNamespace("vcard-temp");
        if (vcard==null) return;
        
        for (int i=0; i<itemsCount; i++){
            try {
                String f1=(String)VCard.vCardFields.elementAt(i);
                String f2=(String)VCard.vCardFields2.elementAt(i);
                
                JabberDataBlock d2=
                        (f2==null) ? vcard : vcard.getChildBlock(f2);
                
                String field=d2.getChildBlockText(f1);
                
                if (field.length()>0) setVCardData(i, field);
            } catch (Exception e) {/**/}
        }
        
       JabberDataBlock photoXML=vcard.getChildBlock("PHOTO");
       if (photoXML==null) return;
       photoXML=photoXML.getChildBlock("BINVAL");
       photo=(byte[])photoXML.getChildBlocks().lastElement();
    }

    public JabberDataBlock constructVCard(){
        JabberDataBlock vcard=new Iq(null, Iq.TYPE_SET, null);
        JabberDataBlock child=vcard.addChild("vCard", null);
        child.setNameSpace("vcard-temp");
        
        int itemsCount=getCount();
        
        for (int i=0; i<itemsCount; i++){
            String field=getVCardData(i);
            if (field==null) continue;
            
            String f1=(String)VCard.vCardFields.elementAt(i);
            String f2=(String)VCard.vCardFields2.elementAt(i);
            
            JabberDataBlock subLevel=child;
            if (f2!=null) {
                subLevel=child.getChildBlock(f2);
                if (subLevel==null) subLevel=child.addChild(f2, null);
            }
            subLevel.addChild(f1, field);
            
        }
        if (photo!=null) {
            vcard.addChild("PHOTO", null).addChild("BINVAL", strconv.toBase64(photo));
        }
        //System.out.println(vcard.toString());
        return vcard;
    }
    
    public Image getPhoto() { 
//#if !(MIDP1)
       try {
           return Image.createImage(photo, 0, photo.length);
       } catch (Exception e) { }
//#endif
       return null; 
    }
    
    public String getNickName() { return getVCardData(NICK_INDEX);}
    
    public static JabberDataBlock getVCardReq(String to, String id ) 
    {
        JabberDataBlock req=new Iq(to, Iq.TYPE_GET, id);
        req.addChild("vCard", null).setNameSpace( "vcard-temp" );

        return req;
    }
    
    public static void request(String jid) {
        StaticData.getInstance().roster.setQuerySign(true); 
        StaticData.getInstance().roster.theStream.send(getVCardReq(jid, "getvc"));
    }
    
    private void fieldsLoader(){
	Vector table[]=new StringLoader().stringLoader("/vcard.txt", 3);

	vCardFields=table[1];
        vCardFields2=table[0];
        vCardLabels=table[2];
        
    }
    public String getVCardData(int index) {
        return (String) vCardData.elementAt(index);
    }

    public void setVCardData(int index, String data) {
        vCardData.setElementAt(data, index);
    }
    
    public int getCount(){ return vCardFields.size(); }

    public String getJid() { return jid; }

}
