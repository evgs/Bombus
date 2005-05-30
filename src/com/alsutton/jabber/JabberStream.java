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

package com.alsutton.jabber;
import java.io.*;
import java.util.*;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.xmlparser.*;
import Client.StaticData;

/**
 * The stream to a jabber server.
 */

public class JabberStream implements XMLEventListener, Runnable
{
  /**
   * The Output stream to the server.
   */

  private OutputStreamWriter outStream;

  /**
   * The input stream from the server.
   */

  private InputStream inpStream;

  /**
   * The dispatcher thread.
   */

  private JabberDataBlockDispatcher dispatcher;
  
  private boolean rosterNotify;
  
  public void enableRosterNotify(boolean en){ rosterNotify=en; }

  /**
   * Constructor. Connects to the server and sends the jabber welcome message.
   *
   * @param connectorInterface The connector which establishes the socket for
   * the connection
   */

  public JabberStream( ConnectorInterface connectorInterface )
    throws IOException
  {
    this( connectorInterface, null );
  }

  /**
   * Constructor. Connects to the server and sends the jabber welcome message.
   *
   * @param connectorInterface The connector which establishes the socket for
   * the connection
   */

  public JabberStream( ConnectorInterface connectorInterface,
    JabberListener theListener )
    throws IOException
  {
    dispatcher = new JabberDataBlockDispatcher();
    if( theListener != null )
    {
      setJabberListener( theListener );
    }

    OutputStream outStr= connectorInterface.openOutputStream();
/*#!M55,M55_Release#*///<editor-fold>
    outStream = new OutputStreamWriter(outStr,"UTF-8");
/*$!M55,M55_Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--    outStream = new OutputStreamWriter(outStr);
/*$M55,M55_Release$*///</editor-fold>
    inpStream = connectorInterface.openInputStream();

    outStream.write( "<stream:stream to=\"" );
    outStream.write( connectorInterface.getHostname());
    outStream.write( "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">" );
    outStream.flush();

    Thread newThread = new Thread( this );
    newThread.start();
    keepAlive=new TimerTaskKeepAlive(StaticData.getInstance().config.keepAlive);
  }
  

  /**
   * The threads run method. Handles the parsing of incomming data in its
   * own thread.
   */

  public void run()
  {
    try
    {
/*#DefaultConfiguration,Release#*///<editor-fold>
      InputStreamReader inSource = new InputStreamReader( inpStream, "UTF-8" );
/*$DefaultConfiguration,Release$*///</editor-fold>
/*#M55,M55_Release#*///<editor-fold>
//--      InputStreamReader inSource = new InputStreamReader( inpStream );
/*$M55,M55_Release$*///</editor-fold>
      XMLParser parser = new XMLParser( this );
      parser.parse( inSource );
      dispatcher.broadcastTerminatedConnection( null );
    }
    catch( Exception e )
    {
      dispatcher.broadcastTerminatedConnection(e);
    }
  }

  /**
   * Method to close the connection to the server and tell the listener
   * that the connection has been terminated.
   */

  public void close()
  {
    keepAlive.destroyTask();
    
    dispatcher.setJabberListener( null );
    try
    {
      send( "</stream:stream>" );
      inpStream.close();
      outStream.close();
    }
    catch( IOException e )
    {
      // Ignore an IO Exceptions because they mean that the stream is
      // unavailable, which is irrelevant.
    }
    finally
    {
      dispatcher.halt();
    }
  }

  /**
   * Method of sending data to the server.
   *
   * @param data The data to send.
   */

 /* public void send( byte[] data ) throws IOException
  {
    outStream.write( data );
    outStream.flush();
  }
  */

  /**
   * Method of sending data to the server.
   *
   * @param The data to send to the server.
   */
  public void sendKeepAlive() throws IOException {
      send(" ");
  }

  public void send( String data ) throws IOException {
      
   synchronized (outStream) {   
/*#M55,M55_Release#*///<editor-fold>
//--          outStream.write(toUTF(data));
/*$M55,M55_Release$*///</editor-fold>
/*#DefaultConfiguration,Release#*///<editor-fold>
          outStream.write(data);
/*$DefaultConfiguration,Release$*///</editor-fold>
          outStream.flush();
   }
  }

  /**
   * Method of sending a Jabber datablock to the server.
   *
   * @param block The data block to send to the server.
   */

  public void send( JabberDataBlock block ) //throws IOException
  {
      try {
          send( block.toString() );
      } catch (Exception e) {e.printStackTrace();}
  }

  /**
   * Set the listener to this stream.
   */

  public void setJabberListener( JabberListener listener )
  {
    dispatcher.setJabberListener( listener );
  }

  /**
   * The current class being constructed.
   */

  private JabberDataBlock currentBlock;

  /**
   * Method called when an XML tag is started in the stream comming from the
   * server.
   *
   * @param name Tag name.
   * @param attributes The tags attributes.
   */

  public boolean tagStarted( String name, Hashtable attributes )
  {
    if (currentBlock!=null){

        currentBlock = new JabberDataBlock( name, currentBlock, attributes );
        // TODO: remove stub
        // M55 STUB
/*#DefaultConfiguration,Release#*///<editor-fold>
        // photo reading
        if ( name.equals("binval") ){
           return true;
        }
/*$DefaultConfiguration,Release$*///</editor-fold>

        if (rosterNotify) if (name.equals("item")) dispatcher.rosterNotify();

    } else if ( name.equals( "stream:stream" ) ) {
        String SessionId=(String)attributes.get("id");
        dispatcher.broadcastBeginConversation(SessionId);
    }
    else if ( name.equals( "message" ) )
      currentBlock = new Message( currentBlock, attributes );
    else if ( name.equals("iq") )
      currentBlock = new Iq( currentBlock, attributes );
    else if ( name.equals("presence") )
      currentBlock = new Presence( currentBlock, attributes );
    return false;
  }

  /**
   * Method called when some plain text is encountered in the XML stream
   * comming from the server.
   *
   * @param text The plain text in question
   */

  public void plaintextEncountered( String text )
  {
    if( currentBlock != null )
    {
      currentBlock.addText( text );
    }
  }

  public void binValueEncountered( byte binVaule[] )
  {
    if( currentBlock != null )
    {
      //currentBlock.addText( text );
        currentBlock.addChild(binVaule);
    }
  }

  /**
   * The method called when a tag is ended in the stream comming from the
   * server.
   *
   * @param name The name of the tag that has just ended.
   */

  public void tagEnded( String name )
  {
    if( currentBlock == null )
      return;

    JabberDataBlock parent = currentBlock.getParent();
    if( parent == null )
      dispatcher.broadcastJabberDataBlock( currentBlock );
    else
      parent.addChild( currentBlock );
    currentBlock = parent;
  }

/*#M55,M55_Release#*///<editor-fold>
//--  // temporary
//--  public static String toUTF(String s) {
//--      int i = 0;
//--      StringBuffer stringbuffer=new StringBuffer();
//--      
//--      for(int j = s.length(); i < j; i++) {
//--          int c = (int)s.charAt(i);
//--          if ((c >= 1) && (c <= 0x7f)) {
//--              stringbuffer.append( (char) c);
//--              
//--          }
//--          if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
//--              stringbuffer.append((char)(0xc0 | (0x1f & (c >> 6))));
//--              stringbuffer.append((char)(0x80 | (0x3f & c)));
//--          }
//--          if ((c >= 0x800) && (c <= 0xffff)) {
//--              stringbuffer.append(((char)(0xe0 | (0x0f & (c >> 12)))));
//--              stringbuffer.append((char)(0x80 | (0x3f & (c >>  6))));
//--              stringbuffer.append(((char)(0x80 | (0x3f & c))));
//--          }
//--      }
//--      
//--      return stringbuffer.toString();
//--  }
/*$M55,M55_Release$*///</editor-fold>

    private class TimerTaskKeepAlive extends TimerTask{
        private Timer t;
        public TimerTaskKeepAlive(int periodSeconds){
            t=new Timer();
            long period=periodSeconds*1000; // milliseconds
            t.schedule(this, period, period);
        }
        public void run() {
            try {
                sendKeepAlive();
                System.out.println("Keep-Alive");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void destroyTask(){
            if (t!=null){
                this.cancel();
                t.cancel();
                t=null;
            }
        }
    }
    private TimerTaskKeepAlive keepAlive;

}
