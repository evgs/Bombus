/*
 * Item.java
 *
 * Created on 19 ќкт€брь 2005 г., 22:27
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ServiceDiscovery;

import Client.StaticData;
import ui.IconTextElement;
import ui.ImageList;

/**
 *
 * @author EvgS
 */
public class Node extends IconTextElement{

    private String node;
    private String name;
    
    public int getImageIndex() { return ImageList.ICON_COLLAPSED_INDEX; }
    public int getColor() { return 0; }
    /** Creates a new instance of Item */
    public Node(String name, String node) {
        super(StaticData.getInstance().rosterIcons);
        this.name=name;
        this.node=node;
    }
    
    public String getName() { return name; }
    public String getNode() { return node; }

    public String toString() { return (name!=null)? name:node; }
    
}
