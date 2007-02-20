/*
 * Title.java
 *
 * Created on 29.01.2006, 1:00
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package Client;

import images.RosterIcons;
import ui.ComplexString;
import ui.FontCache;

/**
 *
 * @author Evg_S
 */
public class Title extends ComplexString{
    
    /** Creates a new instance of Title
     * @param size число полей создаваемого ComplexString
     * @param first первое поле ComplexString
     * @param second второе поле ComplexString
     * @return созданный объект ComplexString, присоединённый в качестве заголовка
     */
    public Title(int size, Object first, Object second) {
        this (size);
        font=FontCache.getRosterNormalFont();
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
