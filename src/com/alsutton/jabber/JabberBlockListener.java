/*
 * ServiceDiscoveryListener.java
 *
 * Created on 4 Èþíü 2005 ã., 21:51
 */

package com.alsutton.jabber;

/**
 *
 * @author Evg_S
 */
public interface JabberBlockListener {
    
  /**
   * Method to handle an incomming block.
   *
   * @parameter data The incomming block
   */

  public void blockArrived( JabberDataBlock data );
    
}
