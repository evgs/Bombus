/*
 * Utf8IOStream.java
 *
 * Created on 18 Декабрь 2005 пїЅ., 0:52
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import javax.microedition.io.*;

/**
 *
 * @author EvgS
 */
public class Utf8IOStream implements Runnable{
    
    private StreamConnection connection;
    private InputStream inpStream;
    private OutputStream outStream;

//#if !(USE_UTF8_READER)
    private OutputStreamWriter outputWriter;
    private InputStreamReader inputReader;
//#endif
    
    /** Creates a new instance of Utf8IOStream */
    public Utf8IOStream(StreamConnection connection) throws IOException {
	this.connection=connection;
//#if !(MIDP1)
        try {
            ((SocketConnection)connection).setSocketOption(SocketConnection.KEEPALIVE, 1);
        } catch (Exception e) {}
//#endif
	
	inpStream = connection.openInputStream();
	outStream = connection.openOutputStream();	

//#if !(USE_UTF8_READER)
        inputReader = new InputStreamReader(inpStream, "UTF-8");
	outputWriter = new OutputStreamWriter(outStream,"UTF-8");
//#else
//--      length=pbyte=0;
//#endif


    }
    
    public void send( String data ) throws IOException {
	
	synchronized (outStream) {
//#if !(USE_UTF8_READER)
	    outputWriter.write(data);
//#else
//--	    //byte a[]=toUTF(data);
//--	    //for (int i=0;i<a.length; i++){
//--	    //	System.out.print(" "+((char)a[i])+"="+a[i]);
//--	    //}
//--	    //System.out.println();
//--	    outStream.write(toUTF(data));
//#endif
	    
//#if OUTSTREAM_FLUSH
	    outStream.flush();
//#endif
	}
    }
    
//#if USE_UTF8_READER
//--    // temporary
//--    private byte[] toUTF(String str) {
//--	StringBuffer outbuf=new StringBuffer();
//--	int srcLen = str.length();
//--	for(int i=0; i < srcLen; i++) {
//--	    int c = (int)str.charAt(i);
//--	    //TODO: ескэйпить коды <0x20
//--	    if ((c >= 1) && (c <= 0x7f)) {
//--		outbuf.append( (char) c);
//--		
//--	    }
//--	    if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
//--		outbuf.append((char)(0xc0 | (0x1f & (c >> 6))));
//--		outbuf.append((char)(0x80 | (0x3f & c)));
//--	    }
//--	    if ((c >= 0x800) && (c <= 0xffff)) {
//--		outbuf.append(((char)(0xe0 | (0x0f & (c >> 12)))));
//--		outbuf.append((char)(0x80 | (0x3f & (c >>  6))));
//--		outbuf.append(((char)(0x80 | (0x3f & c))));
//--	    }
//--	}
//--	
//--	int outLen=outbuf.length();
//--	byte bytes[]=new byte[outLen];
//--	for (int i=0; i<outLen; i++) {
//--	    bytes[i]=(byte)outbuf.charAt(i);
//--	}
//--	return bytes;
//--    }
//--
//--    byte cbuf[]=new byte[512];
//--    int length;
//--    int pbyte;
//--    private int chRead() throws IOException{
//--	if (length>pbyte) return cbuf[pbyte++];
//--      /*if (length>pbyte) {
//--	  //System.out.println((char)cbuf[pbyte]);
//--	  return cbuf[pbyte++];
//--      }*/
//--	
//--	//int avail=1;// пїЅпїЅпїЅпїЅпїЅпїЅ Nokia
//--	int avail=inpStream.available();
//--	if (avail<2) return inpStream.read() &0xff;
//--      /*if (avail<2) {
//--	  System.out.println(" single-byte");
//--	  int ch=inputReader.read();
//--	  System.out.println((char)ch);
//--	  return ch;
//--      }*/
//--	
//--	//System.out.println(" prebuffering "+avail);
//--	
//--	length= inpStream.read(cbuf, 0, (avail<512)?avail:512 );
//--	pbyte=1;
//--	
//--	//System.out.println((char)cbuf[0]);
//--	return cbuf[0];
//--    }
//#endif
    
    public int getNextCharacter()
    throws IOException {
//#if !(USE_UTF8_READER)
	return inputReader.read();
//#else
//--	int chr = chRead() &0xff;
//--	if( chr == 0xff ) return -1; // end of stream
//--	
//--	if (chr<0x80) return chr;
//--	if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
//--	
//--        int chr2= chRead() &0xff;
//--        if (chr2==0xff) return -1;
//--        if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
//--	
//--	if (chr<0xe0) {
//--	    // cx, dx 
//--	    return ((chr & 0x1f)<<6) | (chr2 &0x3f);
//--	}
//--	if (chr<0xf0) {
//--	    // cx, dx 
//--	    int chr3= chRead() &0xff;
//--	    if (chr3==0xff) return -1;
//--	    if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
//--	    else return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
//--	}
//--	
//--	//System.out.print((char)j);
//--	return -1;
//#endif
    }
    
    public void close() {
//#if !(USE_UTF8_READER)
	try { outputWriter.close(); }  catch (Exception e) {};
	try { inputReader.close();  }  catch (Exception e) {};
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
}
