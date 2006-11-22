/*
 * Roster.java
 *
 * Created on 6 Январь 2005 г., 19:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */


package Client;

import Conference.BookmarkQuery;
import Conference.ConferenceGroup;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import Info.Version;
import archive.ArchiveList;
import images.RosterIcons;
//#if FILE_TRANSFER
import io.file.transfer.TransferDispatcher;
//#endif
import locale.SR;
import login.LoginListener;
import login.NonSASLAuth;
import login.SASLAuth;
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
import Client.Config;

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
        Runnable,
        LoginListener
        //ContactEdit.StoreContact
        //Thread
{
    
    
    private Jid myJid;
    
    /**
     * The stream representing the connection to ther server
     */
    public JabberStream theStream ;
        
    int messageCount;
    
    private Object messageIcon;
    public Object transferIcon;
   
    boolean reconnect=false;
    boolean querysign=false;
    
    boolean storepresence=true;
    
    public int myStatus=Presence.PRESENCE_OFFLINE;
    
    private Vector hContacts;
    private Vector vContacts;
    
    private Vector paintVContacts;  // для атомных операций.
    
    public Groups groups;
    
    public Vector bookmarks;

    
    private Command cmdActions=new Command(SR.MS_ITEM_ACTIONS, Command.SCREEN, 1);
    private Command cmdStatus=new Command(SR.MS_STATUS_MENU, Command.SCREEN, 2);
    private Command cmdActiveContact;//=new Command(SR.MS_ACTIVE_CONTACTS, Command.SCREEN, 3);
    private Command cmdAlert=new Command(SR.MS_ALERT_PROFILE_CMD, Command.SCREEN, 8);
    private Command cmdConference=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10);
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 10);
    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 12);
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 14);    
    private Command cmdAccount=new Command(SR.MS_ACCOUNT_, Command.SCREEN, 15);
    private Command cmdInfo=new Command(SR.MS_ABOUT, Command.SCREEN, 80);
    private Command cmdMinimize=new Command(SR.MS_APP_MINIMIZE, Command.SCREEN, 90);
    private Command cmdQuit=new Command(SR.MS_APP_QUIT, Command.SCREEN, 99);
    
    private Config cf;
    private StaticData sd=StaticData.getInstance();

//#if (MOTOROLA_BACKLIGHT)
    private int blState=Integer.MAX_VALUE;

//#endif

//#if SASL
    private String token;

//#endif
    
    private long lastMessageTime=Time.localTime();
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
        title.addElement(null);
        //displayStatus();
        
        //l.setTitleImgL(6); //connect
        hContacts=new Vector();
        groups=new Groups();
        
        vContacts=new Vector(); // just for displaying
        
        int activeType=Command.SCREEN;
        String platform=Version.getPlatformName();
        if (platform.startsWith("Nokia")) activeType=Command.BACK;
        if (platform.startsWith("Intent")) activeType=Command.BACK;
        
        cmdActiveContact=new Command(SR.MS_ACTIVE_CONTACTS, activeType, 3);
        
        addCommand(cmdStatus);
        addCommand(cmdActions);
        addCommand(cmdActiveContact);
        addCommand(cmdAlert);
        addCommand(cmdAdd);
        //addCommand(cmdServiceDiscovery);
        addCommand(cmdConference);
        //addCommand(cmdPrivacy);
        addCommand(cmdTools);
        addCommand(cmdArchive);
        addCommand(cmdInfo);
        addCommand(cmdAccount);

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
        Iq.setXmlLang(SR.MS_XMLLANG);
        setQuerySign(true);
        setProgress(25);
	if (!reconnect) {
	    resetRoster();
	};
        setProgress(26);
        
        //logoff();
        try {
            Account a=sd.account;
//#if SASL_XGOOGLETOKEN
            if (a.isSASL() && a.getServer().startsWith("gmail")) {
                setProgress(SR.MS_TOKEN, 30);
                token=new SASLAuth(a, null, this, null).responseXGoogleToken();
                if (token==null) throw new Exception("Can't get Google token");
            }
//#endif
            setProgress(SR.MS_CONNECT_TO+a.getServer(), 30);
            SR.loaded();
            theStream= a.openJabberStream();
            setProgress(SR.MS_OPENING_STREAM, 40);
            theStream.setJabberListener( this );
        } catch( Exception e ) {
            setProgress(SR.MS_FAILED, 0);
            reconnect=false;
            myStatus=Presence.PRESENCE_OFFLINE;
            e.printStackTrace();
            String error=e.getClass().getName()+"\n"+e.getMessage();
            errorLog( error );
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
        if (s==null) return;
        if (s.length()==0) return;
        Alert error=new Alert(SR.MS_ERROR_, s, null, null);
        //error.setTimeout(30000);
        error.addCommand(new Command(SR.MS_OK, Command.BACK, 1));
        display.setCurrent(error, display.getCurrent());
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
    
    public void setEventIcon(Object icon){
        transferIcon=icon;
        getTitleItem().setElementAt(icon, 7);
        redraw();
    }
    
    public Object getEventIcon() {
        if (transferIcon!=null) return transferIcon;
        return messageIcon;
    }
    
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
                if ( ((Contact) hContacts.elementAt(index)).getGroupType()==Groups.TYPE_SEARCH_RESULT )
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
        
        if (g instanceof ConferenceGroup) {
            ConferenceGroup cg= (ConferenceGroup) g;
            if (cg.getSelfContact().status==Presence.PRESENCE_OFFLINE)
                cg.getConference().status=Presence.PRESENCE_OFFLINE;
        }
        //int gi=g.index;

        int index=0;

        int onlineContacts=0;
        
        synchronized (hContacts) {
            while (index<hContacts.size()) {
                Contact contact=(Contact)hContacts.elementAt(index);
                if (contact.inGroup(g)) {
                    if ( contact.origin>Contact.ORIGIN_ROSTERRES
                         && contact.status==Presence.PRESENCE_OFFLINE
                         && contact.getNewMsgsCount()==0 )
                        hContacts.removeElementAt(index);
                    else { 
                        index++;
                        onlineContacts++;
                    } 
                }
                else index++; 
            }
            if (onlineContacts==0) {
                if (g.index>Groups.TYPE_COMMON) groups.removeGroup(g);
            }
        }
    }
    
    ReEnumerator reEnumerator=null;
    
    public void reEnumRoster(){
        if (reEnumerator==null) reEnumerator=new ReEnumerator();
        reEnumerator.queueEnum();
    }
    
    
    public Vector getHContacts() {return hContacts;}
    
    public final void updateContact(final String nick, final String jid, final String grpName, String subscr, boolean ask) {
        // called only on roster read
        int status=Presence.PRESENCE_OFFLINE;
        if (subscr.equals("none")) status=Presence.PRESENCE_UNKNOWN;
        if (ask) status=Presence.PRESENCE_ASK;
        //if (subscr.equals("remove")) status=Presence.PRESENCE_TRASH;
        if (subscr.equals("remove")) status=-1;
        
        Jid J=new Jid(jid);
        Contact c=findContact(J,false); // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ bare jid
        if (c==null) {
            c=new Contact(nick, jid, Presence.PRESENCE_OFFLINE, null);
            addContact(c);
        }
        for (Enumeration e=hContacts.elements();e.hasMoreElements();) {
            c=(Contact)e.nextElement();
            if (c.jid.equals(J,false)) {
                Group group= (c.jid.isTransport())? 
                    groups.getGroup(Groups.TYPE_TRANSP) :
                    groups.getGroup(grpName);
                if (group==null) {
                    group=groups.addGroup(grpName, true);
                }
                c.nick=nick;
                c.setGroup(group);
                c.subscr=subscr;
                c.offline_type=status;
                c.ask_subscribe=ask;
                //if (status==Presence.PRESENCE_TRASH) c.status=status;
                //if (status!=Presence.PRESENCE_OFFLINE) c.status=status;
                c.setSortKey((nick==null)? jid:nick);
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
            countNewMsgs();
        }
    }

    private MucContact findMucContact(Jid jid) {
        Contact contact=findContact(jid, true);
        try {
            return (MucContact) contact;
        } catch (Exception e) {
            // drop buggy bookmark in roster
            hContacts.removeElement(contact);
            return null;
        }
    }
    
    public final ConferenceGroup initMuc(String from, String joinPassword){
        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();
        
        
        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);
        
        
        // creating room
        
        if (grp==null) // we hasn't joined this room yet
            groups.addGroup(grp=new ConferenceGroup(roomJid, room) );
        grp.password=joinPassword;
        
        MucContact c=findMucContact( new Jid(from.substring(0, rp)) );
        
        if (c==null) {
            c=new MucContact(room, roomJid);
            addContact(c);
        }
        c.status=Presence.PRESENCE_ONLINE;
        c.transport=RosterIcons.ICON_GROUPCHAT_INDEX; 
        c.bareJid=from;
        c.origin=Contact.ORIGIN_GROUPCHAT;
        c.commonPresence=true;
        //c.priority=99;
        //c.key1=0;
        grp.conferenceJoinTime=Time.localTime();
        grp.setConference(c);
        c.setGroup(grp);
        
        // creating self-contact
        String nick=from.substring(rp+1);
        c=grp.getSelfContact();
        if (c==null)
            c=findMucContact( new Jid(from) );

        if (c!=null) if (c.status==Presence.PRESENCE_OFFLINE) { 
            c.nick=nick;
            c.jid.setJid(from);
            c.bareJid=from;
        }
        
        if (c==null) {
            c=new MucContact(nick, from);
            addContact(c);
        }
        
        
        grp.setSelfContact(c);
        c.setGroup(grp);
        c.origin=Contact.ORIGIN_GC_MYSELF;
        
        sort(hContacts);
        return grp;
    }
    
    public final MucContact mucContact(String from){
        // muc message
        int ri=from.indexOf('@');
        int rp=from.indexOf('/');
        String room=from.substring(0,ri);
        String roomJid=from.substring(0,rp).toLowerCase();
        

        ConferenceGroup grp=(ConferenceGroup)groups.getGroup(roomJid);
	

        
        if (grp==null) return null; // we are not joined this room
        
        MucContact c=findMucContact( new Jid(from) );
        
        if (c==null) {
            c=new MucContact(from.substring(rp+1), from);
            addContact(c);
            c.origin=Contact.ORIGIN_GC_MEMBER;
        }
        
        c.setGroup(grp);
        sort(hContacts);
        return c;
    }
    
    public final Contact getContact(final String jid, boolean createInNIL) {
        
        Jid J=new Jid(jid);

        // проверим наличие по полной строке
        Contact c=findContact(J, true); 
        if (c!=null) 
            return c;

        // проверим наличие без ресурсов
        c=findContact(J, false);
        if (c==null) {
            if (!createInNIL) return null;
            c=new Contact(null, jid, Presence.PRESENCE_OFFLINE, "not-in-list");
	    c.bareJid=J.getBareJid();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(groups.getGroup(Groups.TYPE_NOT_IN_LIST));
            addContact(c);
        } else {
            // здесь jid с новым ресурсом
            if (c.origin==Contact.ORIGIN_ROSTER) {
                c.origin=Contact.ORIGIN_ROSTERRES;
                c.status=Presence.PRESENCE_OFFLINE;
                c.jid=J;
                //System.out.println("add resource");
            } else {
                c=c.clone(J, Presence.PRESENCE_OFFLINE);
                addContact(c);
                //System.out.println("cloned");
            }
        }
        sort(hContacts);
        return c;
    }
    
    public void addContact(Contact c) {
        synchronized (hContacts) { hContacts.addElement(c); }
    }
    

    public final Contact findContact(final Jid j, final boolean compareResources) {
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
        Contact c=selfContact();
        c.status=myStatus;
        sort(hContacts);
        
        reEnumRoster();
    }

    public void sendDirectPresence(int status, Contact to) {
        if (to==null) { 
            sendPresence(status);
            return;
        }
        ExtendedStatus es= StatusList.getInstance().getStatus(status);
        Presence presence = new Presence(status, es.getPriority(), es.getMessage());
        presence.setTo(to.getJid());
        if (theStream!=null) {
            theStream.send( presence );
        }
        if (to instanceof MucContact) ((MucContact)to).commonPresence=false;
    }

    
    public Contact selfContact() {
	return getContact(myJid.getJid(), true);
    }
    
    public void sendConferencePresence() {
        ExtendedStatus es= StatusList.getInstance().getStatus(myStatus);
        for (Enumeration e=hContacts.elements(); e.hasMoreElements();) {
            Contact c=(Contact) e.nextElement();
            if (c.origin!=Contact.ORIGIN_GROUPCHAT) continue;
            if (c.status==Presence.PRESENCE_OFFLINE) continue;
            if (!((MucContact)c).commonPresence) continue;
            Presence presence = new Presence(myStatus, es.getPriority(), es.getMessage());
            presence.setTo(c.getJid());
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
        Message message = new Message( 
                to.getJid(), 
                body, 
                subject, 
                groupchat 
        );
        if (groupchat && body==null /*&& subject==null*/) return;
        if (composingState>0) {
            JabberDataBlock event=new JabberDataBlock("x", null,null);
            event.setNameSpace("jabber:x:event");
            if (body==null) event.addChild(new JabberDataBlock("id",null, null));
            if (composingState==1) {
                event.addChild("composing", null);
            }
            message.addChild(event);
        }
        //System.out.println(simpleMessage.toString());
        theStream.send( message );
        lastMessageTime=Time.localTime();
    }
    
    private Vector vCardQueue;
    public void resolveNicknames(int transportIndex){
	vCardQueue=new Vector();
	for (Enumeration e=hContacts.elements(); e.hasMoreElements();){
	    Contact k=(Contact) e.nextElement();
	    if (k.jid.isTransport()) continue;
	    if (k.transport==transportIndex && k.nick==null && k.getGroupType()>=Groups.TYPE_COMMON) {
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

    public void loginFailed(String error){
        myStatus=Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 0);
        
        errorLog(error);
        
        reconnect=false;
        setQuerySign(false);
        redraw();
    }
    
    public void loginSuccess() {
        // enable File transfers
//#if (FILE_IO && FILE_TRANSFER)
            theStream.addBlockListener(TransferDispatcher.getInstance());
//#endif
        // залогинились. теперь, если был реконнект, то просто пошлём статус
        if (reconnect) {
            querysign=reconnect=false;
            sendPresence(myStatus);
            return;
        }
        
        // иначе будем читать ростер
        theStream.enableRosterNotify(true);
        rpercent=60;
        if (StaticData.getInstance().account.isMucOnly()) {
            setProgress(SR.MS_CONNECTED,100);
            try {
                reEnumRoster();
            } catch (Exception e) { e.printStackTrace(); }
            querysign=reconnect=false;
            SplashScreen.getInstance().close(); // display.setCurrent(this);
            
            //query bookmarks
            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
        } else {
            JabberDataBlock qr=new IqQueryRoster();
            setProgress(SR.MS_ROSTER_REQUEST, 60);
            theStream.send( qr );
        }
    }
    
    public void blockArrived( JabberDataBlock data ) {
        try {
            
            if( data instanceof Iq ) {
                String type = (String) data.getTypeAttribute();
                String id=(String) data.getAttribute("id");
                
                if (id!=null) {
                    if (id.startsWith("nickvc")) {
                        VCard vc=new VCard(data);//.getNickName();
                        String from=vc.getJid();
                        String nick=vc.getNickName();
                        
                        Contact c=findContact(new Jid(from), false);
                        
                        String group=(c.getGroupType()==Groups.TYPE_COMMON)?
                            null: c.getGroup().name;
                        if (nick!=null)  storeContact(from,nick,group, false);
                        //updateContact( nick, c.rosterJid, group, c.subscr, c.ask_subscribe);
                        sendVCardReq();
                    }
                    
                    if (id.startsWith("getvc")) {
                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        Contact c=getContact(vcard.getJid(), true);
                        if (c!=null) {
                            c.vcard=vcard;
                            new vCardForm(display, vcard, c.getGroupType()==Groups.TYPE_SELF);
                        }
                    }
                    
                    if (id.equals("getver")) {
                        String from=data.getAttribute("from");
                        String body=null;
                        if (type.equals("error")) {
                            body=SR.MS_NO_VERSION_AVAILABLE;
                            querysign=false;
                        } else if (type.equals("result")) {
                            JabberDataBlock vc=data.getChildBlock("query");
                            if (vc!=null) {
                                body=IqVersionReply.dispatchVersion(vc);
                            }
                            querysign=false;
                        }
                        
                        Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, SR.MS_CLIENT_INFO, body);
                        if (body!=null) { 
                            messageStore(m);
                            redraw();
                        }
                    }
                    
                } // id!=null
                if ( type.equals( "result" ) ) {
                    if (id.equals("getros")) {
                        // а вот и ростер подошёл :)
                        theStream.enableRosterNotify(false);

                        processRoster(data);
                        
                        setProgress(SR.MS_CONNECTED,100);
                        reEnumRoster();
                        // теперь пошлём присутствие
                        querysign=reconnect=false;
                        sendPresence(myStatus);
                        //sendPresence(Presence.PRESENCE_INVISIBLE);
                        
                        SplashScreen.getInstance().close(); // display.setCurrent(this);
                        
                        //loading bookmarks
                        //if (cf.autoJoinConferences)
                            theStream.addBlockListener(new BookmarkQuery(BookmarkQuery.LOAD));
                    } 
                    
                } else if (type.equals("get")){
                    JabberDataBlock query=data.getChildBlock("query");
                    if (query!=null){
                        // проверяем на запрос версии клиента
                        if (query.isJabberNameSpace("jabber:iq:version"))
                            theStream.send(new IqVersionReply(data));
                        // проверяем на запрос локального времени клиента
                        else if (query.isJabberNameSpace("jabber:iq:time"))
                            theStream.send(new IqTimeReply(data));
                        // проверяем на запрос idle
                        else if (query.isJabberNameSpace("jabber:iq:last"))
                            theStream.send(new IqLast(data, lastMessageTime));
                        else replyError(data);
                    }
                } else if (type.equals("set")) {
                    processRoster(data);
                    reEnumRoster();
                }
            }
            
            // If we've received a message
            
            else if( data instanceof Message ) {
                querysign=false;
                boolean highlite=false;
                Message message = (Message) data;
                
                String from=message.getFrom();
                String body=message.getBody().trim();    
                String oob=message.getOOB();
                if (oob!=null) body+=oob;
                if (body.length()==0) body=null; 
                String subj=message.getSubject().trim(); if (subj.length()==0) subj=null;
                String tStamp=message.getTimeStamp();
		
                int start_me=-1;    //  не добавлять ник
                String name=null;
                boolean groupchat=false;
                
                try { // type=null
		    String type=message.getTypeAttribute();
                    if (type.equals("groupchat")) {
                        groupchat=true;
                        start_me=0; // добавить ник в начало
                        int rp=from.indexOf('/');
                        
                        name=from.substring(rp+1);
                        
                        if (rp>0) from=from.substring(0, rp);
                        
                        // subject
                        if (subj!=null) {
                            if (body==null) body=subj;
                            subj=null;
                            start_me=-1; // не добавлять /me к subj
                            highlite=true;
                        }
                    }
                    if (type.equals("error")) {
                        
                        String errCode=message.getChildBlock("error").getAttribute("code");
                        
                        switch (Integer.parseInt(errCode)) {
                            case 403: body=SR.MS_VIZITORS_FORBIDDEN; break;
                            case 503: break;
                            default: body=SR.MS_ERROR_+message.getChildBlock("error")+"\n"+body;
                        }
                    }
                } catch (Exception e) {}
                
                try {
                    JabberDataBlock xmlns=message.findNamespace("http://jabber.org/protocol/muc#user");
                    String password=xmlns.getChildBlockText("password");
                    
                    JabberDataBlock invite=xmlns.getChildBlock("invite");
                    String inviteFrom=invite.getAttribute("from");
                    String inviteReason=invite.getChildBlockText("reason");
                            
                    String room=from+'/'+sd.account.getNickName();
                    initMuc(room, password);
                    
                    body=inviteFrom+SR.MS_IS_INVITING_YOU+from+" ("+inviteReason+')';
                    
                } catch (Exception e) {}
                
                Contact c=getContact(from, true);

                if (name==null) name=c.getName();
                // /me

                if (body!=null) {
                    if (body.startsWith("/me ")) start_me=3;
                    if (start_me>=0) {
                        StringBuffer b=new StringBuffer(name);
                        if (start_me==0) b.append("> ");
                        b.append(body.substring(start_me));
                        body=b.toString();
                    }
                }
                
                boolean compose=false;
                JabberDataBlock x=message.getChildBlock("x");
                //if (body.length()==0) body=null; 
                
                if (x!=null) {
                    compose=(x.getChildBlock("composing")!=null);
                    if (compose) c.acceptComposing=true;
                    if (body!=null) compose=false;
                    c.setComposing(compose);
                }
                redraw();

                if (body==null) return;
                
                Msg m=new Msg(Msg.MESSAGE_TYPE_IN, from, subj, body);
                if (tStamp!=null) 
                    m.dateGmt=Time.dateIso8601(tStamp);
                if (groupchat) {
                    if (c.bareJid.equals(message.getFrom())) {
                        m.messageType=Msg.MESSAGE_TYPE_OUT;
                        m.unread=false;
                    } else {
                        ConferenceGroup mucGrp=(ConferenceGroup)c.getGroup();
                        if (m.dateGmt<= ((ConferenceGroup)c.getGroup()).conferenceJoinTime) m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                        // highliting messages with myNick substring
                        String myNick=mucGrp.getSelfContact().getName();
                        highlite |= body.indexOf(myNick)>-1;
                        //TODO: custom highliting dictionary
                    } 
                }
                m.setHighlite(highlite);  
                messageStore(m);
                //Contact c=getContact(from);
                //c.msgs.addElement(m);
                //countNewMsgs();
                //setFocusTo(c);
                //redraw();
            }
            // присутствие

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
                
                JabberDataBlock xmuc=pr.findNamespace("http://jabber.org/protocol/muc");
                if (xmuc!=null) try {
                    MucContact c = mucContact(from);
                    
//toon
//                   String statusText=status.getChildBlockText("status"); 
//toon                    
                    
                    //System.out.println(b.toString());


                    //c.nick=nick;
                    
                    from=from.substring(0, from.indexOf('/'));
                    Msg chatPresence=new Msg(
                           Msg.MESSAGE_TYPE_PRESENCE,
                           from,
                           null,
                           c.processPresence(xmuc, pr) );
                    if (cf.storeConfPresence) {
                        messageStore(chatPresence);
                    }
                    
                    c.addMessage(m);
                    c.priority=pr.getPriority();
                    if (ti>=0) c.status=ti;
                    
                } /* if (muc) */ catch (Exception e) { /*e.printStackTrace();*/ }
                else {
                    Contact c=getContact(m.from, false); 
                    if (c==null) return; // drop presence
                    messageStore(c, m);
                    c.priority=pr.getPriority();
                    if (ti>=0) c.status=ti;
                    if (ti==Presence.PRESENCE_OFFLINE) c.acceptComposing=false;
                    c.setComposing(false);
                }
		sort(hContacts);
                reEnumRoster();
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    
    void replyError (JabberDataBlock stanza) {
        stanza.setAttribute("to", stanza.getAttribute("from"));
        stanza.setAttribute("from", null);
        stanza.setTypeAttribute("error");
        JabberDataBlock error=stanza.addChild("error", null);
        error.setTypeAttribute("cancel");
        error.addChild("feature-not-implemented",null);
        theStream.send(stanza);
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

                    // найдём группу
                    String group=i.getChildBlockText("group");
                    if (group.length()==0) group=Groups.COMMON_GROUP;

                    // так можно проверить, когда пришёл jabber:iq:roster,
                    // на запрос ростера или при обновлении

                    //String iqType=data.getTypeAttribute();
                    //if (iqType.equals("set")) type=1;

                    updateContact(name,jid,group, subscr, ask);
                    sort(hContacts);
                }
            
            }
    }
    
    
    void messageStore(Contact c, Msg message) {
        if (c==null) return;  
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); 

        if (!message.unread) return;
        //TODO: clear unread flag if not-in-list IS HIDDEN
        
        if (countNewMsgs()) reEnumRoster();
        
        if (c.getGroupType()==Groups.TYPE_IGNORE) return;    // no signalling/focus on ignore
        
	if (cf.popupFromMinimized)
	    Bombus.getInstance().hideApp(false);
	
        if (cf.autoFocus) focusToContact(c, false);

        if (message.messageType!=Msg.MESSAGE_TYPE_HISTORY) 
            playNotify(0);
    }
    
    public void playNotify(int event) {
        String message=cf.messagesnd;
	String type=cf.messageSndType;
	int volume=cf.soundVol;
        int profile=cf.profile;
        if (profile==AlertProfile.AUTO) profile=AlertProfile.ALL;
        
        EventNotify notify=null;
        
        boolean blFlashEn=cf.blFlash;   // motorola e398 backlight bug
        
        switch (profile) {
            case AlertProfile.ALL:   notify=new EventNotify(display, type, message, cf.vibraLen, blFlashEn); break;
            case AlertProfile.NONE:  notify=new EventNotify(display, null, null,    0,           false    ); break;
            case AlertProfile.VIBRA: notify=new EventNotify(display, null, null,    cf.vibraLen, blFlashEn); break;
            case AlertProfile.SOUND: notify=new EventNotify(display, type, message, 0,           blFlashEn); break;
        }
        if (notify!=null) notify.startNotify();
    }
    
    
    Contact messageStore(Msg message){
        Contact c=getContact(message.from, true);
        if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) 
            if (!cf.notInList) return c;

        messageStore(c, message);
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
        //setProgress(SR.MS_LOGINPGS, 42);
        
//#if SASL
        if (sd.account.isSASL()) {
            new SASLAuth(sd.account, SessionId, this, theStream)
  //#if SASL_XGOOGLETOKEN
            .setToken(token)
  //#endif
            ;
   
        } else {
            new NonSASLAuth(sd.account, SessionId, this, theStream);
        }
//#else
//#         new NonSASLAuth(sd.account, SessionId, this, theStream);
//#endif
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
            String error=e.getClass().getName()+"\n"+e.getMessage();
            errorLog(error);
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

    protected void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
//#if (MOTOROLA_BACKLIGHT)
        if (cf.ghostMotor) {
            // backlight management
            if (keyCode=='*') blState=(blState==1)? Integer.MAX_VALUE : 1;
            else blState=Integer.MAX_VALUE;
            
            display.flashBacklight(blState);
        }
//#endif
    }
    

    public void userKeyPressed(int keyCode){
        if (keyCode==KEY_NUM0 /* || keyCode==MOTOE680_REALPLAYER  CONFLICT WITH ALCATEL. (platform=J2ME)*/) {
            if (messageCount==0) return;
            Object atcursor=getFocusedObject();
            Contact c=null;
            if (atcursor instanceof Contact) c=(Contact)atcursor;
            // а если курсор на группе, то искать с самого начала.
            else c=(Contact)hContacts.firstElement();
            
            Enumeration i=hContacts.elements();
            
            int pass=0; // 0=ищем курсор, 1=ищем
            while (pass<2) {
                if (!i.hasMoreElements()) i=hContacts.elements();
                Contact p=(Contact)i.nextElement();
                if (pass==1) if (p.getNewMsgsCount()>0) { 
		    focusToContact(p, true);
                    setRotator();
                    break; 
                }
                if (p==c) pass++; // полный круг пройден
            }
        }

        if (keyCode=='3') searchGroup(-1);
	if (keyCode=='9') searchGroup(1);
        
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
        
        if (c==cmdActiveContact) { new ActiveContacts(display, null); }
        
        if (c==cmdAccount){ new AccountSelect(display, false); }
        if (c==cmdStatus) { new StatusSelect(display, null); }
        if (c==cmdAlert) { new AlertProfile(display); }
        if (c==cmdArchive) { new ArchiveList(display, null); }
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        
        if (c==cmdTools) { new RosterToolsMenu(display); }
        // stream-sensitive commands
        // check for closed socket
        if (StaticData.getInstance().roster.theStream==null) return;
        
        if (c==cmdConference) { new ConferenceForm(display); }
        if (c==cmdActions) try { 
            new RosterItemActions(display, getFocusedObject()); 
        } catch (Exception e) { /* NullPointerException */ }
        
        if (c==cmdAdd) {
            //new MIDPTextBox(display,"Add to roster", null, new AddContact());
            Object o=getFocusedObject();
            Contact cn=null;
            if (o instanceof Contact) {
                cn=(Contact)o;
                if (cn.getGroupType()!=Groups.TYPE_NOT_IN_LIST && cn.getGroupType()!=Groups.TYPE_SEARCH_RESULT) cn=null;
            }
            if (o instanceof MucContact) { cn=(Contact)o; }
            new ContactEdit(display, cn);
        }
    }
    

    public void reEnterRoom(Group group) {
	ConferenceGroup confGroup=(ConferenceGroup)group;
        String confJid=confGroup.getSelfContact().getJid();
        int roomEnd=confJid.indexOf('@');
        String room=confJid.substring(0, roomEnd);
        int serverEnd=confJid.indexOf('/');
        String server=confJid.substring(roomEnd+1,serverEnd);
        String nick=confJid.substring(serverEnd+1);
        
        new ConferenceForm(display, room, server, nick, confGroup.password);
        //sendPresence(confGroup.getSelfContact().getJid(), null, null);

	//confGroup.getConference().status=Presence.PRESENCE_ONLINE;
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

        if (keyCode==cf.keyVibra || keyCode==MOTOE680_FMRADIO /* TODO: redefine keyVibra*/) {
            // swap profiles
            int profile=cf.profile;
            cf.profile=(profile==AlertProfile.VIBRA)? 
                cf.lastProfile : AlertProfile.VIBRA;
            cf.lastProfile=profile;
            
            updateTitle();
            redraw();
        }
        
        if (keyCode==cf.keyOfflines /* || keyCode==MOTOE680_REALPLAYER CONFLICT WITH ALCATEL. (platform=J2ME) 
         TODO: redifine keyOfflines*/) {
            cf.showOfflineContacts=!cf.showOfflineContacts;
            reEnumRoster();
        }

       	if (keyCode==KEY_NUM3) new ActiveContacts(display, null);

        if (keyCode==cf.keyHide && cf.allowMinimize) {
            Bombus.getInstance().hideApp(true);
        }
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
	
	if (c.getGroupType()==Groups.TYPE_NOT_IN_LIST) {
	    hContacts.removeElement(c);
            countNewMsgs();
	    reEnumRoster();
	} else
	    theStream.send(new IqQueryRoster(c.getBareJid(),null,null,"remove"));
    }
   
    
    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateTitle();
    }
    /**
     * store cotnact on server
     */
    public void storeContact(String jid, String name, String group, boolean askSubscribe){
        
        theStream.send(new IqQueryRoster(jid, name, group, null));
        if (askSubscribe) theStream.send(new Presence(jid,"subscribe"));
    }

    public void loginMessage(String msg) {
        setProgress(msg, 42);
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
                    Group selfContactGroup=groups.getGroup(Groups.TYPE_SELF);
                    if (cf.selfContact || selfContactGroup.tonlines>1 || selfContactGroup.unreadMessages>0 )
                        groups.addToVector(tContacts, Groups.TYPE_SELF);
                    // adding groups
                    for (i=Groups.TYPE_COMMON;i<groups.getCount();i++)
                        groups.addToVector(tContacts,i);
                    // hiddens
                    if (cf.ignore) groups.addToVector(tContacts,Groups.TYPE_IGNORE);
                    // not-in-list
                    if (cf.notInList) groups.addToVector(tContacts,Groups.TYPE_NOT_IN_LIST);

                    // transports
                    Group transpGroup=groups.getGroup(Groups.TYPE_TRANSP);
                    if (cf.showTransports || transpGroup.unreadMessages>0)
                        groups.addToVector(tContacts,Groups.TYPE_TRANSP);
                    
                    // search result
                    //if (groups.getGroup(Groups.SRC_RESULT_INDEX).tncontacts>0)
                    groups.addToVector(tContacts, Groups.TYPE_SEARCH_RESULT);
                    
                    vContacts=tContacts;
                    
                    setRosterTitle("("+groups.getRosterOnline()+"/"+groups.getRosterContacts()+")");
                    
                    //resetStrCache();
                    if (cursor<0) cursor=0;
                    
                    // вернём курсор на прежний элемент
                    if ( locCursor==cursor && focused!=null ) {
                        int c=vContacts.indexOf(focused);
                        if (c>=0) moveCursorTo(c, force);
			force=false;
                    }
                    //if (cursor>=vContacts.size()) cursor=vContacts.size()-1; //moveCursorEnd(); // вернём курсор из нирваны
                    
                    focusedItem(cursor);
                    redraw();
                }
            } catch (Exception e) {e.printStackTrace();}
            thread=null;
        }
    }
}

