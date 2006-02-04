/*
 * Item.java
 *
 * Created on 19 ќкт€брь 2005 г., 22:27
 *
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ServiceDiscovery;

import Client.StaticData;
import images.RosterIcons;
import ui.Colors;
import ui.IconTextElement;
import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class Node extends IconTextElement{

    private String node;
    private String name;
    
    public int getImageIndex() { return RosterIcons.ICON_COLLAPSED_INDEX; }
    public int getColor() { return Colors.LIST_INK; }
    /** Creates a new instance of Item */
    public Node(String name, String node) {
        super(RosterIcons.getInstance());
        this.name=name;
        this.node=node;
    }
    
    public String getName() { return name; }
    public String getNode() { return node; }

    public String toString() { return (name!=null)? name:node; }
    
}
