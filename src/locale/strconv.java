package util;
/*
 * strconv.java
 *
 * Created on 12 ßíâàðü 2005 ã., 1:25
 */

/**
 *
 * @author Eugene Stahov
 */
import java.lang.*;

public class strconv {
    
    /** Creates a new instance of strconv */
    private strconv() {
    }
    
    public static final String convAsciiToUnicode(final String s){
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

    public static final String convUnicodeToAscii(final String s){
        if (s==null) return null;
        StringBuffer b=new StringBuffer(s.length());
        for (int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            if (ch==0x401) ch=0xa8; //¨
            if (ch==0x451) ch=0xb8; //¸
            if (ch>0x409) ch+=0xc0-0x410;
            b.append(ch);
            //setCharAt(i, ch);
        }
        return b.toString();
    }
    
}
