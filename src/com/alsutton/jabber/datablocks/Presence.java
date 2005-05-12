/*
  Copyright (c) 2000, Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.alsutton.jabber.datablocks;
import com.alsutton.jabber.*;

import java.util.*;
import ui.ImageList;

/**
 * Class representing the presence message block.
 */

public class Presence extends JabberDataBlock
{
  /**
   * Constructor.
   *
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  public Presence( JabberDataBlock _parent, Hashtable _attributes )
  {
    super( _parent, _attributes );
  }

  /**
   * Default constructor for outgoing presence messages.
   */

  public Presence(String to, String type){
      super(null,null);
      setAttribute("to",to);
      setAttribute("type",type);
  };
  

  public Presence(int status, int priority, String message)
  {
    super( null, null );
    switch (status){
        case PRESENCE_OFFLINE: setType(PRS_OFFLINE); break;
        case PRESENCE_INVISIBLE: setType(PRS_INVISIBLE); break;
        case PRESENCE_CHAT: setShow(PRS_CHAT);break;
        case PRESENCE_AWAY: setShow(PRS_AWAY);break;
        case PRESENCE_XA: setShow(PRS_XA);break;
        case PRESENCE_DND: setShow(PRS_DND);break;
    }
    addChild("priority",String.valueOf(priority));
    if (message!=null) 
        if (message.length()>0) addChild("status",message);
  }

  private StringBuffer text;
  private int presenceCode;
  
  public void dispathch(){
      text=new StringBuffer();
      String type=getTypeAttribute();
      presenceCode=PRESENCE_AUTH;
      if (type!=null) {
          if (type.equals(PRS_OFFLINE)) { 
              presenceCode=PRESENCE_OFFLINE;
              text.append("offline");
          };
          if (type.equals("subscribe")) text.append(SUBSCRIBE); 
          if (type.equals("subscribed")) text.append(SUBSCRIBED);
          if (type.equals("unsubscribed")) text.append(UNSUBSCRIBED);
          
          if (type.equals(PRS_ERROR)) {
              presenceCode=PRESENCE_ERROR;
              text.append(PRS_ERROR);
          }
      } else {
          // online-kinds
          String show;
          show=getShow(); text.append(show);
          presenceCode=PRESENCE_ONLINE;
          if (show.equals(PRS_CHAT)) presenceCode=PRESENCE_CHAT;
          if (show.equals(PRS_AWAY)) presenceCode=PRESENCE_AWAY;
          if (show.equals(PRS_XA)) presenceCode=PRESENCE_XA;
          if (show.equals(PRS_DND)) presenceCode=PRESENCE_DND;
          
          show=getTextForChildBlock("status");
          if (show.length()>0) {
              text.append('(');
              text.append(show);
              text.append(')');
          }
          
          // priority
          text.append(" [");
          text.append(getPriority());
          text.append(']');
          
      }
  }

  /**
   * Method to set the presence type
   */

  public void setType( String type )
  {
    setAttribute("type", type);
  }

  public int getPriority(){
      try {
          return Integer.parseInt(getTextForChildBlock("priority"));
      } catch (Exception e) {return 0;}
  }
  
  public void setShow(String text){ addChild("show", text); }
  
  /**
   * Method to get the name of the tag
   */

  public String getTagName()
  {
    return "presence";
  }

  
  public int getTypeIndex() { return presenceCode;}

  public String getPresenceTxt(){ return text.toString(); }
  
  private String getShow(){
      try {
          return getChildBlock("show").getText();
      } catch (Exception e) { return PRS_ONLINE; }
  }

  /**
     * Method to get the presence <B>from</B> field
     * @return <B>from</B> field as a string
     */
  public String getFrom() {
      return getAttribute("from");
  }
  public final static int PRESENCE_ONLINE=0;
  public final static int PRESENCE_CHAT=1;
  public final static int PRESENCE_AWAY=2;
  public final static int PRESENCE_XA=3;
  public final static int PRESENCE_DND=4;
  public final static int PRESENCE_OFFLINE=5;
  public final static int PRESENCE_ASK=6;
  public final static int PRESENCE_UNKNOWN=7;
  public final static int PRESENCE_INVISIBLE=ImageList.ICON_INVISIBLE_INDEX;
  public final static int PRESENCE_ERROR=ImageList.ICON_ERROR_INDEX;
  public final static int PRESENCE_TRASH=ImageList.ICON_ERROR_INDEX+1;
  public final static int PRESENCE_AUTH=-1;
  
  public final static String PRS_OFFLINE="unavailable";
  public final static String PRS_ERROR="error";
  public final static String PRS_CHAT="chat";
  public final static String PRS_AWAY="away";
  public final static String PRS_XA="xa";
  public final static String PRS_DND="dnd";
  public final static String PRS_ONLINE="online";
  public final static String PRS_INVISIBLE="invisible";

  public final static String SUBSCRIBE="This user wants to subscribe to your presence.";
  public final static String SUBSCRIBED="You are now authorized";
  public final static String UNSUBSCRIBED="Your authorization has been removed!";
  
}
