/*
 * Title.java
 *
 * Created on 29 ������ 2006 �., 1:00
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
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
     * @param size ����� ����� ������������ ComplexString
     * @param first ������ ���� ComplexString
     * @param second ������ ���� ComplexString
     * @return ��������� ������ ComplexString, ������������� � �������� ���������
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
