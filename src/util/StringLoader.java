package util;

import java.io.IOException;
import java.io.InputStream;
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
    
    String readLine(InputStream inputstream) throws IOException {
	StringBuffer buf=new StringBuffer();
	if (afterEol>0) {
	    buf.append(afterEol);
	    afterEol=0;
	}
	
	boolean eol=false;
	while (true) {
	    int c = inputstream.read();
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
}
