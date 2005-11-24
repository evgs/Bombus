/*
 * MenuItem.java
 *
 * Created on 2 јпрель 2005 г., 13:22
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

/**
 *
 * @author Eugene Stahov
 */
public class MenuItem extends ComplexString
{
    
    /** Creates a new instance of MenuItem */
    public int index;
    public MenuItem(String name, int index) {
        addElement(" ");
        addElement(name);
        this.index=index;
    }
}
