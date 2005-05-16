/*
 * MenuItem.java
 *
 * Created on 2 јпрель 2005 г., 13:22
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
