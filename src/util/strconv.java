/*
 * strconv.java
 *
 * Created on 12 Январь 2005 г., 1:25
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

/**
 *
 * @author Eugene Stahov
 */
package util;
import java.lang.*;

public class strconv {
    
    /** Creates a new instance of strconv */
    private strconv() {
    }
    
    public static final String convCp1251ToUnicode(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch>0xbf) ch+=0x410-0xc0;
            if (ch==0xa8) ch=0x401;
            if (ch==0xb8) ch=0x451;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public static final String convUnicodeToCp1251(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch==0x401) ch=0xa8; //Ё
            if (ch==0x451) ch=0xb8; //ё
            if (ch>0x409) ch+=0xc0-0x410;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
    public final static String toBase64( String source) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        int len=source.length();
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source.charAt(i))<<8;
            if ((i+1) < len) {
                val |= (0xFF & source.charAt(i+1));
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source.charAt(i+2));
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public final static String toBase64( byte source[]) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        int len=source.length;
        char[] out = new char[((len+2)/3)*4];
        for (int i=0, index=0; i<len; i+=3, index +=4) {
            boolean trip=false;
            boolean quad=false;
            
            int val = (0xFF & source[i])<<8;
            if ((i+1) < len) {
                val |= (0xFF & source[i+1]);
                trip = true;
            }
            val <<= 8;
            if ((i+2) < len) {
                val |= (0xFF & source[i+2]);
                quad = true;
            }
            out[index+3] = alphabet.charAt((quad? (val & 0x3F): 64));
            val >>= 6;
            out[index+2] = alphabet.charAt((trip? (val & 0x3F): 64));
            val >>= 6;
            out[index+1] = alphabet.charAt(val & 0x3F);
            val >>= 6;
            out[index+0] = alphabet.charAt(val & 0x3F);
        }
        return new String(out);
    }
    
    public static StringBuffer toUTFSb(StringBuffer str) {
        int srcLen = str.length();
        StringBuffer outbuf=new StringBuffer( srcLen );
        for(int i=0; i < srcLen; i++) {
            int c = (int)str.charAt(i);
            //TODO: ескэйпить коды <0x20
            if ((c >= 1) && (c <= 0x7f)) {
                outbuf.append( (char) c);
                
            }
            if (((c >= 0x80) && (c <= 0x7ff)) || (c==0)) {
                outbuf.append((char)(0xc0 | (0x1f & (c >> 6))));
                outbuf.append((char)(0x80 | (0x3f & c)));
            }
            if ((c >= 0x800) && (c <= 0xffff)) {
                outbuf.append(((char)(0xe0 | (0x0f & (c >> 12)))));
                outbuf.append((char)(0x80 | (0x3f & (c >>  6))));
                outbuf.append(((char)(0x80 | (0x3f & c))));
            }
        }
        return outbuf;
    }
    
    
    public static String unicodeToUTF(String src) {
        return toUTFSb(new StringBuffer(src)).toString();
    }
    
    public static String toLowerCase(String src){
        StringBuffer dst=new StringBuffer(src);
        int len=dst.length();
        for (int i=0; i<len; i++) {
            char c=dst.charAt(i);
            if (c>'A'-1 && c<'Z'+1) c+='a'-'A';         // default latin chars
            if (c>0x40f && c<0x430) c+=0x430-0x410;     // cyrillic chars
            // TODO: other schemes by request
            dst.setCharAt(i, c);
        }
        return dst.toString();
    }
}
