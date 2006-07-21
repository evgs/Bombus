/*
 * Roster.java
 *
 * Created on 6 Январь 2005 г., 19:16
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

//TODO: упростить обработку исключений для theStream.send

package Client;

import Conference.ConferenceGroup;
import Conference.MucContact;
import Conference.QueryConfigForm;
import Conference.affiliation.Affiliations;
import archive.ArchiveList;
import images.RosterIcons;
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
    
    public Object messageIcon;
   
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
    private Command cmdAdd=new Command(SR.MS_ADD_CONTACT, Command.SCREEN, 4);
    private Command cmdAlert=new Command(SR.MS_ALERT_PROFILE_CMD, Command.SCREEN, 8);
    private Command cmdConference=new Command(SR.MS_CONFERENCE, Command.SCREEN, 10);
    private Command cmdArchive=new Command(SR.MS_ARCHIVE, Command.SCREEN, 10);
    private Command cmdTools=new Command(SR.MS_TOOLS, Command.SCREEN, 11);    
    private Command cmdAccount=new Command(SR.MS_ACCOUNT_, Command.SCREEN, 12);
    private Command cmdOptions=new Command(SR.MS_OPTIONS, Command.SCREEN, 20);
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
        addCommand(cmdConference);
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
                if (g.index>Groups.COMMON_INDEX) groups.removeGroup(g);
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
        Contact c=findContact(J,false); // пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅ bare jid
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
        c.transport=RosterIcons.ICON_GROUPCHAT_INDEX; //FIXME: убрать хардкод
        c.bareJid=from;
        c.origin=Contact.ORIGIN_GROUPCHAT;
        //c.priority=99;
        c.jidHash=0;
        grp.conferenceJoinTime=Time.localTime();
        grp.setConference(c);
        c.setGroup(grp);
        
        // creating self-contact
        c=grp.getSelfContact();
        if (c==null)
            c=findMucContact( new Jid(from) );
        
        if (c==null) {
            c=new MucContact(from.substring(rp+1), from);
            addContact(c);
        }
        
        grp.setSelfContact(c);
        c.setGroup(grp);
        c.origin=Contact.ORIGIN_GC_MYSELF;
        
        sort();
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
        sort();
        return c;
    }
    
    public final Contact getContact(final String jid) {
        
        Jid J=new Jid(jid);

        // проверим наличие по полной строке
        Contact c=findContact(J, true); 
        if (c!=null) 
            return c;

        // проверим наличие без ресурсов
        c=findContact(J, false);
        if (c==null) {
            // хм... нет такой буквы
            // здесь будем игнорить позже
            //System.out.println("new");
            c=new Contact(null, jid, Presence.PRESENCE_OFFLINE, "not-in-list");
	    c.bareJid=J.getBareJid();
            c.origin=Contact.ORIGIN_PRESENCE;
            c.setGroup(groups.getGroup(Groups.NIL_INDEX));
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
        return (findContact(new Jid(Jid), compareResources));
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
        Contact c=getContact(myJid.getJid());
        c.status=myStatus;
        sort();
        
        reEnumRoster();
    }
    
    public Contact selfContact() {
	return getContact(myJid.getJid());
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

    public void loginFailed(String error){
        myStatus=Presence.PRESENCE_OFFLINE;
        setProgress(SR.MS_LOGIN_FAILED, 0);
        
        errorLog(error);
        
        reconnect=false;
        setQuerySign(false);
        redraw();
    }
    
    public void loginSuccess() {
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
                /*if ( type.equals( "error" ) ) {
                    if (id.equals("auth-s")) {
                        // Authorization error
                        JabberDataBlock err=data.getChildBlock("error");
                        loginFailed(err.toString());
                    }
                }*/
                
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
                    if (id.equals("getros")) {
                        // а вот и ростер подошёл :)
                        //SplashScreen.getInstance().setProgress(95);
                        
                        theStream.enableRosterNotify(false);

                        processRoster(data);
                        
                        setProgress(SR.MS_CONNECTED,100);
                        reEnumRoster();
                        // теперь пошлём присутствие
                        querysign=reconnect=false;
                        sendPresence(myStatus);
                        //sendPresence(Presence.PRESENCE_INVISIBLE);
                        
                        SplashScreen.getInstance().close(); // display.setCurrent(this);
                        
                    }
                    if (id.startsWith("getvc")) {
                        setQuerySign(false);
                        VCard vcard=new VCard(data);
                        Contact c=getContact(vcard.getJid());
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
                        // проверяем на запрос версии клиента
                        if (query.isJabberNameSpace("jabber:iq:version"))
                            //String xmlns=query.getAttribute("xmlns");
                            //if (xmlns!=null) if (xmlns.equals("jabber:iq:version"))
                            theStream.send(new IqVersionReply(data));
                        // проверяем на запрос локального времени клиента
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
                String body=message.getBody().trim();    if (body.length()==0) body=null; 
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
                        }
                    }
		    if (type.equals("error")) {
//toon                  
                        
                        String mucErrCode=message.getChildBlock("error").getAttribute("code");
                        
                        if ( mucErrCode.equals("403") ) {
                            body=SR.MS_VIZITORS_FORBIDDEN;
                        } else                         
                            body=SR.MS_ERROR_+message.getChildBlock("error")+"\n"+body;
                  
//toon                        
		    }
                } catch (Exception e) {}
                
                try {
                    //TODO: invitations
                    JabberDataBlock xmlns=message.findNamespace("http://jabber.org/protocol/muc#user");
                    String password=xmlns.getChildBlockText("password");
                    
                    JabberDataBlock invite=xmlns.getChildBlock("invite");
                    String inviteFrom=invite.getAttribute("from");
                    String inviteReason=invite.getChildBlockText("reason");
                            
                    String room=from+'/'+sd.account.getNickName();
                    initMuc(room, password);
                    
                    body=inviteFrom+SR.MS_IS_INVITING_YOU+from+" ("+inviteReason+')';
                    
                } catch (Exception e) {}
                
                Contact c=getContact(from);

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
                    if (compose) c.accept_composing=true;
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
                        if (m.dateGmt<= ((ConferenceGroup)c.getGroup()).conferenceJoinTime) m.messageType=Msg.MESSAGE_TYPE_HISTORY;
                    } 
                }
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
                    Contact c=messageStore(m);
                    c.priority=pr.getPriority();
                    if (ti>=0) c.status=ti;
                }
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

                    // найдём группу
                    String group=i.getChildBlockText("group");
                    if (group.length()==0) group=Groups.COMMON_GROUP;

                    // так можно проверить, когда пришёл jabber:iq:roster,
                    // на запрос ростера или при обновлении

                    //String iqType=data.getTypeAttribute();
                    //if (iqType.equals("set")) type=1;

                    updateContact(name,jid,group, subscr, ask);
                }
            
            }
    }
    
    
    Contact messageStore(Msg message){
        Contact c=getContact(message.from);
        if (c.getGroupIndex()==Groups.NIL_INDEX) 
            if (!cf.notInList) return c;

        if (c==null) return c;  // not to store/signal not-in-list message
        c.addMessage(message);
        
        if (cf.ghostMotor) System.gc(); //TODO: тест для моторол
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
	
        if (cf.autoFocus) focusToContact(c, false);

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
                    break; 
                }
                if (p==c) pass++; // полный круг пройден
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
//#         if (c.getLabel().charAt(0)>127) theStream=null; // 8==o ()() fuck translations
//#endif
        
        if (c==cmdAccount){ new AccountSelect(display, false); }
        if (c==cmdStatus) { new StatusSelect(display); }
        if (c==cmdAlert) { new AlertProfile(display); }
        if (c==cmdOptions){ new ConfigForm(display); }
        if (c==cmdArchive) { new ArchiveList(display, null); }
        if (c==cmdInfo) { new Info.InfoWindow(display); }
        
        // stream-sensitive commands
        // check for closed socket
        if (StaticData.getInstance().roster.theStream==null) return;
        
        if (c==cmdConference) { new ConferenceForm(display); }
        if (c==cmdActions) try { 
            new RosterItemActions(display, getFocusedObject()); 
        } catch (Exception e) { /* NullPointerException */ }
        
        if (c==cmdTools) { new RosterToolsMenu(display); }
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

        if (keyCode==cf.keyHide && cf.allowMinimize) {
            Bombus.getInstance().hideApp(true);
        }
    }

    protected void keyPressed(int keyCode){
	super.keyPressed(keyCode);
	if (keyCode=='3') searchGroup(-1);
	if (keyCode=='9') searchGroup(1);
        
//#if (MOTOROLA_BACKLIGHT)
        if (cf.ghostMotor) {
            // backlight management
            if (keyCode=='*') blState=(blState==0)? Integer.MAX_VALUE : 1;
            else blState=Integer.MAX_VALUE;
            
            display.flashBacklight(blState);
        }
//#endif
	
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
   
    
    public void setQuerySign(boolean requestState) {
        querysign=requestState;
        updateTitle();
    }
    void setMucMod(Contact contact, Hashtable itemAttributes){
        JabberDataBlock iq=new Iq(contact.jid.getBareJid(), Iq.TYPE_SET, "itemmuc");
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
                    Group selfContactGroup=groups.getGroup(Groups.SELF_INDEX);
                    if (cf.selfContact || selfContactGroup.tonlines>1 || selfContactGroup.unreadMessages>0 )
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
                    
                    // вернём курсор на прежний элемент
                    if ( locCursor==cursor && focused!=null ) {
                        int c=vContacts.indexOf(focused);
                        if (c>=0) moveCursorTo(c, force);
			force=false;
                    }
                    if (cursor>=vContacts.size()) moveCursorEnd(); // вернём курсор из нирваны
                    
                    focusedItem(cursor);
                    redraw();
                }
            } catch (Exception e) {e.printStackTrace();}
            thread=null;
        }
    }
}

