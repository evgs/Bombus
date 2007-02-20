/*
 * FilterInputStream.java
 *
 * Created on 15.10.2006, 20:31
 *
 * Copyright (c) 2006, Roman Kapl <msntfs@gmail.com>
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
import java.io.OutputStream;
public final class FakeDeflate extends OutputStream {
	private final OutputStream ous;
	private byte[] buf;
	private int bufferUse;
	public FakeDeflate(OutputStream ous) {
		this.ous = ous;
		buf = new byte[1024];
		try {
			byte cMethod = 8;
			byte cInfo = 3;
			byte cm = (byte) (cMethod | (cInfo << 4));
			ous.write(cm);
			byte flags = 0;// bez dictu a fastest
			if ((cm * 256 + flags) % 31 != 0) {
				flags += 31 - ((cm * 256 + flags) % 31);
			}
			ous.write(flags);
			// nulovy fdict
			/*
			 * ous.write(0); ous.write(0); ous.write(0); ous.write(0);
			 */
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}
	public void write(int arg0) throws IOException {
		byte[] b = new byte[1];
		b[0] = (byte) arg0;
	}
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	public void write(byte[] b, int off, int len) throws IOException {
		if (bufferUse + len <= buf.length) {// buffer nam staci len ho
			// zkopirujeme
			for (int i = 0; i < len; i++) {
				buf[bufferUse + i] = b[off + i];
			}
			bufferUse += len;
		} else {
			// nestaci buffer zapiseme maximalne 64tis byte a pak jdeme dale
			int rLen = bufferUse + len;
			boolean wasBigger = false;
			if (rLen > 65536) {
				rLen = 65536;
				wasBigger = true;
			}
			writeBlockHeader(rLen);
			ous.write(buf, 0, bufferUse);
			ous.write(b, off, rLen - bufferUse);
			bufferUse = 0;
			if (wasBigger) {// nemohly jsme zapsat cely block
				write(b, off + rLen, len - rLen);// kolotoc se opakuje
			} else {
				// jsme li ti co zapsaly posledni data
				ous.flush();
			}
		}
	}
	public void close() throws IOException {
		flush();
		ous.write(1);// final block
		int len = 0;
		ous.write(len);
		ous.write(len >>> 8);
		len = ~len;
		ous.write(len);
		ous.write(len >>> 8);
		ous.flush();
		ous.close();
	}
	private void writeBlockHeader(int len) throws IOException {
		ous.write(0);
		ous.write(len);
		ous.write(len >> 8);
		len = ~len;
		ous.write(len);
		ous.write(len >> 8);
	}
	public void flush() throws IOException {
		if (bufferUse > 0) {
			writeBlockHeader(bufferUse);
			ous.write(buf, 0, bufferUse);
			bufferUse = 0;
			ous.flush();
		}
	}
}
