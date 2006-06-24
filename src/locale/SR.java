/*
 * SR.java
 *
 * Created on March 19, 2006, 3:06 PM
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 * (c) Alexey V.Lubimov <avl@l14.ru>, 2006.
 */

package locale;

import Client.Config;
import java.util.Hashtable;
import util.StringLoader;

public class SR {
    
    public   static String MS_JID = loadString( "Jid" );
    public   static String MS_LOADING = loadString( "Loading" );
    public   static String MS_PRIVACY_LISTS = loadString( "Privacy Lists" );
    public   static String MS_EXISTING_GROUPS = loadString( "Existing groups" );
    public   static String MS_MESSAGE_FONT = loadString( "Message font" );
    public   static String MS_ROSTER_FONT = loadString( "Roster font" );
    public   static String MS_PASTE_BODY = loadString( "Paste Body" );
    public   static String MS_CONFIG_ROOM = loadString( "Configure Room" );
    public   static String MS_PASTE_SUBJECT = loadString( "Paste Subject" );
    public   static String MS_DISCO = loadString( "Service Discovery" );
    public   static String MS_USER_JID = loadString( "User JID" );
    public   static String MS_NEW_LIST = loadString( "New list" );
    public   static String MS_NOLOGIN = loadString( "Select (no login)" );
    public   static String MS_PRIVACY_RULE = loadString( "Privacy rule" );
    public   static String MS_SSL = loadString( "use SSL" );
    public   static String MS_MODIFY = loadString( "Modify" );
    public   static String MS_UPDATE = loadString( "Update" );
    public   static String MS_ACCOUNT_NAME = loadString( "Account name" );
    public   static String MS_GMT_OFFSET = loadString( "GMT offset" );
    public   static String MS_TIME_SETTINGS = loadString( "Time settings (hours)" );
    public   static String MS_CONNECTED = loadString( "Connected" );
    public   static String MS_CONNECT_TO = loadString( "Connect to " );
    public   static String MS_ALERT_PROFILE = loadString( "Alert Profile" );
    public   static String MS_MOVE_UP = loadString( "Move Up" );
    public   static String MS_OWNERS = loadString( "Owners" );
    public   static String MS_OK = loadString( "Ok" );
    public   static String MS_APP_MINIMIZE = loadString( "Minimize" );
    public   static String MS_ROOM = loadString( "Room" );
    public   static String MS_MESSAGES = loadString( "Messages" );
    public   static String MS_REFRESH = loadString( "Refresh" );
    public   static String MS_RESOLVE_NICKNAMES = loadString( "Resolve Nicknames" );
    public   static String MS_PRIVACY_ACTION = loadString( "Action" );
    public   static String MS_BAN = loadString( "Ban" );
    public   static String MS_LEAVE_ROOM = loadString( "Leave Room" );
    public   static String MS_PASSWORD = loadString( "Password" );
    public   static String MS_ITEM_ACTIONS = loadString( "Actions >" );
    public   static String MS_ACTIVATE = loadString( "Activate" );
    public   static String MS_AFFILIATION = loadString( "Affiliation" );
    public   static String MS_ACCOUNTS = loadString( "Accounts" );
    public   static String MS_DELETE_LIST = loadString( "Delete list" );
    public   static String MS_ACCOUNT_= loadString( "Account >" );
    public   static String MS_SHOWOFFLINES = loadString( "Show Offlines" );
    public   static String MS_SELECT = loadString( "Select" );
    public   static String MS_SUBJECT = loadString( "Subject" );
    public   static String MS_GROUP_MENU = loadString( "Group menu" );
    public   static String MS_APP_QUIT = loadString( "Quit" );
    public   static String MS_ROSTERADD = loadString( "Add to roster" );
    public   static String MS_EDIT_LIST = loadString( "Edit list" );
    public   static String MS_REGISTERING = loadString( "Registering" );
    public   static String MS_DONE = loadString( "Done" );
    public   static String MS_ERROR_ = loadString( "Error: " );
    public   static String MS_BROWSE = loadString( "Browse" );
    public   static String MS_SAVE_LIST = loadString( "Save list" );
    public   static String MS_KEEPALIVE_PERIOD = loadString( "Keep-Alive period" );
    public   static String MS_NEWGROUP = loadString( "<New Group>" );
    public   static String MS_SEND = loadString( "Send" );
    public   static String MS_PRIORITY = loadString( "Priority" );
    public   static String MS_FAILED = loadString( "Failed" );
    public   static String MS_SET_PRIORITY = loadString( "Set Priority" );
    public   static String MS_DELETE_RULE = loadString( "Delete rule" );
    public   static String MS_IGNORE_LIST = loadString( "Ignore-List" );
    public   static String MS_ROSTER_REQUEST = loadString( "Roster request" );
    public   static String MS_PRIVACY_TYPE = loadString( "Type" );
    public   static String MS_NAME = loadString( "Name" );
    public   static String MS_USERNAME = loadString( "Username" );
    public   static String MS_FULLSCREEN = loadString( "fullscreen" );
    public   static String MS_ALL_PRIORITIES = loadString( "All Priorities" );
    public   static String MS_ADD_BOOKMARK = loadString( "Add bookmark" );
    public   static String MS_CONFERENCES_ONLY = loadString( "conferences only" );
    public   static String MS_CLIENT_INFO = loadString( "Client Version" );
    public   static String MS_DISCARD = loadString( "Discard Search" );
    public   static String MS_SEARCH_RESULTS = loadString( "Search Results" );
    public   static String MS_GENERAL = loadString( "General" );
    public   static String MS_MEMBERS = loadString( "Members" );
    public   static String MS_ADD_CONTACT = loadString( "Add Contact" );
    public   static String MS_SUBSCRIPTION = loadString( "Subscription" );
    public   static String MS_STATUS_MENU = loadString( "Status >" );
    public   static String MS_JOIN = loadString( "Join" );
    public   static String MS_STARTUP_ACTIONS = loadString( "Startup actions" );
    public   static String MS_SERVER = loadString( "Server" );
    public   static String MS_ADMINS = loadString( "Admins" );
    public   static String MS_MK_ILIST = loadString( "Make Ignore-List" );
    public   static String MS_OPTIONS = loadString( "Options" );
    public   static String MS_DELETE = loadString( "Delete" );
    public   static String MS_DELETE_ASK = loadString( "Delete contact?" );
    public   static String MS_SUBSCRIBE = loadString( "Authorize" );
    public   static String MS_NICKNAMES = loadString( "Nicknames" );
    public   static String MS_ENT_SETUP = loadString( "Entering setup" );
    public   static String MS_ADD_ARCHIVE = loadString( "to Archive" );
    public   static String MS_BACK = loadString( "Back" );
    public   static String MS_HEAP_MONITOR = loadString( "heap monitor" );
    public   static String MS_HIDE_SPLASH = loadString( "Hide Splash" );
    public   static String MS_MESSAGE = loadString( "Message" );
    public   static String MS_OTHER = loadString( "<Other>" );
    public   static String MS_HISTORY = loadString( "history -" );
    public   static String MS_APPEND = loadString( "Append" );
    public   static String MS_ACTIVE_CONTACTS = loadString( "Active Contacts" );
    public   static String MS_SELECT_NICKNAME = loadString( "Select nickname" );
    public   static String MS_GROUP = loadString( "Group" );
    public   static String MS_JOIN_CONFERENCE = loadString( "Join conference" );
    public   static String MS_NO = loadString( "No" );
    public   static String MS_REENTER = loadString( "Re-Enter Room" );
    public   static String MS_NEW_MESSAGE = loadString( "New Message" );
    public   static String MS_ADD = loadString( "Add" );
    public   static String MS_LOGON = loadString( "Logon" );
    public   static String MS_LOGINPGS = loadString( "Login in progress" );
    public   static String MS_STANZAS = loadString( "Stanzas" );
    public   static String MS_AT_HOST = loadString( "at Host" );
    public   static String MS_AUTO_CONFERENCES = loadString( "join conferences" );
    public   static String MS_STATUS = loadString( "Status" );
    public   static String MS_SMILES_TOGGLE = loadString( "Smiles" );
    public   static String MS_CONTACT = loadString( "Contact >" );
    public final static String MS_SLASHME = "/me";
    public   static String MS_ORDER = loadString( "Order" );
    public   static String MS_OFFLINE_CONTACTS = loadString( "offline contacts" );
    public   static String MS_TRANSPORT = loadString( "Transport" );
    public   static String MS_COMPOSING_EVENTS = loadString( "composing events" );
    public   static String MS_ADD_SMILE = loadString( "Add Smile" );
    public   static String MS_NICKNAME = loadString( "Nickname" );
    public   static String MS_REVOKE_VOICE = loadString( "Revoke Voice" );
    public   static String MS_NOT_IN_LIST = loadString( "Not-in-list" );
    public   static String MS_COMMANDS = loadString( "Commands" );
    public   static String MS_CHSIGN = loadString( "- (Sign)" );
    public   static String MS_SETDEFAULT = loadString( "Set default" );
    public   static String MS_BANNED = loadString( "Outcasts (Ban)" );
    public   static String MS_SET_AFFILIATION = loadString( "Set affiliation to" );
    public   static String MS_HIDE_OFFLINES = loadString( "Hide Offlines" );
    public   static String MS_REGISTER_ACCOUNT = loadString( "Register Account" );
    public   static String MS_AUTOLOGIN = loadString( "autologin" );
    public   static String MS_LOGOFF = loadString( "Logoff" );
    public   static String MS_PUBLISH = loadString( "Publish" );
    public   static String MS_SUBSCR_REMOVE = loadString( "Remove subscription" );
    public   static String MS_SET = loadString( "Set" );
    public   static String MS_APPLICATION = loadString( "Application" );
    public   static String MS_BOOKMARKS = loadString( "Bookmarks" );
    public   static String MS_TEST_SOUND = loadString( "Test sound" );
    public   static String MS_STARTUP = loadString( "Startup" );
    public   static String MS_EDIT_RULE = loadString( "Edit rule" );
    public   static String MS_CANCEL = loadString( "Cancel" );
    public   static String MS_CLOSE = loadString( "Close" );
    public   static String MS_ARCHIVE = loadString( "Archive" );
    public   static String MS_FREE = loadString( "free " );
    public   static String MS_CONFERENCE = loadString( "Conference" );
    public   static String MS_SOUND = loadString( "Sound" );
    public   static String MS_LOGIN_FAILED = loadString( "Login failed" );
    public   static String MS_DISCOVER = loadString( "Browse" ); //"Discover"
    public   static String MS_NEW_JID = loadString( "New Jid" );
    public   static String MS_PLAIN_PWD = loadString( "plain-text password" );
    public   static String MS_PASTE_NICKNAME = loadString( "Paste Nickname" );
    public   static String MS_KICK = loadString( "Kick" );
    public   static String MS_CLEAR_LIST = loadString( "Clear List" );
    public   static String MS_GRANT_VOICE = loadString( "Grant Voice" );
    public   static String MS_MOVE_DOWN = loadString( "Move Down" );
    public   static String MS_QUOTE = loadString( "Quote" );
    public   static String MS_ROSTER_ELEMENTS = loadString( "Roster elements" );
    public   static String MS_ENABLE_POPUP = loadString( "popup from background" );
    public   static String MS_SMILES = loadString( "smiles" );
    public   static String MS_ABOUT = loadString( "About" );
    public   static String MS_RESOURCE = loadString( "Resource" );
    public   static String MS_DISCONNECTED = loadString( "Disconnected" );
    public   static String MS_EDIT = loadString( "Edit" );
    public   static String MS_HOST_IP = loadString( "Host name/IP (optional)" );
    public   static String MS_ADD_RULE = loadString( "Add rule" );
    public   static String MS_ALL_STATUSES = loadString( "for all status types" );
    public   static String MS_PASTE_JID = loadString( "Paste Jid" );
    public   static String MS_GOTO_URL = loadString( "Goto URL" );
    public   static String MS_CLOCK = loadString( "Clock -" );
    public   static String MS_LOGIN = loadString( "Login" );
    public   static String MS_CLOCK_OFFSET = loadString( "Clock offset" );
    public   static String MS_YES = loadString( "Yes" );
    public   static String MS_FLASHBACKLIGHT = loadString( "flash backlight" );
    public   static String MS_SUSPEND = loadString( "Suspend" );
    public   static String MS_ALERT_PROFILE_CMD = loadString( "Alert Profile >" );
    public   static String MS_MY_VCARD = loadString( "My vCard" );
    public   static String MS_TRANSPORTS = loadString( "transports" );
    public   static String MS_NEW_ACCOUNT = loadString( "New Account" );
    public   static String MS_SELF_CONTACT = loadString( "self-contact" );
    public   static String MS_VCARD = loadString( "vCard" );
    public   static String MS_SET_SUBJECT = loadString( "Set Subject" );
    public   static String MS_TOOLS = loadString( "Tools" );
    public   static String MS_JABBER_TOOLS = loadString( "Jabber Tools" );
    public   static String MS_PORT = loadString( "Port" );
    public   static String MS_RESUME = loadString( "Resume Message" );
    public   static String MS_PROXY_ENABLE = loadString( "proxy CONNECT" );
    public   static String MS_PROXY_HOST = loadString( "Proxy name/IP" );
    public   static String PROXY_PORT = loadString( "Proxy port" );
    public   static String MS_ARE_YOU_SURE_WANT_TO_DISCARD = loadString( "Are You sure want to discard " );
    public   static String MS_FROM_OWNER_TO = loadString( " from OWNER to " );
    public   static String MS_MODIFY_AFFILIATION = loadString( "Modify affiliation" );
    public   static String MS_AUTOFOCUS = loadString( "Autofocus" );
    public   static String MS_ADD_TO_ROSTER = loadString( "Add to roster" );
    public   static String MS_CLEAR=loadString( "Clear" );
    public   static String MS_ALT_LANG="langfile";
    public   static String MS_GRANT_MEMBERSHIP = "Grant Membership";
    public   static String MS_SELLOGIN = loadString( "Connect" );
//--toon
    public final static String MS_UNAFFILIATE = "Unaffiliate";
    public final static String MS_GRANT_MODERATOR = "Grant Moderator";
    public final static String MS_REVOKE_MODERATOR = "Revoke Moderator";
    public final static String MS_GRANT_ADMIN = "Grant Admin";
    //public final static String MS_REVOKE_ADMIN = "Revoke Admin";
    public final static String MS_GRANT_OWNERSHIP = "Grant Ownership";
    //public final static String MS_REVOKE_OWNERSHIP = "Revoke Ownership";
//--toon
    public final static String MS_VIZITORS_FORBIDDEN="Visitors are not allowed to send messages to all occupants";
    public final static String MS_IS_INVITING_YOU=" is inviting You to ";
    public final static String MS_ASK_SUBSCRIPTION="Ask subscription";
    public final static String MS_GRANT_SUBSCRIPTION="Grant subscription";
    public final static String MS_INVITE="Invite to conference";
    public final static String MS_INVITE_REASON="Reason";
    public final static String MS_YOU_HAVE_BEEN_INVITED="You have been invited to ";
    public final static String MS_SURE_CLEAR="Are You sure want to clear messagelist?";
    public final static String MS_DISCO_ROOM="Participants";
    public final static String CAPS_STATE="Abcd";
    
    public   static String MS_STORE_PRESENCE = loadString( "room presences" );
    
    public static String MS_IS_NOW_KNOWN_AS=loadString(" is now known as ");
    public static String MS_WAS_BANNED=loadString(" was banned ");
    public static String MS_WAS_KICKED=loadString(" was kicked ");
    public static String MS_HAS_BEEN_KICKED_BECAUSE_ROOM_BECAME_MEMBERS_ONLY=loadString(" has been kicked because room became members-only");
    public static String MS_HAS_LEFT_CHANNEL=loadString(" has left the channel");
    public static String MS_HAS_JOINED_THE_CHANNEL_AS=loadString(" has joined the channel as ");
    public static String MS_AND=loadString(" and ");
    public static String MS_IS_NOW=loadString(" is now ");    
    

    public static String MS_XMLLANG;
    public static String MS_IFACELANG;

    private SR() { }
    
    private static Hashtable lang;
    
    private static String loadString(String key) {
        if (lang==null) {
            String langFile=Config.getInstance().langFileName();
            if (langFile==null) lang=new Hashtable(); 
            else lang=new StringLoader().hashtableLoader(langFile);
            System.out.print("Loading locale ");
            System.out.println(langFile);
            MS_XMLLANG=(String)lang.get("xmlLang");
            
            MS_IFACELANG=MS_XMLLANG;
            if (MS_IFACELANG==null) MS_IFACELANG="en";
        }
        String value=(String)lang.get(key);
//#if LOCALE_DEBUG
        if (value==null) {
            if (Config.getInstance().lang!=0) {
                System.out.print("Can't find local string for <");
                System.err.print(key);
                System.err.println('>');
            }
        }
//#endif
        return (value==null)?key:value;
    }

    public static void loaded() {
        lang=null;
    }
}
