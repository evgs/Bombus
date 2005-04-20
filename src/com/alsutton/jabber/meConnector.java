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

package com.alsutton.jabber;
import javax.microedition.io.*;
import java.io.*;

/**
 * Title:        meConnector.java
 * Description:  Class for getting a connection to the server for j2me
 */

public class meConnector extends ConnectorInterface
{
  /**
   * The connection to the jabber server
   */

  private StreamConnection connection = null;

  /**
   * Constructor
   *
   * @param hostname The host to connect to
   * @param port The port to connect to
   */

  public meConnector( String hostname, int port ) throws IOException
  {
    super( hostname );

    /*StringBuffer connectorStringBuffer = new StringBuffer( "socket://" );
    connectorStringBuffer.append( hostname );
    connectorStringBuffer.append( ":" );
    connectorStringBuffer.append( port );
    //connectorStringBuffer.append( "/" );

    String connectorString = connectorStringBuffer.toString();
    connection = (StreamConnection) Connector.open( connectorString );
     */
    connection = (StreamConnection) Connector.open( 
            "socket://"+hostname+":"+port 
            );
/*#DefaultConfiguration,Release#*///<editor-fold>
    try {    
        ((SocketConnection) connection).setSocketOption(SocketConnection.KEEPALIVE,1);
    } catch (Exception e) { e.printStackTrace(); }
/*$DefaultConfiguration,Release$*///</editor-fold>
  }

  /**
   * Method to return the input stream of the connection
   *
   * @return The input stream
   */

  public InputStream openInputStream() throws IOException
  {
    return connection.openInputStream();
  }

  /**
   * Method to return the output stream of the connection
   *
   * @return The output stream
   */

  public OutputStream openOutputStream()  throws IOException
  {
    return connection.openOutputStream();
  }
}
