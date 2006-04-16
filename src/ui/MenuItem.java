/*
 * MenuItem.java
 *
 * Created on 2 Апрель 2005 г., 13:22
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

/**
 *
 * @author Eugene Stahov
 */
public class MenuItem extends IconTextElement
{
    
    /** Creates a new instance of MenuItem */
    public int index;
    private String name;
    
    public MenuItem(String name, int index) {
	super(null);
        this.index=index;
	this.name=name;
    }

    protected int getImageIndex() { return -1;  }
    public int getColor() { return Colors.LIST_INK; }
    public String toString(){ return name; }
}
