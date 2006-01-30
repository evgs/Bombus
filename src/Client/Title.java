/*
 * Title.java
 *
 * Created on 29 январь 2006 г., 1:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

import images.RosterIcons;
import ui.ComplexString;

/**
 *
 * @author Evg_S
 */
public class Title extends ComplexString{
    
    /** Creates a new instance of Title
     * @param size число полей создаваемого ComplexString
     * @param first первое поле ComplexString
     * @param second второе поле ComplexString
     * @return созданный объект ComplexString, присоединЄнный в качестве заголовка
     */
    public Title(int size, Object first, Object second) {
        this (size);
        if (first!=null) setElementAt(first,0);
        if (second!=null) setElementAt(second,1);
    }
    
    public Title(Object obj) {
        this(1, obj, null);
    }
    
    public Title(int size) {
        super (RosterIcons.getInstance());
        setSize(size);
    }
}
