/*
 * FilterInputStream.java
 *
 * Created on 30.07.2006, 20:31
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jcraft.jzlib;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author evgs
 */
public class FilterInputStream extends InputStream{

    protected InputStream in;
    
    /** Creates a new instance of FilterInputStream */
    protected FilterInputStream(InputStream in) { this.in=in; }
    
    public int available() throws IOException {  return in.available(); }
    
    public void close() throws IOException { in.close(); }
    
    public void mark(int readlimit) { in.mark(readlimit); }
    
    public boolean markSupported() { return in.markSupported(); }
    
    public int read() throws IOException { return in.read(); }
    
    public int read(byte[] b) throws IOException { return in.read(b); }
    
    public int read(byte[] b, int off, int len) throws IOException { return in.read(b, off, len); }

    public void reset() throws IOException { in.reset(); }
    
    public long skip(long n) throws IOException { return in.skip(n); }

}
