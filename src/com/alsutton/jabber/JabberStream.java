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
import Client.Config;
import io.Utf8IOStream;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.xmlparser.*;
import util.StringLoader;



/**
 * The stream to a jabber server.
 */

public class JabberStream implements XMLEventListener, Runnable {
    
    private Utf8IOStream iostream;
    
    /**
     * The dispatcher thread.
     */
    
    private JabberDataBlockDispatcher dispatcher;
    
    //private Vector sendQueue;
    
    private boolean rosterNotify;
    
    private String server; // for ping
    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public JabberStream( String server, String hostAddr, boolean xmppV1, String proxy, JabberListener theListener )
    throws IOException {
        this.server=server;
        boolean waiting=Config.getInstance().istreamWaiting;
        if (proxy==null) {
            StreamConnection connection = (StreamConnection) Connector.open(hostAddr);
            iostream=new Utf8IOStream(connection);
            iostream.setStreamWaiting(waiting);
        } else {
            StreamConnection connection = (StreamConnection) Connector.open(proxy);
            iostream=new Utf8IOStream(connection);
            iostream.setStreamWaiting(waiting);
            
            send( "CONNECT " + hostAddr + " HTTP/1.0 \r\n"
                + "HOST " + hostAddr + "\r\n" 
                + "Pragma: no-cache\r\n" + "\r\n");
            
            String inpLine=iostream.readLine();
            if (inpLine.indexOf("200",0)<=0) throw new IOException(inpLine);
            while (inpLine.length()>0) {
                inpLine=iostream.readLine();
            }
        }

        
        dispatcher = new JabberDataBlockDispatcher();
        if( theListener != null ) {
            setJabberListener( theListener );
        }
        
     
        new Thread( this ). start();
        
        initiateStream(server, xmppV1);
        
        keepAlive=new TimerTaskKeepAlive(Config.getInstance().keepAlive);
    }

    public void initiateStream(final String server, final boolean xmppV1) throws IOException {
        
        //sendQueue=new Vector();
        
        StringBuffer header=new StringBuffer("<stream:stream to='" );
        header.append( server );
        header.append( "' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'" );
        if (xmppV1) header.append(" version='1.0'");
        header.append( '>' );
        send(header.toString());
    }
    
    
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    
    public void run() {
        try {
            XMLParser parser = new XMLParser( this );
            parser.parse( iostream );
            //dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
            System.out.println("Exception in parser:");
            e.printStackTrace();
            dispatcher.broadcastTerminatedConnection(e);
        }
    }
    
    /**
     * Method to close the connection to the server and tell the listener
     * that the connection has been terminated.
     */
    
    public void close() {
        keepAlive.destroyTask();
        
        dispatcher.setJabberListener( null );
        try {
            send( "</stream:stream>" );
            try {  Thread.sleep(500); } catch (Exception e) {};
            //connection.close();
        } catch( IOException e ) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
	    iostream.close();
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
        switch (Config.getInstance().keepAliveType){
            case 2:
                ping();
                break;
            case 1:
                send("<iq/>");
                break;
            default:
                send(" ");
        }
    }
    
    public void send( String data ) throws IOException {
	iostream.send(new StringBuffer(data));
        //System.out.println(data);
    }
    
    public void sendBuf( StringBuffer data ) throws IOException {
	iostream.send(data);
        //System.out.println(data);
    }
    
    /**
     * Method of sending a Jabber datablock to the server.
     *
     * @param block The data block to send to the server.
     */
    
    public void send( JabberDataBlock block )  { new SendJabberDataBlock(block); }
    
    /**
     * Set the listener to this stream.
     */
    
    public void addBlockListener(JabberBlockListener listener) { 
        dispatcher.addBlockListener(listener);
    }
    public void cancelBlockListener(JabberBlockListener listener) { 
        dispatcher.cancelBlockListener(listener);
    }
    
    public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }
    
    public void setJabberListener( JabberListener listener ) {
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
    
    public boolean tagStarted( String name, Hashtable attributes ) {
        if (currentBlock!=null){
            
            currentBlock = new JabberDataBlock( name, currentBlock, attributes );
            // TODO: remove stub
            // M55 STUB
//#if !(MIDP1)
            // photo reading
            if ( name.equals("BINVAL") ){
                return true;
            }
//#endif
            
            if (rosterNotify) if (name.equals("item")) dispatcher.rosterNotify();
            
        } else if ( name.equals( "stream:stream" ) ) {
            String SessionId=(String)attributes.get("id");
            dispatcher.broadcastBeginConversation(SessionId);
        } else if ( name.equals( "message" ) )
            currentBlock = new Message( currentBlock, attributes );
        else if ( name.equals("iq") )
            currentBlock = new Iq( currentBlock, attributes );
        else if ( name.equals("presence") )
            currentBlock = new Presence( currentBlock, attributes );
        else currentBlock = new JabberDataBlock(name, null, null);
        return false;
    }
    
    /**
     * Method called when some plain text is encountered in the XML stream
     * comming from the server.
     *
     * @param text The plain text in question
     */
    
    public void plaintextEncountered( String text ) {
        if( currentBlock != null ) {
            currentBlock.setText( text );
        }
    }
    
    public void binValueEncountered( byte binVaule[] ) {
        if( currentBlock != null ) {
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
    
    public void tagEnded( String name ) {
        if( currentBlock == null )
            return;
        
        JabberDataBlock parent = currentBlock.getParent();
        if( parent == null ) {
            dispatcher.broadcastJabberDataBlock( currentBlock );
            //System.out.println(currentBlock.toString());
        } else
            parent.addChild( currentBlock );
        currentBlock = parent;
    }

    private void ping() {
        JabberDataBlock ping=new Iq(null, Iq.TYPE_GET, "ping");
        ping.addChild("query", null).setNameSpace("jabber:iq:version");
        send(ping);
    }

//#if ZLIB
    public void setZlibCompression() {
        iostream.setStreamCompression();
    }

    public String getStreamStats() {
        return iostream.getStreamStats();
    }
//#endif
    
    private class TimerTaskKeepAlive extends TimerTask{
        private Timer t;
        public TimerTaskKeepAlive(int periodSeconds){
            t=new Timer();
            long period=periodSeconds*1000; // milliseconds
            t.schedule(this, period, period);
        }
        public void run() {
            try {
                System.out.println("Keep-Alive");
                sendKeepAlive();
            } catch (Exception e) { e.printStackTrace(); }
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
    
    private class SendJabberDataBlock implements Runnable {
        private JabberDataBlock data;
        public SendJabberDataBlock(JabberDataBlock data) {
            this.data=data;
            new Thread(this).start();
        }
        public void run(){
            try {
                StringBuffer buf=new StringBuffer();
                data.constructXML(buf);
                sendBuf( buf );
            } catch (Exception e) {e.printStackTrace(); }
        }
    }
}
