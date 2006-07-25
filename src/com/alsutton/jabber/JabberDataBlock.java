/*
  Copyright (c) 2000, Al Sutton (al@alsutton.com)
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

package com.alsutton.jabber;
import Client.NvStorage;
import java.util.*;

/**
 * Title:        JabberDataBlock.java
 * Description:  The base class for Jabber datablocks objects in the datablock sub package
 */

public class JabberDataBlock
{
    private final static int MAX_CHILDS=400;
  /**
   * The name of this tag
   */

  private String tagName;

  /**
   * The list of child blocks inside this block
   */

  protected Vector childBlocks;

  /**
   * A string representing all the text within the data block
   */

  protected String textData = null;

  /**
   * This blocks' parent
   */

  protected JabberDataBlock parent;

  /**
   * The list of attributes in this tag
   */

  protected Hashtable attributes;

  /**
   * Constructor
   */

  public JabberDataBlock( )
  {
    this( "unknown", null, null );
  }

  /**
   * Constructor
   *
   * @param parent The parent of this data block
   */

  //public JabberDataBlock( JabberDataBlock _parent )
  //{
  //  this( "unknown", _parent, null );
  //}

  /**
   * Constructor including an Attribute list
   *
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  public JabberDataBlock( JabberDataBlock _parent, Hashtable _attributes )
  {
    this( "unknown", _parent, _attributes );
  }

  /**
   * Constructor including an Attribute list
   *
   * @param _tagName The name of the block
   * @param _parent The parent of this datablock
   * @param _attributes The list of element attributes
   */

  public JabberDataBlock( String _tagName, JabberDataBlock _parent, Hashtable _attributes )
  {
    parent = _parent;
    attributes = _attributes;
    tagName = _tagName;
  }

  public JabberDataBlock( JabberDataBlock _parent, String _tagName, String _body  )
  {
    this( _tagName, _parent, null );
    setText(_body);
  }
  /**
   * Method to add a child to the list of child blocks
   *
   * @param newData The child block to add
   */

  public void addChild( Object newData ) {
      if( childBlocks == null )
	  childBlocks = new Vector();
      if (childBlocks.size()<MAX_CHILDS)
	  childBlocks.addElement( newData );
  }
  
  /**
   * Method to add a simple child to the list of child blocks
   *
   * @param name The child block name to add
   * @param text The child block text body to add
   */
  public JabberDataBlock addChild(String name, String text){
      JabberDataBlock child=new JabberDataBlock(name,this,null);
      if (text!=null) child.setText(text);
      addChild(child);
      return child;
  }


  /**
   * Method to add some text to the text buffer for this block
   *
   * @param text The text to add
   */

  public void setText( String text ) { textData=text; }

  /**
   * Method to get the parent of this block
   *
   * @return This blocks parent
   */

  public JabberDataBlock getParent() { return parent; }


  /**
   * Method to return the data as a byte stream ready to send over
   * the wire
   *
   * @return The data to send as a byte array
   */

  public byte[] getBytes()
  {
    String data = toString();
    return data.getBytes();
  }

  /**
   * Method to get the text element of this block
   *
   * @return The text contained in this block
   */

  public String getText()
  {
    return (textData==null)?"":textData.toString();
  }

  /**
   * Method to get an attribute
   *
   * @param attributeName The name of the attribute to get
   * @return The value of the attribute
   */

  public String getAttribute( String attributeName )
  {
    if (attributes==null) return null;
    return (String) attributes.get( attributeName );
  }
  
  public String getTypeAttribute(){
      return getAttribute("type");
  }
  
  public boolean isJabberNameSpace(String xmlns){
      String xmlnsatr=getAttribute("xmlns");
      if (xmlnsatr==null) return false;
      return xmlnsatr.startsWith(xmlns);
  } 

  public JabberDataBlock findNamespace(String xmlns) {
      if (childBlocks==null) return null;
      for (Enumeration e=childBlocks.elements(); e.hasMoreElements();){
          JabberDataBlock d=(JabberDataBlock)e.nextElement();
          if (d.isJabberNameSpace(xmlns)) return d;
      }
      return null;
  }
  
  public void setNameSpace(String xmlns){
      setAttribute("xmlns", xmlns);
  }
  /**
   * Method to set an attribute value
   *
   * @param attributeName The name of the attribute to set
   * @param value The value of the attribute
   */

  public void setAttribute( String attributeName, String value )
  {
    if( attributeName == null )
      return;

    if( attributes == null )
      attributes = new Hashtable();

    if (value!=null) attributes.put( attributeName, value );
    else attributes.remove(attributeName);
  }

  public void setTypeAttribute( String value ) {
      setAttribute("type",value);
  }
  
  /**
   * Returns a vector holding all of the children of this block
   *
   * @param Vector holding all the children
   */

  public Vector getChildBlocks()
  {
    return (Vector) childBlocks;
  }

  /**
   * Returns a child block by  the tagName
   *
   */

  public JabberDataBlock getChildBlock(String byTagName)
  {
    if (childBlocks==null) return null;
    for (Enumeration e=childBlocks.elements(); e.hasMoreElements();){
        JabberDataBlock d=(JabberDataBlock)e.nextElement();
        if (d.getTagName().equals(byTagName)) return d;
    }
    return null;
  }

  /**
   * Returns a child block by text
   *
   */

  public JabberDataBlock getChildBlockByText(String text)
  {
    if (childBlocks==null) return null;
    for (Enumeration e=childBlocks.elements(); e.hasMoreElements();){
        JabberDataBlock d=(JabberDataBlock)e.nextElement();
        if (text.equals(d.getText()) ) return d;
    }
    return null;
  }

  /**
   * Method to return the text for a given child block
   */

  public String getChildBlockText( String blockname )
  {
      try {
        JabberDataBlock child=getChildBlock(blockname);
        return child.getText();
      } catch (Exception e) {}
      return "";
  }
  
      private void appendXML(StringBuffer dest, String src){
        if (src==null) return;
        int len=src.length();
        for (int i=0;i<len;i++){
            char ch=src.charAt(i);
            switch (ch) {
                case '&':   dest.append("&amp;"); break;
                case '"':   dest.append("&quot;"); break;
                case '<':   dest.append("&lt;"); break;
                case '>':   dest.append("&gt;"); break;
                case '\'':  dest.append("&apos;"); break;
                default: dest.append(ch);
            }
        }
    }

  /**
   * Method to convert this into a String
   *
   * @return The element as an XML string
   */

  public String toString()
  {
    StringBuffer data = new StringBuffer( "<" );
    data.append( getTagName() );
    if( attributes != null )
      addAttributeToStringBuffer( data );

    // short xml
    if (textData==null && childBlocks ==null ) {
        data.append("/>");
        return data.toString();
    }
    
    data.append( '>' );
    

    appendXML(data, textData);

    if( childBlocks != null )
    {
      Enumeration e = childBlocks.elements();
      while( e.hasMoreElements() )
      {
        JabberDataBlock thisBlock = (JabberDataBlock) e.nextElement();
        data.append( thisBlock.toString() );
      }
    }

    // end tag
    data.append( "</" );
    data.append( getTagName() );
    data.append( '>' );

    return data.toString();
  }

  /**
   * Method to add all the attributes to a string buffer
   *
   * @param buffer The string buffer to which all the attributes will be added
   */

  protected void addAttributeToStringBuffer( StringBuffer buffer )
  {
    Enumeration e = attributes.keys();
    while( e.hasMoreElements() )
    {
      String nextKey = (String) e.nextElement();
      String nextValue = (String) attributes.get( nextKey );

      buffer.append( ' ' );
      buffer.append( nextKey );
      buffer.append( "=\"" );
      appendXML(buffer, nextValue);
      //buffer.append( nextValue );
      buffer.append( '\"' );
    }
  }

  /**
   * Method to return the start of tag string as a string
   *
   * @return The start tag string
   */

  public StringBuffer getTagStart()
  {
    StringBuffer tagStart = new StringBuffer( "<" );
    tagStart.append( getTagName() );
    if( attributes != null )
      addAttributeToStringBuffer( tagStart );
    tagStart.append( '>' );

    return tagStart;
  }

  /**
   * Method to return the tag name
   *
   * @return The tag name
   */

  public String getTagName()
  {
    return tagName;
  }
}
