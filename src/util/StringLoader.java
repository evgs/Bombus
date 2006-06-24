/*
 * StringLoader.java
 *
 * Created on 25 Ноябрь 2005 г., 1:25
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */
package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

public class StringLoader {
    
    int afterEol;
    
    public Vector[] stringLoader(String resource, int columns) {
	StringBuffer buf = new StringBuffer();
	Vector table[] = new Vector[columns];
	for (int i = 0; i<columns; i++) {
	    table[i]=new Vector();
	}
	
	afterEol=0;
	InputStream in = this.getClass().getResourceAsStream(resource);
	try {
	    while (true) {
		String line=readLine(in);
		if (line==null)  break;
		
		if (line.startsWith("//")) continue; // skip all remarks

		int indexFrom=0;
		
		for (int i = 0; i<columns; i++) {
		    String cell=null;
		    try {
			int indexTo=line.indexOf(0x09, indexFrom);
			
			if (indexTo<0) indexTo=line.length();
			if (indexFrom<indexTo) cell=line.substring(indexFrom, indexTo);
			indexFrom=indexTo+1;
		    } catch (Exception e) { e.printStackTrace(); }
		    
		    table[i].addElement( cell );
		}
	    }
	    in.close();
	} catch (Exception e)	{ e.printStackTrace();}
	return table;
    }
    
    public Hashtable hashtableLoader(String resource) {
	Hashtable hash = new Hashtable();
	
	afterEol=0;
	InputStream in = this.getClass().getResourceAsStream(resource);
	try {
	    while (true) {
		String line=readLine(in);
                String key, value;
		if (line==null)  break;
		
		if (line.startsWith("//")) continue; // skip all remarks

                String cell=null;
                try {
                    int indexTab=line.indexOf(0x09);
                    
                    if (indexTab<=0) continue; // process next line
                    
                    key=line.substring(0, indexTab);
                    value=line.substring(indexTab+1, line.length() );
                    hash.put(key, value);
                } catch (Exception e) { e.printStackTrace(); }
	    }
	    in.close();
	} catch (Exception e)	{ /* Empty file or not found */}
	return hash;
    }
    
    String readLine(InputStream inputstream) throws IOException {
	StringBuffer buf=new StringBuffer();
	if (afterEol>0) {
	    buf.append(afterEol);
	    afterEol=0;
	}
	
	boolean eol=false;
	while (true) {
	    int c = getUtfChar(inputstream);
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
		    afterEol=c;
		    //inputstream.reset();
		    break;
		}
		buf.append((char) c);
	    }
	}
	return buf.toString();
    }

    int getUtfChar(InputStream is) throws IOException {
        int chr = is.read();
        if( chr == 0xff ) return -1; // end of stream

	if (chr<0x80) return chr;
	if (chr<0xc0) throw new IOException("Bad UTF-8 Encoding encountered");
        int chr2= is.read() &0xff;
        if (chr2==0xff) return -1;
        if (chr2<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
	
	if (chr<0xe0) {
	    // cx, dx 
	    return ((chr & 0x1f)<<6) | (chr2 &0x3f);
	}
	if (chr<0xf0) {
	    // cx, dx 
	    int chr3= is.read() &0xff;
	    if (chr3==0xff) return -1;
	    if (chr3<0x80) throw new IOException("Bad UTF-8 Encoding encountered");
	    else return ((chr & 0x0f)<<12) | ((chr2 &0x3f) <<6) | (chr3 &0x3f);
	}
	
	//System.out.print((char)j);
	return -1;            
    }
}
