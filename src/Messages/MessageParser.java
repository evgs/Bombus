/*
 * SmileTree.java
 *
 * Created on 6.02.2005, 19:38
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

package Messages;

import images.SmilesIcons;
import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.Font;

import ui.*;
import Client.Msg;
import Client.Config;
/**
 *
 * @author Eugene Stahov
 */
public final class MessageParser implements Runnable{
    
    private final static int URL=-2;
    private final static int NOSMILE=-1;
    private Vector smileTable;
    
    private Leaf root;

    // Singleton
    private static MessageParser instance=null;
    
    private int width; // window width
    
    private ImageList il;
    
    private Vector tasks=new Vector();
    
    private Thread thread;
    boolean wordsWrap;
    private static String wrapSeparators=" .,-=/\\;:+*()[]<>~!@#%^_&";
    
    public static MessageParser getInstance() {
        if (instance==null) instance=new MessageParser("/images/smiles.txt");
        return instance;
    }
    /**
     * smile table loader
     * 
     * @param resource - path to smiles-description text file
     * @param smileTable - (result) Vector of smile's string-representations
     */
    
    public Vector getSmileTable() { return smileTable; }
    
    private class Leaf {
        public int smile=NOSMILE;   // нет смайлика в узле
        public String smileChars;     // символы смайликов
        public Vector child;

        public Leaf() {
            child=new Vector();
            smileChars=new String();
        }
        
        public Leaf findChild(char c){
            int index=smileChars.indexOf(c);
            return (index==-1)?null:(Leaf)child.elementAt(index);
        }

        private void addChild(char c, Leaf child){
            this.child.addElement(child);
            smileChars=smileChars+c;
        }
    }
    
    private void addSmile(String smile, int index) {
	Leaf p=root;   // этой ссылкой будем ходить по дереву
	Leaf p1;
	
	int len=smile.length();
	for (int i=0; i<len; i++) {
	    char c=smile.charAt(i);
	    p1=p.findChild(c);
	    if (p1==null) {
		p1=new Leaf();
		p.addChild((char)c,p1);
	    }
	    p=p1;
	}
	p.smile=index;
    }
    
    private MessageParser(String resource) {
        
        smileTable=new Vector();
        root=new Leaf();
        // opening file;
        try { // generic errors
            
            // счёт номера строки, он же номер смайла
            int strnumber=0;
            // вложение
            // int level=0; 
            boolean strhaschars=false;
            boolean endline=false;
            
            InputStream in=this.getClass().getResourceAsStream(resource);
            //DataInputStream f=new DataInputStream(in);
            
            StringBuffer s=new StringBuffer(10);
            boolean firstSmile=true;
            
            //try { // eof
                int c;
                while (true) {
                    c=in.read();
                    //System.out.println(c);
                    if (c<0) break;
                    switch (c) {
                        case 0x0d:
                        case 0x0a:
                            if (strhaschars) endline=true; else break;
                        case 0x09:
                        //case 0x20:
                            // конец строки смайлика
                            
			    String smile=s.toString();
                            if (firstSmile) smileTable.addElement(smile);
			    
			    addSmile(smile,strnumber);
			    
                            s.setLength(0);
                            //s=new StringBuffer(6);
                            firstSmile=false;
                            
                            break;
                        default:
                            s.append((char)c);
                            strhaschars=true;
                    }
                    if (endline) {
                        endline=strhaschars=false;
                        strnumber++;
                        firstSmile=true;
                    }
                }
            //} catch (Exception e) { /* неправильный файл смайлов */ }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	addSmile("http://", URL);
        addSmile("\01", ComplexString.NICK_ON);
        addSmile("\02", ComplexString.NICK_OFF);
    }

    public void parseMsg(MessageItem messageItem,  int width)
    {
        synchronized (tasks) {
            wordsWrap=Config.getInstance().textWrap==1;
            messageItem.msgLines=new Vector();
            this.il=(messageItem.smilesEnabled())? SmilesIcons.getInstance() : null;
            this.width=width;

            if (tasks.indexOf(messageItem)>=0) return;

            tasks.addElement(messageItem);
            if (thread==null) {
                thread=new Thread(this);
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();
            }
        }
        return;
    }
    
    public void run() {
        while(true) {
            
            MessageItem task=null;
            synchronized (tasks) {
                if (tasks.size()==0) {
                    thread=null;
                    return;
                }
                task=(MessageItem) tasks.lastElement();
            }
            
            parseMessage(task);
            
            synchronized (tasks) {
                tasks.removeElement(task);
            }
        }
    }

    private void parseMessage(final MessageItem task) {
        
        Vector lines=task.msgLines;
        boolean singleLine=task.msg.itemCollapsed;
        
        //boolean noWrapSpace=false;
        boolean underline=false;
        
        int state=0;
        if (task.msg.subject==null) state=1;
        while (state<2) {
            int w=0;
            StringBuffer s=new StringBuffer();
	    int wordWidth=0;
	    int wordStartPos=0;
            
            ComplexString l=new ComplexString(il);
            lines.addElement(l);
            
            Font f=(task.msg.isHighlited())? FontCache.getMsgFontBold(): FontCache.getMsgFont();
            l.setFont(f);
            
            String txt=(state==0)? task.msg.subject: task.msg.toString();
            
            int color=(state==0)?
                Colors.MSG_SUBJ:
                Colors.LIST_INK;
            l.setColor(color);
           
            if (txt==null) {
                state++;
                continue;
            }
            
            int pos=0;
            while (pos<txt.length()) {
                Leaf smileLeaf=root;
                int smileIndex=-1;
                int smileStartPos=pos;
                int smileEndPos=pos;
                
                while (pos<txt.length()) {
                    char c=txt.charAt(pos);
                    
                    if (underline) {
                        switch (c) {
                            case ' ':
                            case 0x09:
                            case 0x0d:
                            case 0x0a:
                            case 0xa0:
                            case ')':
                                underline=false;
                                if (wordStartPos!=pos) {
                                    s.append(txt.substring(wordStartPos,pos));
                                    wordStartPos=pos;
				    w+=wordWidth;
                                    wordWidth=0;
                                }
                                if (s.length()>0) {
                                    l.addUnderline();
                                    l.addElement(s.toString());
                                }
                                s.setLength(0);
                        }
                        break; // не смайл
                    }
                    
                    smileLeaf=smileLeaf.findChild(c);
                    if (smileLeaf==null) {
                        break;    //этот символ c не попал в смайл
                    }
                    if (smileLeaf.smile!=-1) {
                        // нашли смайл
                        smileIndex=smileLeaf.smile;
                        smileEndPos=pos;
                    }
                    pos++; // продолжаем поиск смайла
                    
                } //while (i<txt.length())
                
                if (smileIndex==URL) {
                    if (s.length()>0) l.addElement(s.toString());
                    s.setLength(0);
                    underline=true;
                }
                
                if (smileIndex>=0 && task.smilesEnabled()) {
                    // есть смайлик
                    
                    // слово перед смайлом в буфер
 		    if (wordStartPos!=smileStartPos) {
 			s.append(txt.substring(wordStartPos, smileStartPos));
                        w+=wordWidth;
                        wordWidth=0;
 		    }
                    // добавим строку
                    if (s.length()>0) {
                        if (underline) l.addUnderline();
                        l.addElement(s.toString());
                    }
                    // очистим
                    s.setLength(0);
                    // добавим смайлик
                    int iw=(smileIndex<0x01000000)? il.getWidth() : 0;
                    if (w+iw>width) {
                        task.notifyRepaint(lines, task.msg, false);
                        l=new ComplexString(il);     // новая строка
                        lines.addElement(l);    // добавим l в v
                        
                        if (singleLine) {
                            return;
                        }
                        
                        l.setColor(color);
                        l.setFont(f);
                        w=0;
                    }
                    l.addImage(smileIndex); w+=iw;
                    // передвинем указатель
                    pos=smileEndPos;
		    // next word will start after smile
		    wordStartPos=pos+1;
                } else {
                    pos=smileStartPos;
                    char c=txt.charAt(pos);
                    
                    int cw=f.charWidth(c);
                    if (c!=0x20) {
                        boolean newline= ( c==0x0d || c==0x0a /*|| c==0xa0*/ );
			if (wordWidth+cw>width || newline) {
			    // Add current oneWord buffer to s because:
			    // word is too long to fit in line or character is newline

			    s.append(txt.substring(wordStartPos,pos));
			    w+=wordWidth;
			    wordWidth=0;
			    wordStartPos=pos;
                            if (newline) wordStartPos++;
			}
                        if (w+wordWidth+cw>width || newline) {
                            if (underline) l.addUnderline();
                            l.addElement(s.toString());    // последняя подстрока в l
                            s.setLength(0); w=0;
                            
                            if (c==0xa0) l.setColor(Colors.MSG_HIGHLIGHT);
                            
                            l=new ComplexString(il);     // новая строка
                            lines.addElement(l);    // добавим l в v
                            task.notifyRepaint(lines, task.msg, false);

                            if (singleLine) {
                                return;
                            }
                            
                            l.setColor(color);
                            l.setFont(f);
                        }
                    }
		    if (c==0x09)
			c=0x20;
		    	    
                    if (c>0x1f) {
			wordWidth+=cw;
		    }
		    if (wrapSeparators.indexOf(c)>=0 || !wordsWrap) {
			if (pos>wordStartPos) 
                            s.append(txt.substring(wordStartPos,pos));
			if (c>0x1f) s.append(c);
			w+=wordWidth;
			wordStartPos=pos+1;
			wordWidth=0;
		    }
                }
                pos++;
            }
	    if (wordStartPos!=pos)
		s.append(txt.substring(wordStartPos,pos));
            if (s.length()>0) {
                if (underline) {
                    l.addUnderline();
                }
                l.addElement(s.toString());
            }
            
            if (l.isEmpty()) lines.removeElementAt(lines.size()-1);  // последняя строка
            
            task.notifyRepaint(lines, task.msg, true);
            state++;
        }
    }

    public interface MessageParserNotify {
        void notifyRepaint(Vector v, Msg parsedMsg, boolean finalized);
	//void notifyUrl(String url);
    }
}
