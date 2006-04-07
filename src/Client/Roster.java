/*
 * Roster.java
 *
 * Created on 6 ������ 2005 �., 19:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

//TODO: ��������� ��������� ���������� ��� theStream.send

package Client;

import Conference.ConferenceGroup;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import archive.ArchiveList;
import images.RosterIcons;
import locale.SR;
import midlet.Bombus;
import vcard.VCard;
import vcard.vCardForm;
import com.alsutton.jabber.*;
import com.alsutton.jabber.datablocks.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
//import javax.microedition.media.*;
//import Client.Contact.*;
import ui.*;
import ServiceDiscovery.ServiceDiscovery;
import Conference.ConferenceForm;
import PrivacyLists.PrivacySelect;

//import Client.msg.*;

/**
 *
 * @author Eugene Stahov
 */
//public class Roster implements JabberListener, VList.Callback{
public class Roster
        extends VirtualList
        implements
        JabberListener,
        CommandListener,
        Runnable
        //ContactEdit.StoreContact
        //Thread
{
    
    
    private Jid myJid;
    
    /**
     * The stream representing the connection to ther server
     */
    public JabberStream theStream ;
        
    int messageCount;
    
    public Object messageIcon;
   
    boolean reconnect=false;
    boolean querysign=false;
    
    public int myStatus=Presence.PRESENCE_OFFLINE;
    
    private Vector hContacts;
    private Vector vContacts;
    
    private Vector paintVContacts;  // ��� ������� ��������.
    
    public Groups groups;
    
    public Vector bookmarks;

    
    private Command cmdActions=new Command(SR.MS_ITEM_ACTIONS, Command.SCREEN, 1);
    private Command cmdStatus=new Command(SR.MS_STATUS_MENU, Command.SCREEN, 2);
    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 4);
    private Command cmdAlert=new Command(SR.MS_ALERT_PROFILE_CMD, Command.SCREEN, 8);
    private Command cmdGroupChat=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10);
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 10);
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 11);    
    private Command cmdAccount=new Command(SR.MS_ACCOUNT_, Command.SCREEN, 12);
    private Command cmdOptions=new Command(SR.MS_OPTIONS, Command.SCREEN, 20);
    private Command cmdInfo=new Command(SR.MS_ABOUT, Command.SCREEN, 80);
    private Command cmdMinimize=new Command(SR.MS_APP_MINIMIZE, Command.SCREEN, 90);
    private Command cmdQuit=new Command(SR.MS_APP_QUIT, Command.SCREEN, 99);
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();
    
    //public JabberBlockListener discoveryListener;
    
    /**
     * Creates a new instance of Roster
     * Sets up the stream to the server and adds this class as a listener
     */
    public Roster(Display display /*, boolean selAccount*/) {
        super();

	setProgress(24);
        //setTitleImages(StaticData.getInstance().rosterIcons);
                
        this.display=display;
        
        cf=Config.getInstance();
        
        //msgNotify=new EventNotify(display, Profile.getProfile(0) );
        Title title=new Title(4, null, null);
        setTitleItem(title);
        title.addRAlign();
        title.addElement(null);
        title.addElement(null);
        //displayStatus();
        
        //l.setTitleImgL(6); //connect
        hContacts=new Vector();
        groups=new Groups();
        
        vContacts=new Vector(); // just for displaying
        
        addCommand(cmdStatus);
        addCommand(cmdActions);
        addCommand(cmdAlert);
        addCommand(cmdAdd);
        //addCommand(cmdServiceDiscovery);
        addCommand(cmdGroupChat);
        //addCommand(cmdPrivacy);
        addCommand(cmdTools);
        addCommand(cmdArchive);
        addCommand(cmdInfo);
        addCommand(cmdAccount);

        addCommand(cmdOptions);
        addCommand(cmdQuit);
        
        
        addOptionCommands();
        //moveCursorTo(0);
        setCommandListener(this);
        //resetStrCache();
        
        //if (visible) display.setCurrent(this);
        /*if (selAccount) {
            new AccountSelect(display);
        } else {
            // connect whithout account select
            Account.launchAccount();
        }*/
	//setRosterTitle("offline");
	updateTitle();
	
        SplashScreen.getInstance().setExit(display, this);
    }
    
    void addOptionCommands(){
        if (cf.allowMinimize) addCommand(cmdMinimize);
        //Config cf=StaticData.getInstance().config;
        //        if (cf.showOfflineContacts) {
        //            addCommand(cmdHideOfflines);
        //            removeCommand(cmdShowOfflines);
        //        } else {
        //            addCommand(cmdShowOfflines);
        //            removeCommand(cmdHideOfflines);
        //        }
    }
    public void setProgress(String pgs,int percent){
        SplashScreen.getInstance().setProgress(pgs, percent);
        setRosterTitle(pgs);
        redraw();
    }
    public void setProgress(int percent){
        SplashScreen.getInstance().setProgress(percent);
        //redraw();
    }
    
    private void setRosterTitle(String s){
        getTitleItem().setElementAt(s, 3);
    }
    
    private int rscaler;
    private int rpercent;
    
    public void rosterItemNotify(){
        rscaler++;
        if (rscaler<4) return;
        rscaler=0;
        rpercent++;
        if (rpercent==100) rpercent=60;
        SplashScreen.getInstance().setProgress(rpercent);
    }
    
    // establishing connection process
    public void run(){
        Iq.setXmlLang(cf.xmlLang);
        setQuerySign(true);
        setProgress(25);
	if (!reconnect) {
	    resetRoster();
	};
        setProgress(26);
        
        //logoff();
        try {
            Account a=sd.account;
            setProgress(SR.MS_CONNECT_TO+a.getServer(), 30);
            theStream= a.openJabberStream();
            setProgress(SR.MS_LOGIN, 40);
            theStream.setJabberListener( this );
        } catch( Exception e ) {
            setProgress(SR.MS_FAILED, 0);
            reconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            e.printStackTrace();
            errorLog( e.getMessage() );
            setQuerySign(false);
            redraw();
            //l.setTitleImgL(0);//offline
        }
        //l.setCallback(this);
    }

    public void resetRoster() {
	synchronized (hContacts) {
	    hContacts=new Vector();
	    groups=new Groups();
	    vContacts=new Vector(); // just for displaying
	    bookmarks=null;
	}
	myJid=new Jid(sd.account.getJid());
	updateContact(sd.account.getNickName(), myJid.getBareJid(), Groups.SELF_GROUP, "self", false);
	
	System.gc();
    }
    
    public void errorLog(String s){
            Msg m=new Msg(Msg.MESSAGE_TYPE_OUT, myJid.getJid(), "Error", s);
            messageStore(m);
    }
    
    public void beginPaint() {
        paintVContacts=vContacts;
    }
    
    public VirtualElement getItemRef(int Index){
        return (VirtualElement) paintVContacts.elementAt(Index);
    }
    
    public int getItemCount(){
        return paintVContacts.size();
    };
    
    private void updateTitle(){
        int s=querysign?RosterIcons.ICON_PROGRESS_INDEX:myStatus;
        int profile=cf.profile;//StaticData.getInstance().config.profile;
        Object en=(profile>1)? new Integer(profile+RosterIcons.ICON_PROFILE_INDEX):null;
        Title title=(Title) getTitleItem();
        title.setElementAt(new Integer(s), 2);
        title.setElementAt(en, 5);
        if (messageCount==0) {
            messageIcon=null;
            title.setElementAt(null,1);
        } else {
            messageIcon=new Integer(RosterIcons.ICON_MESSAGE_INDEX);
            title.setElementAt(" "+messageCount+" ",1);
        }
        title.setElementAt(messageIcon, 0);
    }
    
    boolean countNewMsgs() {
        int m=0;
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                m+=c.getNewMsgsCount();
            }
        }
        messageCount=m;
//#if USE_LED_PATTERN
//--                int pattern=cf.m55LedPattern;
//--                if (pattern>0) EventNotify.leds(pattern-1, m>0);
//#endif
        updateTitle();
        return (m>0);
    }
    
    public void cleanupSearch(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                if ( ((Contact) hContacts.elementAt(index)).getGroupIndex()==Groups.SRC_RESULT_INDEX )
                    hContacts.removeElementAt(index);
                else index++;
            }
        }
        reEnumRoster();
    }
    
    public void cleanupGroup(){
        Group g=(Group)getFocusedObject();
        if (g==null) return;
        if (!g.collapsed) return;
        
        int gi=g.index;

        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact contact=(Contact)hContacts.elementAt(index);
                if (contact.inGroup(g)) {
                    if ( contact.origin>Contact.ORIGIN_ROSTERRES
                         && contact.status==Presence.PRESENCE_OFFLINE
                         && contact.getNewMsgsCount()==0 )
                        hContacts.removeElementAt(index);
                    else index++; 
                }
                else index++; 
            }
        }
    }
    
    ReEnumerator reEnumerator=null;
    
    public void reEnumRoster(){
        if (reEnumerator==null) reEnumerator=new ReEnumerator();
        reEnumerator.queueEnum();
    }
    
    
    public Vector getHContacts() {return hContacts;}
    
    public final void updateContact(final String nick, final String Jid, final String grpName, String subscr, boolean ask) {
        // called only on roster read
        int status=Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status=Presence.PRESENCE_UNKNOWN;
        if (ask) status=Presence.PRESENCE_ASK;
        //if (subscr.equals("remove")) status=Presence.PRESENCE_TRASH;
        if (subscr.equals("remove")) status=-1;
        
        Jid J=new Jid(Jid);
        Contact c=getContact(J,false); // ������� �� bare jid
        if (c==null) {
            c=new Contact(nick, Jid, Presence.PRESENCE_OFFLINE, null);
            addContact(c);
        }
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            c=(Contact)e.nextElement();
            if (c.jid.equals(J,false)) {
                Group group= (c.jid.isTransport())? 
                    groups.getGroup(Groups.TRANSP_INDEX) :
                    groups.getGroup(grpName);
                if (group==null) {
                    group=groups.addGroup(grpName, null);
                }
                c.nick=nick;
                c.setGroup(group);
                c.subscr=subscr;
                c.offline_type=status;
                c.ask_subscribe=ask;
                //if (status==Presence.PRESENCE_TRASH) c.status=status;
                //if (status!=Presence.PRESENCE_OFFLINE) c.status=status;
            }
        }
        if (status<0) removeTrash();
    }
    
    private final void removeTrash(){
        int index=0;
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact c=(Contact)hContacts.elementAt(index);
                if (c.offline_type<0) {
                    hContacts.removeElementAt(index);
                } else index++;
            }
        }
    }

    public final void mucContact(String from, byte origin){
        // muc message
        boolean isRoom=(origin==Contact.ORIGIN_GROUPCHAT);
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();
        //String nick=null;
        //if (rp>0) if (!isRoom) nick=from.substring(rp+1);
        
        //updateContact(null /*nick*/ , from, room, "muc", false);
        Group grp=groups.getGroup(roomJid);
	
        if (grp==null) grp=groups.addGroup(new ConferenceGroup(roomJid, room) );
        //grp.imageExpandedIndex=ImageList.ICON_GCJOIN_INDEX;
        /*
        Contact c=presenceContact(from, isRoom?Presence.PRESENCE_ONLINE:-1);
        if (isRoom){  
            //c.status=Presence.PRESENCE_ONLINE;  
            c.transport=7; //FIXME: ������ �������
            c.jid=new Jid(from.substring(0, rp));
            c.origin=Contact.ORIGIN_GROUPCHAT;
        }
         */ 
        Contact c;
        if (isRoom){
            c=presenceContact(from.substring(0, rp), Presence.PRESENCE_ONLINE);
            //c.status=Presence.PRESENCE_ONLINE;  
            c.transport=7; //FIXME: ������ �������
            c.bareJid=from;
            c.origin=Contact.ORIGIN_GROUPCHAT;
            //c.priority=99;
            c.jidHash=0;
	    c.conferenceJoinTime=Time.localTime();
	    ((ConferenceGroup)grp).setConference(c);
        } else {
	    if (origin==Contact.ORIGIN_GC_MYSELF) {
		c=((ConferenceGroup)grp).getSelfContact();
		if (c==null) {
		    c=presenceContact(from, -1);
		    c.nick=from.substring(rp+1);
		    ((ConferenceGroup)grp).setSelfContact(c);
		}
	    } else {
                c=presenceContact(from, -1);
	        c.nick=from.substring(rp+1);
	    }
        }
        c.setGroup(grp);
        c.offline_type=Presence.PRESENCE_OFFLINE;
        if (c.origin<origin) c.origin=origin;
        sort();
    }
    
    public final Contact presenceContact(final String jid, int Status) {
        
        // �������� ������� �� ������ ������
        Jid J=new Jid(jid);
        
        Contact c=getContact(J, true); //Status!=Presence.PRESENCE_ASK);
        if (c!=null) {
            // ��������� ������
            if (Status>=0) {
                //if (c.status<7 || c.status==Presence.PRESENCE_ASK) 
                c.status=Status;
                sort();
                //System.out.println("updated");
            }
            return c;
        }
        // �������� ������� ��� ��������
        
        if (Status<0) Status=Presence.PRESENCE_OFFLINE;
        c=getContact(J, false);
        if (c==null) {
            // ��... ��� ����� �����
            // ����� ����� �������� �����
            //System.out.println("new");
            c=new Contact(null, jid, Status, "not-in-list");
	    c.bareJid=J.getBareJid();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(groups.getGroup(Groups.NIL_INDEX));
            addContact(c);
        } else {
            // ����� jid � ����� ��������
            if (c.origin==Contact.ORIGIN_ROSTER) {
                c.origin=Contact.ORIGIN_ROSTERRES;
                c.status=Status;
                c.jid=J;
                //System.out.println("add resource");
            } else {
                c=c.clone(J, Status);
                addContact(c);
                //System.out.println("cloned");
            }
        }
        sort();
        return c;
    }
    public void addContact(Contact c) {
        synchronized (hContacts) { hContacts.addElement(c); }
    }
    
    private void sort(){
        synchronized (hContacts) {
            int f, i;
            Contact temp, temp2;
            
            for (f = 1; f < hContacts.size(); f++) {
                temp=getContact(f);
                if ( temp.compare(getContact(f-1)) >=0 ) continue;
                i    = f-1;
                while (i>=0){
                    temp2=getContact(i);
                    if (temp2.compare(temp) <0) break;
                    hContacts.setElementAt(temp2,i+1);
                    i--;
                }
                hContacts.setElementAt(temp,i+1);
            }
        }
        //reEnumRoster();
    }
    
    private final Contact getContact(int index) {
        return (Contact)(hContacts.elementAt(index));
    }
    
    public final Contact getContact(final String Jid, boolean compareResources) {
        return (getContact(new Jid(Jid), compareResources));
    }
    public final Contact getContact(final Jid j, final boolean compareResources) {
        synchronized (hContacts) {
            for (Enumeration e=hContacts.elements();e.hasMoreElements();){
                Contact c=(Contact)e.nextElement();
                if (c.jid.equals(j,compareResources)) return c;
            }
        }
        return null;
    }
    
    /**
     * Method to inform the server we are now online
     */
    
    public void sendPresence(int status) {
        myStatus=status;
        setQuerySign(false);
        if (myStatus==Presence.PRESENCE_OFFLINE) {
            synchronized(hContacts) {
                for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
                    Contact c=(Contact)e.nextElement();
                    //if (c.status<Presence.PRESENCE_UNKNOWN)
                        c.status=Presence.PRESENCE_OFFLINE; // keep error & unknown
                }
            }
        }
        //Vector v=sd.statusList;//StaticData.getInstance().statusList;
        //ExtendedStatus es=null;
        
        // reconnect if disconnected        
        if (myStatus!=Presence.PRESENCE_OFFLINE && theStream==null ) {
            reconnect=(hContacts.size()>1);
            redraw();
            
            new Thread(this).start();
            return;
        }
        
        // send presence
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
        if (theStream!=null) {
            if (!StaticData.getInstance().account.isMucOnly() )
		theStream.send( presence );
            
            sendConferencePresence();

            // disconnect
            if (status==Presence.PRESENCE_OFFLINE) {
                try {
                    theStream.close();
                } catch (Exception e) { e.printStackTrace(); }
                theStream=null;
                System.gc();
            }
        }
        Contact c=presenceContact(myJid.getJid(), myStatus);
        
        reEnumRoster();
    }
    
    public Contact selfContact() {
	return presenceContact(myJid.getJid(), -1);
    }
    
    public void sendConferencePresence() {
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact c=(Contact) e.nextElement();
            if (c.origin!=Contact.ORIGIN_GROUPCHAT) continue;
            if (c.status==Presence.PRESENCE_OFFLINE) continue;
            Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
            presence.setAttribute("to", c.getJid());
            theStream.send(presence);
        }
    }
    
    public void sendPresence(String to, String type, JabberDataBlock child) {
        JabberDataBlock presence=new Presence(to, type);
        if (child!=null) presence.addChild(child);
        theStream.send(presence);
    }
    /**
     * Method to send a message to the specified recipient
     */
    
    public void sendMessage(Contact to, final String body, final String subject , int composingState) {
        boolean groupchat=to.origin==Contact.ORIGIN_GROUPCHAT;
        Message simpleMessage = new Message( 
                to.getJid(), 
                body, 
                subject, 
                groupchat 
        );
        if (groupchat && body==null /*&& subject==null*/) return;
        if (composingState>0) {
            JabberDataBlock event=new JabberDataBlock("x", null,null);
            event.setNameSpace("jabber:x:event");
            //event.addChild(new JabberDataBlock("id",null, null));
            if (composingState==1) {
                event.addChild("composing", null);
            }
            simpleMessage.addChild(event);
        }
        //System.out.println(simpleMessage.toString());
        theStream.send( simpleMessage );
    }
    
    private Vector vCardQueue;
    public void resolveNicknames(int transportIndex){
	vCardQueue=new Vector();
	for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
	    Contact k=(Contact) e.nextElement();
	    if (k.jid.isTransport()) continue;
	    if (k.transport==transportIndex && k.nick==null && k.getGroupIndex()>=Groups.COMMON_INDEX) {
		vCardQueue.addElement(VCard.getVCardReq(k.getJid(), "nickvc"+k.bareJid));
	    }
	}
	setQuerySign(true);
	sendVCardReq();
	
    }
    private void sendVCardReq(){
        querysign=false; 
        if (vCardQueue!=null) if (!vCardQueue.isEmpty()) {
            JabberDataBlock req=(JabberDataBlock) vCardQueue.lastElement();
            vCardQueue.removeElement(req);
            //System.out.println(k.nick);
            theStream.send(req);
            querysign=true;
        }
        updateTitle();
    }
    /**
     * Method to handle an incomming datablock.
     *
     * @param data The incomming data
     */
    public void blockArrived( JabberDataBlock data ) {
        try {
            
            if( data instanceof Iq ) {
                String type = (String) data.getTypeAttribute();
                if ( type.equals( "error" ) ) {
                    if (data.getAttribute("id").equals("auth-s")) {
                        // ������ �����������
                        myStatus=Presence.PRESENCE_OFFLINE;
                        setProgress(SR.MS_LOGIN_FAILED, 0);
                        
                        JabberDataBlock err=data.getChildBlock("error");
                        errorLog(err.toString());
                        
                        reconnect=false;
                        setQuerySign(false);
                        redraw();
                    }
                }
                String id=(String) data.getAttribute("id");
                
                if (id!=null) if (id.startsWith("nickvc")) {
                    VCard vc=new VCard(data);//.getNickName();
                    String from=vc.getJid();
                    String nick=vc.getNickName();
                    Contact c=getContact(from, false);
                    String group=(c.getGroupIndex()==Groups.COMMON_INDEX)?
                        null: c.getGroup().name;
                    if (nick!=null)  storeContact(from,nick,group, false);
                    //updateContact( nick, c.rosterJid, group, c.subscr, c.ask_subscribe);
                    sendVCardReq();
                }
                
                if ( type.equals( "result" ) ) {
                    if (id.equals("auth-s") ) {
                        // ������������. ������, ���� ��� ���������, �� ������ ����� ������
                        if (reconnect) {
                            querysign=reconnect=false;
                            sendPresence(myStatus);
                            return;
                        }
                        
                        // ����� ����� ������ ������
                        theStream.enableRosterNotify(true);
                        rpercent=60;
			if (StaticData.getInstance().account.isMucOnly()) {
			    setProgress(SR.MS_CONNECTED,100);
			    try {
				reEnumRoster();
			    } catch (Exception e) { e.printStackTrace(); }
			    querysign=reconnect=false;
			    SplashScreen.getInstance().close(); // display.setCurrent(this);
			} else {
			    JabberDataBlock qr=new IqQueryRoster();
			    setProgress("Roster request ", 60);
			    theStream.send( qr );
			}
                    }
                    if (id.equals("getros")) {
                        // � ��� � ������ ������ :)
                        //SplashScreen.getInstance().setProgress(95);
                        
                        theStream.enableRosterNotify(false);

                        processRoster(data);
                        
                        setProgress(SR.MS_CONNECTED,100);
                        reEnumRoster();
                        // ������ ����� �����������
                        querysign=reconnect=false;
                        sendPresence(myStatus);
                        //sendPresence(Presence.PRESENCE_INVISIBLE);
                        
                        SplashScreen.getInstance().close(); // display.setCurrent(this);
                        
                    }
                    if (id.startsWith("getvc")) {
                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        Contact c=presenceContact(vcard.getJid(),-1);
                        c.vcard=vcard;
                        new vCardForm(display, vcard, c.getGroupIndex()==Groups.SELF_INDEX);
                    }
                    if (id.equals("getver")) {
                        JabberDataBlock vc=data.getChildBlock("query");
                        if (vc!=null) {
                            querysign=false;
                            String from=data.getAttribute("from");
                            String body=IqVersionReply.dispatchVersion(vc);
                            
                            Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, SR.MS_CLIENT_INFO, body);
                            messageStore(m);
                            redraw();
                            
                        }
                    }
                    
                } else if (type.equals("get")){
                    JabberDataBlock query=data.getChildBlock("query");
                    if (query!=null){
                        // ��������� �� ������ ������ �������
                        if (query.isJabberNameSpace("jabber:iq:version"))
                            //String xmlns=query.getAttribute("xmlns");
                            //if (xmlns!=null) if (xmlns.equals("jabber:iq:version"))
                            theStream.send(new IqVersionReply(data));
                        // ��������� �� ������ ���������� ������� �������
                        if (query.isJabberNameSpace("jabber:iq:time"))
                            //String xmlns=query.getAttribute("xmlns");
                            //if (xmlns!=null) if (xmlns.equals("jabber:iq:version"))
                            theStream.send(new IqTimeReply(data));
                    }
                } else if (type.equals("set")) {
                    processRoster(data);
                    reEnumRoster();
                }
            }
            
            // If we've received a message
            
            else if( data instanceof Message ) {
                querysign=false;
                Message message = (Message) data;
                
                String from=message.getFrom();
                String body=message.getBody().trim();
                String tStamp=message.getTimeStamp();
		
                int start_me=-1;    //  �� ��������� ���
                String name=null;
                boolean groupchat=false;
                try { // type=null
		    String type=message.getTypeAttribute();
                    if (type.equals("groupchat")) {
                        groupchat=true;
                        start_me=0; // �������� ��� � ������
                        int rp=from.indexOf('/');
                        
                        name=from.substring(rp+1);
                        
                        if (rp>0) from=from.substring(0, rp);
                    }
		    if (type.equals("error")) {
			body=SR.MS_ERROR_+message.getChildBlock("error")+"\n"+body;
		    }
                } catch (Exception e) {}
                Contact c=presenceContact(from, -1);
                if (name==null) name=c.getName();
                // /me
                if (body!=null) if (body.startsWith("/me ")) start_me=3;
                if (start_me>=0) {
                    StringBuffer b=new StringBuffer(name);
                    if (start_me==0) b.append("> ");
                    b.append(body.substring(start_me));
                    body=b.toString();
                }
                
                boolean compose=false;
                JabberDataBlock x=message.getChildBlock("x");
                if (body.length()==0) body=null; 
                
                if (x!=null) {
                    compose=(x.getChildBlock("composing")!=null);
                    if (compose) c.accept_composing=true;
                    if (body!=null) compose=false;
                    c.setComposing(compose);
                }
                redraw();

                if (body==null) return;
                
                String subj=message.getSubject().trim();
                if (subj.length()==0) subj=null;
  
            
                Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, subj, body);
                if (tStamp!=null) 
                    m.dateGmt=Time.dateIso8601(tStamp);
                if (groupchat) {
                    if (c.bareJid.equals(message.getFrom())) {
                        m.messageType=Msg.MESSAGE_TYPE_OUT;
                        m.unread=false;
                    } else {
                        if (m.dateGmt<=c.conferenceJoinTime) m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                    } 
                }
                messageStore(m);
                //Contact c=getContact(from);
                //c.msgs.addElement(m);
                //countNewMsgs();
                //setFocusTo(c);
                //redraw();
            }
            // �����������
            else if( data instanceof Presence ) {
                if (myStatus==Presence.PRESENCE_OFFLINE) return;
                Presence pr= (Presence) data;
                
                String from=pr.getFrom();
                pr.dispathch();
                int ti=pr.getTypeIndex();
                //PresenceContact(from, ti);
                Msg m=new Msg(
                        (ti==Presence.PRESENCE_AUTH)?
                            Msg.MESSAGE_TYPE_AUTH:Msg.MESSAGE_TYPE_PRESENCE,
                        from,
                        null,
                        pr.getPresenceTxt());
                Contact c=messageStore(m);
                c.priority=pr.getPriority();
                JabberDataBlock xmuc=pr.findNamespace("http://jabber.org/protocol/muc");
                if (xmuc!=null){
                    int rp=from.indexOf('/');
                    String nick=from.substring(rp+1);
                    c.sortCode(nick);
                    StringBuffer b=new StringBuffer(nick);
                    JabberDataBlock item=xmuc.getChildBlock("item");
                    
                    String role=item.getAttribute("role");
                    String affil=item.getAttribute("affiliation");
                    String chNick=item.getAttribute("nick");
                    
                    JabberDataBlock status=xmuc.getChildBlock("status");
                    String statusCode=(status==null)? "" : status.getAttribute("code");

                    boolean moderator=role.startsWith("moderator");
                    c.transport=(moderator)? 6:0; //FIXME: ������ �������
                    c.jidHash=c.jidHash & 0x3fffffff | ((moderator)? 0:0x40000000);
                    
                    if (pr.getTypeIndex()==Presence.PRESENCE_OFFLINE) {
                        String reason=item.getChildBlockText("reason");
                        if (statusCode.equals("303")) {
                            b.append(" is now known as ");
                            b.append(chNick);
			    // �������� jid
			    String newJid=from.substring(0,rp+1)+chNick;
			    System.out.println(newJid);
			    c.jid.setJid(newJid);
			    c.bareJid=newJid; // ���������, ����� � ��� ������...
			    from=newJid;
			    c.nick=chNick;
			    
                        } else if (statusCode.equals("307")){
                            b.append(" was kicked (");
                            b.append(reason);
                            b.append(")");
                            if (c==((ConferenceGroup)c.getGroup()).getSelfContact())
                                leaveRoom(0,c.getGroup());
                        } else if (statusCode.equals("301")){
                            b.append(" was banned (");
                            b.append(reason);
                            b.append(")");
                            //if (c==((ConferenceGroup)groups.getGroup(c.getGroupIndex())).getSelfContact())
                            if (c==((ConferenceGroup)c.getGroup()).getSelfContact())
                                leaveRoom(0, c.getGroup());
                        } else
                        b.append(" has left the channel");
		    } else {
			if (c.status==Presence.PRESENCE_OFFLINE) {
			    String realJid=item.getAttribute("jid");
			    if (realJid!=null) {
				b.append(" (");
				b.append(realJid);
				b.append(')');
				c.realJid=realJid;  //for moderating purposes
			    }
			    b.append(" has joined the channel as ");
			    b.append(role);
			    if (!affil.equals("none")) {
				b.append(" and ");
				b.append(affil);
			    }
			} else {                        
			    b.append(" is now ");
			    b.append(pr.getPresenceTxt());
			}
                    }
                    //System.out.println(b.toString());


                    mucContact(from, Contact.ORIGIN_GC_MEMBER);
                    //c.nick=nick;
                    
                    from=from.substring(0, rp);
                    m=new Msg(
                        Msg.MESSAGE_TYPE_PRESENCE,
                        from,
                        null,
                        b.toString());
                    messageStore(m);
                } // if (muc)
		if (ti>=0) c.status=ti;
		sort();
                reEnumRoster();
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    void processRoster(JabberDataBlock data){
        JabberDataBlock q=data.getChildBlock("query");
        if (!q.isJabberNameSpace("jabber:iq:roster")) return;
        int type=0;
        
        Vector cont=(q!=null)?q.getChildBlocks():null;
        
        if (cont!=null)
            for (Enumeration e=cont.elements(); e.hasMoreElements();){
                JabberDataBlock i=(JabberDataBlock)e.nextElement();
                if (i.getTagName().equals("item")) {
                    String name=i.getAttribute("name");
                    String jid=i.getAttribute("jid");
                    String subscr=i.getAttribute("subscription");
                    boolean ask= (i.getAttribute("ask")!=null);

                    // ����� ������
                    String group=i.getChildBlockText("group");
                    if (group.length()==0) group=Groups.COMMON_GROUP;

                    // ��� ����� ���������, ����� ����� jabber:iq:roster,
                    // �� ������ ������� ��� ��� ����������
                    //String iqType=data.getTypeAttribute();
                    //if (iqType.equals("set")) type=1;

                    updateContact(name,jid,group, subscr, ask);
                }
            
            }
    }
    
    
    Contact messageStore(Msg message){
        Contact c=presenceContact(message.from,-1);
        if (c.getGroupIndex()==Groups.NIL_INDEX) 
            if (!cf.notInList) return c;

        if (c==null) return c;  // not to store/signal not-in-list message
        c.addMessage(message);
        //message.from=c.getNickJid();
        /*
        switch (message.messageType) {
            case Msg.MESSAGE_TYPE_PRESENCE:
            case Msg.MESSAGE_TYPE_OUT: return c;
        }*/
        if (!message.unread) return c;
        
        if (countNewMsgs()) reEnumRoster();
        
        if (c.getGroupIndex()==Groups.IGNORE_INDEX) return c;    // no signalling/focus on ignore
        
	if (cf.popupFromMinimized)
	    Bombus.getInstance().hideApp(false);
	
//#if !(DISABLE_AUTOFOCUS)
//#         focusToContact(c, false);
//#endif

        if (message.messageType!=Msg.MESSAGE_TYPE_HISTORY) 
            AlertProfile.playNotify(display, 0);
        return c;
    }

    private void focusToContact(final Contact c, boolean force) {
	
	Group g=c.getGroup();
	if (g.collapsed) {
	    g.collapsed=false;
	    reEnumerator.queueEnum(c, force);
	    //reEnumRoster();
	} else {
	    
	    int index=vContacts.indexOf(c);
	    if (index>=0) moveCursorTo(index, force);
	}
    }
    
    
    /**
     * Method to begin talking to the server (i.e. send a login message)
     */
    
    public void beginConversation(String SessionId) {
        //try {
        setProgress(SR.MS_LOGINPGS, 42);
        Account a=sd.account;//StaticData.getInstance().account;
        Login login = new Login( 
                a.getUserName(), 
                a.getServer(), 
                a.getPassword(), 
                a.getPlainAuth()?null:SessionId, 
                a.getResource()
        );
        theStream.send( login );
        //} catch( Exception e ) {
        //l.setTitleImgL(0);
        //e.printStackTrace();
        //}
        //l.setTitleImgL(2);
        
    }
    
    /**
     * If the connection is terminated then print a message
     *
     * @e The exception that caused the connection to be terminated, Note that
     *  receiving a SocketException is normal when the client closes the stream.
     */
    public void connectionTerminated( Exception e ) {
        //l.setTitleImgL(0);
        //System.out.println( "Connection terminated" );
        if( e != null ) {
            errorLog(e.getMessage());
            e.printStackTrace();
        }
        setProgress(SR.MS_DISCONNECTED, 0);
        try {
            sendPresence(Presence.PRESENCE_OFFLINE);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        redraw();
    }
    
    //private VList l;
    //private IconTextList l;
    
    public void eventOk(){
        super.eventOk();
        if (createMsgList()==null) {
            cleanupGroup();
            reEnumRoster();
        }
    }
    
    
    private Displayable createMsgList(){
        Object e=getFocusedObject();
        if (e instanceof Contact) {
            return new ContactMessageList((Contact)e,display);
        }
        return null;
    }
    protected void keyGreen(){
        Displayable pview=createMsgList();
        if (pview!=null) {
            Contact c=(Contact)getFocusedObject();
            ( new MessageEdit(display, c, c.msgSuspended) ).setParentView(pview);
            c.msgSuspended=null;
        }
        //reEnumRoster();
    }
    
    public void userKeyPressed(int keyCode){
        if (keyCode==KEY_NUM0) {
            if (messageCount==0) return;
            Object atcursor=getFocusedObject();
            Contact c=null;
            if (atcursor instanceof Contact) c=(Contact)atcursor;
            // � ���� ������ �� ������, �� ������ � ������ ������.
            else c=(Contact)hContacts.firstElement();
            
            Enumeration i=hContacts.elements();
            
            int pass=0; // 0=���� ������, 1=����
            while (pass<2) {
                if (!i.hasMoreElements()) i=hContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) if (p.getNewMsgsCount()>0) { 
		    focusToContact(p, true);
                    break; 
                }
                if (p==c) pass++; // ������ ���� �������
            }
        }
    }
    
    public void logoff(){
        if (theStream!=null)
        try {
             sendPresence(Presence.PRESENCE_OFFLINE);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    };
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdQuit) {
            destroyView();
            logoff();
            //StaticData sd=StaticData.getInstance();
            //cf.saveToStorage();
	    Bombus.getInstance().notifyDestroyed();
            return;
        }
        if (c==cmdMinimize) { Bombus.getInstance().hideApp(true);  }
        
//#if !(TRANSLATED)        
        if (c.getLabel().charAt(0)>127) theStream=null; // 8==o ()() fuck translations
//#endif
        
        if (c==cmdAccount){ new AccountSelect(display, false); }
        if (c==cmdGroupChat) { new ConferenceForm(display); }
        /*if (c==cmdLeave) {
            if (atCursor instanceof Group) leaveRoom( ((Group)atCursor).index );
        }*/
        if (c==cmdStatus) { new StatusSelect(display); }
        if (c==cmdAlert) { new AlertProfile(display); }
        if (c==cmdOptions){ new ConfigForm(display); }
        if (c==cmdActions) { new RosterItemActions(display, getFocusedObject()); }
        if (c==cmdTools) { new RosterToolsMenu(display); }
        if (c==cmdArchive) { new ArchiveList(display, null); }
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        if (c==cmdAdd) {
            //new MIDPTextBox(display,"Add to roster", null, new AddContact());
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupIndex()!=Groups.NIL_INDEX && cn.getGroupIndex()!=Groups.SRC_RESULT_INDEX) cn=null;
            }
            new ContactEdit(display, cn);
        }
    }
    

    public void reEnterRoom(Group group) {
	ConferenceGroup confGroup=(ConferenceGroup)group;
        sendPresence(confGroup.getSelfContact().getJid(), null, null);

	confGroup.getConference().status=Presence.PRESENCE_ONLINE;
    }
    public void leaveRoom(int index, Group group){
	//Group group=groups.getGroup(index);
	ConferenceGroup confGroup=(ConferenceGroup)group;
	Contact myself=confGroup.getSelfContact();
        sendPresence(myself.getJid(), "unavailable", null);
	
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact contact=(Contact)e.nextElement();
            if (contact.inGroup(group)) contact.status=Presence.PRESENCE_OFFLINE; 
        }

    }
    
    protected void showNotify() { super.showNotify(); countNewMsgs(); }
    
    
    protected void keyRepeated(int keyCode) {
        super.keyRepeated(keyCode);
        if (kHold==keyCode) return;
        //kHold=keyCode;
        kHold=keyCode;
        
        if (keyCode==cf.keyLock) 
            new KeyBlock(display, getTitleItem(), cf.keyLock, cf.ghostMotor); 

        if (keyCode==cf.keyVibra) {
            cf.profile=(cf.profile==AlertProfile.VIBRA)? 
                cf.def_profile : AlertProfile.VIBRA;
            updateTitle();
            redraw();
        }
        
        if (keyCode==cf.keyOfflines) {
            cf.showOfflineContacts=!cf.showOfflineContacts;
            reEnumRoster();
        }

        if (keyCode==cf.keyHide && cf.allowMinimize) {
            Bombus.getInstance().hideApp(true);
        }
    }

    protected void keyPressed(int keyCode){
	super.keyPressed(keyCode);
	if (keyCode=='3') searchGroup(-1);
	if (keyCode=='9') searchGroup(1);
	
    }
    private void searchGroup(int direction){
	synchronized (vContacts) {
	    int size=vContacts.size();
	    int pos=cursor;
	    int count=size;
	    try {
		while (count>0) {
		    pos+=direction;
		    if (pos<0) pos=size-1;
		    if (pos>=size) pos=0;
		    if (vContacts.elementAt(pos) instanceof Group) break;
		}
		moveCursorTo(pos, true);
	    } catch (Exception e) { }
	}
    }

    public void deleteContact(Contact c) {
	for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
	    Contact c2=(Contact)e. nextElement();
	    if (c.jid.equals(c2. jid,false)) {
		c2.status=c2.offline_type=Presence.PRESENCE_TRASH;
	    }
	}
	
	if (c.getGroupIndex()==Groups.NIL_INDEX) {
	    hContacts.removeElement(c);
	    reEnumRoster();
	} else
	    theStream.send(new IqQueryRoster(c.getBareJid(),null,null,"remove"));
    }
    /*public void focusedItem(int index) {
        //TODO: refactor this code
        // ��� ������ ���������� ��� ��������� (?)
        if (!isShown()) return;
        if (vContacts==null) return;
        if (index>=vContacts.size()) return;
        Object atCursor=vContacts.elementAt(index);
        if (atCursor instanceof Contact) {
            addCommand(cmdActions);
            //removeCommand(cmdGroup);
        } else removeCommand(cmdActions);
        
        if (atCursor instanceof Group) {    // FIXME: ������� cmdLeave
            Group g=(Group)atCursor;
            if (g.index==Groups.SRC_RESULT_INDEX)  addCommand(cmdDiscard);
            if (g.imageExpandedIndex==ImageList.ICON_GCJOIN_INDEX) addCommand(cmdLeave);
        } else {
            removeCommand(cmdDiscard);
            removeCommand(cmdLeave);
        }
        
    }
     */
   
    
    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateTitle();
    }
    void setMucMod(Contact contact, Hashtable itemAttributes){
        JabberDataBlock iq=new Iq();
        iq.setTypeAttribute("set");
        iq.setAttribute("to", contact.jid.getBareJid());
        JabberDataBlock query=iq.addChild("query", null);
        query.setNameSpace("http://jabber.org/protocol/muc#admin");
        JabberDataBlock item=new JabberDataBlock("item", null, itemAttributes);
        query.addChild(item);
        //System.out.println(iq);
        theStream.send(iq);
    }
    /**
     * store cotnact on server
     */
    public void storeContact(String jid, String name, String group, boolean newContact){
        
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (newContact) theStream.send(new Presence(jid,"subscribe"));
    }

    private class ReEnumerator implements Runnable{

        Thread thread;
        int pendingRepaints=0;
	boolean force;
	
	Object desiredFocus;
        
        public void queueEnum(Object focusTo, boolean force) {
	    desiredFocus=focusTo;
	    this.force=force;
	    queueEnum();
        }
	
        synchronized public void queueEnum() {
            pendingRepaints++;
            if (thread==null) (thread=new Thread(this)).start();
        }
        
        public void run(){
            try {
                while (pendingRepaints>0) {
                    //System.out.println(pendingRepaints);
                    pendingRepaints=0;
                    
                    int locCursor=cursor;
                    Object focused=(desiredFocus==null)?getFocusedObject():desiredFocus;
		    desiredFocus=null;
                    
                    Vector tContacts=new Vector(vContacts.size());
                    //boolean offlines=cf.showOfflineContacts;//StaticData.getInstance().config.showOfflineContacts;
                    
                    Enumeration e;
                    int i;
                    groups.resetCounters();
                    
                    synchronized (hContacts) {
                        for (e=hContacts.elements();e.hasMoreElements();){
                            Contact c=(Contact)e.nextElement();
                            boolean online=c.status<5;
                            // group counters
                            Group grp=c.getGroup();
			    grp.addContact(c);
                        }
                    }
                    // self-contact group
                    if (cf.selfContact || groups.getGroup(Groups.SELF_INDEX).tonlines>1)
                        groups.addToVector(tContacts, Groups.SELF_INDEX);
                    // adding groups
                    for (i=Groups.COMMON_INDEX;i<groups.getCount();i++)
                        groups.addToVector(tContacts,i);
                    // hiddens
                    if (cf.ignore) groups.addToVector(tContacts,Groups.IGNORE_INDEX);
                    // not-in-list
                    if (cf.notInList) groups.addToVector(tContacts,Groups.NIL_INDEX);
                    // transports
                    if (cf.showTransports) groups.addToVector(tContacts,Groups.TRANSP_INDEX);
                    
                    // search result
                    //if (groups.getGroup(Groups.SRC_RESULT_INDEX).tncontacts>0)
                    groups.addToVector(tContacts, Groups.SRC_RESULT_INDEX);
                    
                    vContacts=tContacts;
                    
                    setRosterTitle("("+groups.getRosterOnline()+"/"+groups.getRosterContacts()+")");
                    
                    //resetStrCache();
                    if (cursor<0) cursor=0;
                    
                    // ����� ������ �� ������� �������
                    // TODO: ����������������!
                    if ( locCursor==cursor && focused!=null ) {
                        int c=vContacts.indexOf(focused);
                        if (c>=0) moveCursorTo(c, force);
			force=false;
                    }
                    if (cursor>=vContacts.size()) moveCursorEnd(); // ����� ������ �� �������
                    
                    focusedItem(cursor);
                    redraw();
                }
            } catch (Exception e) {e.printStackTrace();}
            thread=null;
        }
    }
}

