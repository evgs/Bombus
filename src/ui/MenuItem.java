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
    public MenuItem(String name) {
        addElement(" ");
        addElement(name);
    }
    
    public void action(){}
    
    //public String toString(){ return name;}
}
