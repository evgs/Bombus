/*
  Copyright (c) 2000,2001 Al Sutton (al@alsutton.com)
  All rights reserved.
  Redistribution and use in source and binary forms, with or without modification, are permitted
  provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice, this list of conditions
  and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright notice, this list of
  conditions and the following disclaimer in the documentation and/or other materials provided with
  the distribution.

  Neither the name of Al Sutton nor the names of its contributors may be used to endorse or promote
  products derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.alsutton.xmlparser;

/**
 * The main XML Parser class.
 */

import java.io.*;

import java.util.*;

public class XMLParser
{
  /** The reader from which the stream is being read  */
//#if USE_UTF8_READER
//--  private InputStream inputReader;
//#else
  private Reader inputReader;
//#endif

  /** The handler for XML Events. */

  private XMLEventListener eventHandler;

  /** The root tag for the document. */

  private String rootTag = null;
  
  private static final int MAX_BLOCK_SIZE=4096-3; //max array for m55=4096?

  /** Constructor, Used to override default dispatcher.
   *
   * @param _eventHandler The event handle to dispatch events through.
   */

  public XMLParser( XMLEventListener _eventHandler )
  {
    eventHandler = _eventHandler;
  }

  private StringBuffer streamData = new StringBuffer(16);
  /**
   * Method to read until an end condition.
   *
   * @param checker The class used to check if the end condition has occurred.
   * @return A string representation of the data read.
   */
  
  private String readUntilEnd( int tagBracket )
    throws IOException, EndOfXMLException
  {
    //StringBuffer streamData = new StringBuffer(16);
    streamData.setLength(0);
    StringBuffer xmlChar = null;
    int inQuote = 0;    // 0 or " or '
    boolean inXMLchar=false;

    int nextChar = getNextCharacter();
    if( nextChar == -1 )
      throw new EndOfXMLException();
    while( nextChar != -1)
            //&& (inQuote == true || checker.shouldStop( nextChar ) == false) )
    {
        if (nextChar==tagBracket)
            if (inQuote==0) break;
        
        if (nextChar==' ')
            if (inQuote==0 && tagBracket=='>') break;
        
        switch (nextChar) {
            case '\'': // '
            case '\"': // "       
                inQuote=( inQuote==0 )? nextChar: 0;
                break;
            case '&':
                inXMLchar=true;
                xmlChar=new StringBuffer(6);
                break;
            case ';':
                if (inXMLchar) {
                    inXMLchar=false;
                    String s=xmlChar.toString();
                    if (s.equals("amp")) nextChar='&'; else
                    if (s.equals("apos")) nextChar='\''; else
                    if (s.equals("quot")) nextChar='\"'; else
                    if (s.equals("gt")) nextChar='>'; else
                    if (s.equals("lt")) nextChar='<'; else
                    if (xmlChar.charAt(0)=='#') 
                        try {
                            xmlChar.deleteCharAt(0);
                            nextChar=Integer.parseInt(xmlChar.toString());
                        } catch (Exception e) {
                            nextChar=' ';
                        }
                }
            default:
                if (!inXMLchar) {
                    if (streamData.length()<MAX_BLOCK_SIZE) 
                        streamData.append( (char) nextChar );
                    else if (streamData.length()==MAX_BLOCK_SIZE)
                        streamData.append("...");
                } else xmlChar.append( (char) nextChar );
        }
        nextChar = getNextCharacter();
    }
    
    if( nextChar != '<' && nextChar != '>')
      streamData.append( (char) '\n' );

    String returnData = streamData.toString();
    return returnData;
  }

//#if USE_UTF8_READER
//--  byte cbuf[]=new byte[512];
//--  int length;
//--  int pbyte;
//--  private int chRead() throws IOException{
//--      if (length>pbyte) return cbuf[pbyte++];
//--      /*if (length>pbyte) {
//--	  //System.out.println((char)cbuf[pbyte]);
//--	  return cbuf[pbyte++];
//--      }*/
//--      
//--      //int avail=1;// тестим Nokia
//--      int avail=inputReader.available();
//--      if (avail<2) return inputReader.read();
//--      /*if (avail<2) {
//--          System.out.println(" single-byte");
//--	  int ch=inputReader.read();
//--          System.out.println((char)ch);
//--	  return ch;
//--      }*/
//--      
//--      //System.out.println(" prebuffering "+avail);
//--      
//--      length= inputReader.read(cbuf, 0, (avail<512)?avail:512 );
//--      pbyte=1;
//--      
//--      //System.out.println((char)cbuf[0]);
//--      return cbuf[0];
//--  }
//#endif
  private int getNextCharacter()
  throws IOException {
//#if !(USE_UTF8_READER)
      return inputReader.read();
//#else
//--      int utfChar = -1;
//--      int j = chRead();
//--      if( j == -1 ) return utfChar;
//--      
//--      j &= 0xff; boolean flag = false;
//--      switch(j >> 4) {
//--          case 8: // '\b'
//--          case 9: // '\t'
//--          case 10: // '\n'
//--          case 11: // '\013'
//--          default:
//--              break;
//--              
//--          case 0: // '\0'
//--          case 1: // '\001'
//--          case 2: // '\002'
//--          case 3: // '\003'
//--          case 4: // '\004'
//--          case 5: // '\005'
//--          case 6: // '\006'
//--          case 7: // '\007'
//--              utfChar = j;
//--              break;
//--              
//--          case 12: // '\f'
//--          case 13: // '\r'
//--              utfChar = j & 0x1f;  utfChar <<= 6;  int k = chRead();
//--              if((k & 0xc0) != 0x80) throw new IOException("Bad UTF-8 Encoding encountered");
//--              utfChar += k & 0x3f; break;
//--              
//--          case 14: // '\016'
//--              utfChar = j & 0xf;  utfChar <<= 6;
//--              int l = chRead();
//--              if((l & 0xc0) != 128) throw new IOException("Bad UTF-8 Encoding encountered");
//--              utfChar += l & 0x3f;  utfChar <<= 6; 
//--              l = chRead();
//--              if((l & 0xc0) != 128)
//--                  throw new IOException("Bad UTF-8 Encoding encountered");
//--              utfChar += l & 0x3f;
//--              break;
//--              
//--      }
//--      //System.out.print((char)j);          
//--      return utfChar;
//#endif
  }
  
  /**
   * Method to handle the reading and dispatch of tag data.
   */

  private boolean handleTag()
    throws IOException, EndOfXMLException
  {
    boolean startTag = true,
            emptyTag = false,
            hasMoreData = true;
    String tagName = null;
    Hashtable attributes = null;

    do
    {
      String data = readUntilEnd ( '>' );
      int substringStart = 0,
          substringEnd = data.length();

      if( data.startsWith( "/" )  )
      {
        startTag = false;
        substringStart++;
      }

      if( data.endsWith( "/" ) )
      {
        emptyTag = true;
        substringEnd--;
      }

      hasMoreData = data.endsWith( "\n" );
      if( hasMoreData )
        substringEnd--;

      data = data.substring( substringStart, substringEnd );

      if( tagName == null )
      {
        tagName = data;//.toLowerCase();
        continue;
      }

      if( attributes == null )
        attributes = new Hashtable();

      int stringLength = data.length();
      int equalitySign = data.indexOf( '=' );
      if( equalitySign == -1 )
      {
        if( hasMoreData )
          continue;
        else
          break;
      }

      String attributeName = data.substring(0, equalitySign);
      int valueStart = equalitySign+1;
      if( valueStart >= data.length() )
      {
        attributes.put( attributeName, "" );
        continue;
      }

      //substringStart = valueStart;
      //char startChar = data.charAt( substringStart );
      //if( startChar  == '\"' || startChar  == '\'' )
      //  substringStart++;

      //substringEnd = stringLength;
      //char endChar = data.charAt( substringEnd-1 );
      //if( substringEnd > substringStart && endChar  == '\"' || endChar  == '\'' )
      //  substringEnd--;

      attributes.put( attributeName, data.substring( valueStart, stringLength ) );
    } while( hasMoreData );

    if( tagName.startsWith( "?") )
      return false;

    //tagName = tagName;//.toLowerCase();
    
    boolean binflag=false;
    if( startTag )
    {
      if( rootTag == null )
        rootTag = tagName;
      binflag=eventHandler.tagStarted( tagName, attributes);
    }

    if( emptyTag || !startTag )
    {
      eventHandler.tagEnded( tagName );
      if( rootTag != null && tagName.equals( rootTag ) )
        throw new EndOfXMLException();
    }
    
    return binflag;
  }

  /**
   * Method to handle the reading in and dispatching of events for plain text.
   */

  private void handlePlainText()
    throws IOException, EndOfXMLException
  {
    String data = readUntilEnd ( '<' );
    eventHandler.plaintextEncountered( data );
  }

  private void handleBinValue() 
    throws IOException, EndOfXMLException
  {
      int len=0;
      int ibuf=1;
      ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);
      while (true) {
          int nextChar = getNextCharacter();
          if( nextChar == -1 )
              throw new EndOfXMLException();
          int base64=-1;
          if (nextChar>'A'-1 && nextChar<'Z'+1) base64=nextChar-'A';
          else if (nextChar>'a'-1 && nextChar<'z'+1) base64=nextChar+26-'a';
          else if (nextChar>'0'-1 && nextChar<'9'+1) base64=nextChar+52-'0';
          else if (nextChar=='+') base64=62;
          else if (nextChar=='/') base64=63;
          else if (nextChar=='=') {base64=0; len++;}
          else if (nextChar=='<') break;
          if (base64>=0) ibuf=(ibuf<<6)+base64;
          if (ibuf>=0x01000000){
              baos.write((ibuf>>16) &0xff);
              if (len==0) baos.write((ibuf>>8) &0xff);
              if (len<2) baos.write(ibuf &0xff);
              //len+=3;
              ibuf=1;
          }
      }
      baos.close();
      //System.out.println(ibuf);
      //System.out.println(baos.size());
      eventHandler.binValueEncountered( baos.toByteArray() );
  }
  /**
   * The main parsing loop.
   *
   * @param _inputReader The reader for the XML stream.
   */

  public void  parse ( 
//#if USE_UTF8_READER
//--          InputStream _inputReader 
//#else
          Reader _inputReader 
//#endif
          )
    throws IOException
  {
//#if USE_UTF8_READER
//--      length=pbyte=0;
//#endif
    inputReader = _inputReader;
    boolean binval=false;
    
    try {
        while( true ) {
            if (binval)
                handleBinValue();
            else
                handlePlainText();
            binval=handleTag();
        }
    } catch( EndOfXMLException x ) {
        // The EndOfXMLException is purely used to drop out of the
        // continuous loop.
    } catch ( Exception e ) {
        e.printStackTrace();
    }
 }
}
