/*
 * SmileTree.java
 *
 * Created on 6 ‘евраль 2005 г., 19:38
 */

package Messages;

import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.Font;

import ui.*;
import Client.Msg;
/**
 *
 * @author Eugene Stahov
 */
public final class MessageParser {
    
    public int Smile=-1;   // нет смайлика в узле
    private String chs;     // символы смайликов
    private Vector child;
    
    /** Creates a new instance of SmileTree */
    public MessageParser() {
        child=new Vector ();
        chs=new String();
    }
    /**
     * Smile table loader
     * @param resource - path to smiles-description text file
     * @param smileTable - (result) Vector of smile's string-representations
     */
    
    public MessageParser(String resource, Vector smileTable) {
        this();
        // opening file;
        try { // generic errors
            
            // счЄт номера строки, он же номер смайла
            int strnumber=0;
            // вложение
            int level=0; 
            boolean strhaschars=false;
            boolean endline=false;
            
            MessageParser p=this,   // этой ссылкой будем ходить по дереву
                    p1;
            
            InputStream in=this.getClass().getResourceAsStream(resource);
            DataInputStream f=new DataInputStream(in);
            
            StringBuffer s=new StringBuffer(10);
            boolean firstSmile=true;
            
            try { // eof
                char c;
                while (true) {
                    c=(char)f.readUnsignedByte();
                    
                    switch (c) {
                        case 0x0d:
                        case 0x0a:
                            if (strhaschars) endline=true; else break;
                        case 0x09:
                        //case 0x20:
                            // конец строки смайлика - ставим его номер
                            p.Smile=strnumber;
                            
                            if (firstSmile) smileTable.addElement(s.toString());
                            s.setLength(0);
                            //s=new StringBuffer(6);
                            firstSmile=false;
                            
                            p=this;// в начало дерева
                            break;
                        default:
                            if (firstSmile) s.append(c);
                            strhaschars=true;
                            p1=p.findChild(c);
                            if (p1==null) {
                                p1=new MessageParser();
                                p.addChild(c,p1);
                            }
                            p=p1;
                    }
                    if (endline) {
                        endline=strhaschars=false;
                        strnumber++;
                        firstSmile=true;
                    }
                }
            } catch (EOFException e) { f.close(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addChild(char c, MessageParser child){
        this.child.addElement(child);
        chs=chs+c;
    }
    public MessageParser findChild(char c){
        int index=chs.indexOf(c);
        return (index==-1)?null:(MessageParser)child.elementAt(index);
    }

    public Vector parseMsg(
            Msg msg, 
            ImageList il,       //!< если null, то смайлы игнорируютс€
            int width, 
            boolean singleLine, //!< парсить только одну строку из сообщени€
            NotifyAddLine notify
            )
    {
        Vector v=new Vector();
        
        int w=0;
        int state=0;
        if (msg.subject==null) state=1;
        while (state<2) {
            StringBuffer s=new StringBuffer();
            ComplexString l=new ComplexString(il);
            Font f=l.getFont();
        
        
            if (singleLine) width-=f.charWidth('>');
            
            String txt=(state==0)? msg.subject: msg.toString();
            int color=(state==0)? 0xa00000:0x000000;
            l.setColor(color);
            
            int i=0;
            if (txt!=null)
            while (i<txt.length()) {
                MessageParser p1,p=this;
                int smileIndex=-1;
                int smileStart=i;
                int smileEnd=i;
                while (i<txt.length()) {
                    char c=txt.charAt(i);

                    if (il==null) break;

                    p1=p.findChild(c);
                    if (p1==null) break;    //этот символ c не попал в смайл
                    p=p1;
                    if (p.Smile!=-1) {
                        // нашли смайл
                        smileIndex=p.Smile;
                        smileEnd=i;
                    }
                    i++; // продолжаем поиск смайла
                }
                if (smileIndex!=-1) {
                    // есть смайлик
                    // добавим строку
                    if (s.length()>0) l.addElement(s.toString());
                    // очистим
                    s.setLength(0);
                    // добавим смайлик
                    int iw=il.getWidth();
                    if (w+iw>width) {
                        if (singleLine) {
                            // возврат одной строки
                            l.addRAlign();
                            l.addElement(">");
                            return l;
                        }
                        v.addElement(l);    // добавим l в v
                        if (notify!=null) notify.notifyRepaint(v);
                        l=new ComplexString(il);     // нова€ строка
                        l.setColor(color);
                        w=0;
                    }
                    l.addImage(smileIndex); w+=iw;
                    // передвинем указатель
                    i=smileEnd;
                } else {
                    // символ в строку-накопитель
                    i=smileStart;
                    char c=txt.charAt(i);
                    int cw=f.charWidth(c);
                    if (w+cw>width || c==0x0d || c==0x0a || c==0xa0) {
                        l.addElement(s.toString());    // последн€€ подстрока в l
                        s.setLength(0); w=0;

                        if (c==0xa0) l.setColor(0x904090);

                        if (singleLine) {
                            // возврат одной строки
                            l.addRAlign();
                            l.addElement(">");
                            return l;
                        }

                        v.addElement(l);    // добавим l в v
                        if (notify!=null) notify.notifyRepaint(v);
                        l=new ComplexString(il);     // нова€ строка
                        l.setColor(color);
                    }
                    if (c>0x1f) {  s.append(c); w+=cw; }
                }
                i++;
            }
            if (s.length()>0) l.addElement(s.toString());

            if (singleLine) return l;   // возврат одной строки

            if (!l.isEmpty()) v.addElement(l);  // последн€€ строка

            if (notify!=null) {
                notify.notifyRepaint(v);
                notify.notifyFinalized();
            }
            state++;
        }
        return v;

    }

    public interface NotifyAddLine {
        void notifyRepaint(Vector v);
        void notifyFinalized();
    }
}
