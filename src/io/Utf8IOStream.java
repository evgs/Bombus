/*
 * Utf8IOStream.java
 *
 * Created on 18 Декабрь 2005 пїЅ., 0:52
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io;

//#if ZLIB
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZInputStream;
import com.jcraft.jzlib.ZOutputStream;
//#endif
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import javax.microedition.io.*;
import Client.Config;
import util.strconv;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream implements Runnable{
    
    private StreamConnection connection;
    private InputStream inpStream;
    private OutputStream outStream;

    private boolean iStreamWaiting;

    private int bytesRecv;

    private int bytesSent;
    

//#if (ZLIB)
    public void setStreamCompression(){
        inpStream=new ZInputStream(inpStream);
        outStream=new ZOutputStream(outStream, JZlib.Z_DEFAULT_COMPRESSION);
        ((ZOutputStream)outStream).setFlushMode(JZlib.Z_SYNC_FLUSH);
    }
//#endif
    
//#if !(USE_UTF8_READER)
//#     private OutputStreamWriter outputWriter;
//#     private InputStreamReader inputReader;
//#endif
    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
//#if !(MIDP1)
        try {
            Config cf=Config.getInstance();
            SocketConnection sc=(SocketConnection)connection;
            sc.setSocketOption(SocketConnection.KEEPALIVE, 1);
            /*if (cf.socketLINGER>=0) sc.setSocketOption(SocketConnection.LINGER, cf.socketLINGER);
            if (cf.socketRCVBUF>=0) sc.setSocketOption(SocketConnection.RCVBUF, cf.socketRCVBUF);
            if (cf.socketSNDBUF>=0) sc.setSocketOption(SocketConnection.SNDBUF, cf.socketSNDBUF);*/
        } catch (Exception e) {}
//#endif
	
	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

//#if !(USE_UTF8_READER)
//#         inputReader = new InputStreamReader(inpStream, "UTF-8");
//# 	outputWriter = new OutputStreamWriter(outStream,"UTF-8");
//#else
      length=pbyte=0;
//#endif


    }
    
    public void send( StringBuffer data ) throws IOException {
	
	synchronized (outStream) {
//#if !(USE_UTF8_READER)
//# 	    outputWriter.write(data);
//#else
	    //byte a[]=toUTF(data);
	    //for (int i=0;i<a.length; i++){
	    //	System.out.print(" "+((char)a[i])+"="+a[i]);
	    //}
	    //System.out.println();
            
            StringBuffer outbuf=strconv.toUTFSb(data);
            int outLen=outbuf.length();
            byte bytes[]=new byte[outLen];
            for (int i=0; i<outLen; i++) {
                bytes[i]=(byte)outbuf.charAt(i);
            }
            
	    outStream.write(bytes);
            bytesSent++;
//#endif
	    
//#if OUTSTREAM_FLUSH
	    outStream.flush();
//#endif
	}
//#if (XML_STREAM_DEBUG)        
//#         System.out.println(">> "+data);
//#endif
    }
    
//#if USE_UTF8_READER
    // temporary

    byte cbuf[]=new byte[512];
    int length;
    int pbyte;
    
    private int chRead() throws IOException{
        bytesRecv++;
        if (length>pbyte) return cbuf[pbyte++];

        int avail=inpStream.available();
        
//#if ZLIB
        if (inpStream instanceof ZInputStream) avail=512;
//#endif
        
        while (avail==0 
//#if (!ZLIB)
//#                 && iStreamWaiting
//#endif
                ) {
            try { Thread.sleep(100); } catch (Exception e) {};
            avail=inpStream.available();
        }
        
    //#if !(XML_STREAM_DEBUG)
	if (avail<2) return inpStream.read() &0xff;
    //#else
//#         if (avail<2) {
//# 	  System.out.println(" single-byte");
//#           int ch=inpStream.read();
//# 	  System.out.println("<< "+(char)ch);
//# 	  return ch;
//#         }
//#           System.out.println(" prebuffering "+avail);
    //#endif
	
	
	
	length= inpStream.read(cbuf, 0, (avail<512)?avail:512 );
	pbyte=1;
	
    //#if (XML_STREAM_DEBUG)
//# 	System.out.println("<< "+new String(cbuf, 0, length));
    //#endif
	return cbuf[0];
    }
//#endif
    
    public int getNextCharacter()
    throws IOException {
//#if !(USE_UTF8_READER)
//# 	return inputReader.read();
//#else
	int chr = chRead() &0xff;
	if( chr == 0xff ) return -1; // end of stream
	
	if (chr<0x80) return chr;
	if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
	
        int chr2= chRead() &0xff;
        if (chr2==0xff) return -1;
        if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
	
	if (chr<0xe0) {
	    // cx, dx 
	    return ((chr & 0x1f)<<6) | (chr2 &0x3f);
	}
	if (chr<0xf0) {
	    // cx, dx 
	    int chr3= chRead() &0xff;
	    if (chr3==0xff) return -1;
	    if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
	    else return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
	}
	
	//System.out.print((char)j);
	return -1;
//#endif
    }
    
    public void close() {
//#if !(USE_UTF8_READER)
//# 	try { outputWriter.close(); }  catch (Exception e) {};
//# 	try { inputReader.close();  }  catch (Exception e) {};
//#endif
	try { outStream.close();    }  catch (Exception e) {};
	try { inpStream.close();    }  catch (Exception e) {};
	// Alcatel temporary bugfix - this method hangs
	//try { connection.close();   }  catch (Exception e) {};
	new Thread(this).start();
    }

    public void run() {
	// Alcatel temporary bugfix - this method hangs
	try { connection.close();   }  catch (Exception e) {};
    }
    
    public String readLine() throws IOException {
	StringBuffer buf=new StringBuffer();
	/*if (afterEol>0) {
	    buf.append(afterEol);
	    afterEol=0;
	}*/
	
	boolean eol=false;
	while (true) {
	    int c = getNextCharacter();
	    if (c<0) { 
		eol=true;
		if (buf.length()==0) return null;
		break;
	    }
	    if (c==0x0d || c==0x0a) {
		eol=true;
		//inputstream.mark(2);
		if (c==0x0a) break;
	    }
	    else {
		if (eol) {
		    //afterEol=c;
		    //inputstream.reset();
		    break;
		}
		buf.append((char) c);
	    }
	}
	return buf.toString();
    }

    /**
     * Enables inputStream.available() polling before read
     * it is critical for Motorola phones
     */
    public void setStreamWaiting(boolean iStreamWaiting) {  this.iStreamWaiting = iStreamWaiting; }
//#if ZLIB
    public String getStreamStats() {
        StringBuffer stats=new StringBuffer();
        int sent=this.bytesSent;
        int recv=this.bytesRecv;
        if (inpStream instanceof ZInputStream) {
            ZInputStream z = (ZInputStream) inpStream;
            recv+=z.getTotalIn()-z.getTotalOut();
            ZOutputStream zo = (ZOutputStream) outStream;
            sent+=zo.getTotalIn()-zo.getTotalOut();
            stats.append("ZLib:\nin="); stats.append(z.getTotalIn()); stats.append(" inz="); stats.append(z.getTotalOut());
            stats.append("\nout="); stats.append(zo.getTotalOut()); stats.append(" outz="); stats.append(zo.getTotalIn());
        }
        stats.append("\nStream:\nin="); stats.append(recv);
        stats.append("\nout="); stats.append(sent);
        
        return stats.toString();
    }
//#endif
}
