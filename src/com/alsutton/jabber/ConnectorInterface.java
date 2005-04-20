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

  Neither the name of Al Sutton nor alsutton.com may be used to endorse or promote
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

import java.io.*;
/**
 * The interface for all connectors which handle creating the input
 * and output stream.
 */

public abstract class ConnectorInterface
{
  /**
   * The name of the host being connected to
   */

  protected String hostname;

  /**
   * Constructor
   *
   * @param _hostname The name of the host being connected to
   */

  public ConnectorInterface( String _hostname )
  {
    hostname = _hostname;
  }

  /**
   * Method to get the name of the host being connected to
   *
   * @return The hostname being connected to
   */

  public String getHostname()
  {
    return hostname;
  }
  /**
   * Method to return the input stream of the connection
   *
   * @return The input stream
   */

  public abstract InputStream openInputStream() throws IOException;

  /**
   * Method to return the output stream of the connection
   *
   * @return The output stream
   */

  public abstract OutputStream openOutputStream() throws IOException;
}
