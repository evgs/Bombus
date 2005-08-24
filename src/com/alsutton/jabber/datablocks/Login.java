/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
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
import com.ssttr.crypto.SHA1;

/**
 * The class representing the login message.
 * <p>
 * This has no incomming constructor as login messages are never
 * received from the server.
 */

public class Login extends JabberDataBlock
{
  /**
   * Constructor. Builds the string ready for sending to the server.
   *
   * @param username The username to log in with
   * @param password The password to log in with
   * @param sid Session id for digest generation
   * @param resource The resource name to use
   */

  public Login( String username, String server, String password, String sid, String resource)
  {
    super( );

    setAttribute( "id", "auth-s" );
    setAttribute( "type", "set" );
    setAttribute( "to", server);

    JabberDataBlock queryBlock = new JabberDataBlock( "query", null, null );
    queryBlock.setNameSpace( "jabber:iq:auth" );

    addChild(queryBlock);

    if( username != null )
    {
      JabberDataBlock usernameBlock = new JabberDataBlock( "username", null, null );
      usernameBlock.addText( username );
      queryBlock.addChild( usernameBlock );
    }

    if( password != null )
    {
      String digest=null;
      String pwdtype;
      if (sid!=null) {
          pwdtype="digest";
          SHA1 a=new SHA1();
          a.init();
          a.updateASCII(sid+password);
          a.finish();
          digest=a.getDigestHex();
      } else pwdtype="password";//plain
        // plain text
      JabberDataBlock passwordBlock = new JabberDataBlock( pwdtype, null, null );
      passwordBlock.addText( (digest==null)?password:digest );
      queryBlock.addChild( passwordBlock );
    }

    if( resource != null )
    {
      JabberDataBlock resourceBlock = new JabberDataBlock( "resource", null, null );
      resourceBlock.addText( resource );
      queryBlock.addChild( resourceBlock );
   }
  }

  /**
   * Method to return the tag name
   *
   * @return Always the string "iq".
   */
  public String getTagName()
  {
    return "iq";
  }
}
