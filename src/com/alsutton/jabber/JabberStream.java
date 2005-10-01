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
import Client.NvStorage;
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import com.alsutton.jabber.datablocks.*;
import com.alsutton.xmlparser.*;
import Client.StaticData;



/**
 * The stream to a jabber server.
 */

public class JabberStream implements XMLEventListener, Runnable {
    
    /*#!MIDP1#*///<editor-fold>
    private SocketConnection connection = null;
    /*$!MIDP1$*///</editor-fold>
    /*#MIDP1#*///<editor-fold>
//--    private StreamConnection connection = null;
    /*$MIDP1$*///</editor-fold>
    
    /*#USE_UTF8_READER#*///<editor-fold>
//--    private OutputStream outStream;
    /*$USE_UTF8_READER$*///</editor-fold>
    /*#!USE_UTF8_READER#*///<editor-fold>
    private OutputStreamWriter outStream;
    /*$!USE_UTF8_READER$*///</editor-fold>
    
    /**
     * The input stream from the server.
     */
    
    private InputStream inpStream;
    
    /**
     * The dispatcher thread.
     */
    
    private JabberDataBlockDispatcher dispatcher;
    
    //private Vector sendQueue;
    
    private boolean rosterNotify;
    
    public void enableRosterNotify(boolean en){ rosterNotify=en; }
    
    /**
     * Constructor. Connects to the server and sends the jabber welcome message.
     *
     */
    
    public JabberStream( String hostName, String hostAddr, int hostPort,
            JabberListener theListener , boolean ssl)
            throws IOException {
        String url=((ssl)?"ssl://":"socket://")+hostAddr+":"+hostPort ;
        connection =
/*#!MIDP1#*///<editor-fold>
                (SocketConnection) Connector.open(url);
/*$!MIDP1$*///</editor-fold>
/*#MIDP1#*///<editor-fold>
//--                (StreamConnection) Connector.open(url);
/*$MIDP1$*///</editor-fold>
        
/*#!MIDP1#*///<editor-fold>
        try {
            connection.setSocketOption(SocketConnection.KEEPALIVE,1);
        } catch (Exception e) { e.printStackTrace(); }
/*$!MIDP1$*///</editor-fold>
        dispatcher = new JabberDataBlockDispatcher();
        if( theListener != null ) {
            setJabberListener( theListener );
        }
        
        inpStream = connection.openInputStream();
        new Thread( this ). start();
/*#!USE_UTF8_READER#*///<editor-fold>
        OutputStream outStr= connection.openOutputStream();
        outStream = new OutputStreamWriter(outStr,"UTF-8");
/*$!USE_UTF8_READER$*///</editor-fold>
/*#USE_UTF8_READER#*///<editor-fold>
//--        outStream = connection.openOutputStream();
/*$USE_UTF8_READER$*///</editor-fold>
        
        //sendQueue=new Vector();
        
        StringBuffer header=new StringBuffer("<stream:stream to=\"" );
        header.append( hostName );
        header.append( "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\">" );
        send(header.toString());
        /*#USE_LOGGER#*///<editor-fold>
//--        NvStorage.logS("SENT=");
//--        NvStorage.logCrLf();
//--        NvStorage.logS(header.toString());
//--        NvStorage.logCrLf();
        /*$USE_LOGGER$*///</editor-fold>
        
        keepAlive=new TimerTaskKeepAlive(StaticData.getInstance().config.keepAlive);
    }
    
    
    /**
     * The threads run method. Handles the parsing of incomming data in its
     * own thread.
     */
    
    public void run() {
        try {
            XMLParser parser = new XMLParser( this );
/*#!USE_UTF8_READER#*///<editor-fold>
            InputStreamReader inSource = new InputStreamReader( inpStream, "UTF-8" );
            parser.parse( inSource );
/*$!USE_UTF8_READER$*///</editor-fold>
/*#USE_UTF8_READER#*///<editor-fold>
//--//            InputStreamReader inSource = new InputStreamReader( inpStream );
//--            parser.parse( inpStream );
/*$USE_UTF8_READER$*///</editor-fold>
            dispatcher.broadcastTerminatedConnection( null );
        } catch( Exception e ) {
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
            outStream.flush();
            try {  Thread.sleep(500); } catch (Exception e) {};
            inpStream.close();
            outStream.close();
            //connection.close();
        } catch( IOException e ) {
            // Ignore an IO Exceptions because they mean that the stream is
            // unavailable, which is irrelevant.
        } finally {
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
/*#USE_UTF8_READER#*///<editor-fold>
//--            outStream.write(toUTF(data).getBytes());
/*$USE_UTF8_READER$*///</editor-fold>
/*#!USE_UTF8_READER#*///<editor-fold>
            outStream.write(data);
/*$!USE_UTF8_READER$*///</editor-fold>
/*#OUTSTREAM_FLUSH#*///<editor-fold>
//--            outStream.flush();
/*$OUTSTREAM_FLUSH$*///</editor-fold>
            //System.out.println(data);
        }
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
    
    /*public void cancelBlockListenerByClass(Class removeClass) {
        dispatcher.cancelBlockListenerByClass(removeClass);
    }*/
    
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
/*#MIDP2#*///<editor-fold>
//--            // photo reading
//--            if ( name.equals("binval") ){
//--                return true;
//--            }
/*$MIDP2$*///</editor-fold>
            
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
            /*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(currentBlock, true);
            /*$USE_LOGGER$*///</editor-fold>
            dispatcher.broadcastJabberDataBlock( currentBlock );
            //System.out.println(currentBlock.toString());
        } else
            parent.addChild( currentBlock );
        currentBlock = parent;
    }
    
/*#USE_UTF8_READER#*///<editor-fold>
//--    // temporary
//--    public static String toUTF(String s) {
//--        int i = 0;
//--        StringBuffer stringbuffer=new StringBuffer();
//--
//--        for(int j = s.length(); i < j; i++) {
//--            int c = (int)s.charAt(i);
//--            if ((c >= 1) && (c <= 0x7f)) {
//--                stringbuffer.append( (char) c);
//--
//--            }
//--            if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
//--                stringbuffer.append((char)(0xc0 | (0x1f & (c >> 6))));
//--                stringbuffer.append((char)(0x80 | (0x3f & c)));
//--            }
//--            if ((c >= 0x800) && (c <= 0xffff)) {
//--                stringbuffer.append(((char)(0xe0 | (0x0f & (c >> 12)))));
//--                stringbuffer.append((char)(0x80 | (0x3f & (c >>  6))));
//--                stringbuffer.append(((char)(0x80 | (0x3f & c))));
//--            }
//--        }
//--
//--        return stringbuffer.toString();
//--    }
/*$USE_UTF8_READER$*///</editor-fold>
    
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
    
    private class SendJabberDataBlock implements Runnable {
        private JabberDataBlock data;
        public SendJabberDataBlock(JabberDataBlock data) {
            this.data=data;
            new Thread(this).start();
        }
        public void run(){
            try {
                /*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(data, false);
                /*$USE_LOGGER$*///</editor-fold>
                send( data.toString() );
            } catch (Exception e) {
                e.printStackTrace();
                /*#USE_LOGGER#*///<editor-fold>
//--            NvStorage.log(e, "JabberStream:382");
                /*$USE_LOGGER$*///</editor-fold>
            }
            
        }
    }
}
