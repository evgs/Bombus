/*
 * VirtualElement.java
 *
 * Created on 29 Март 2005 г., 0:13
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;
import javax.microedition.lcdui.*;

/**
 * интерфейс виртуального элемента списка.
 * @author Eugene Stahov
 */
public interface VirtualElement {
    
    /**
     * высота элемента
     * @return высота элемента в пикселах
     */
    public int getVHeight();
    
    /**
     * высоты строк элементов
     * используются при скроллировании.
     * возвращает null, если элемент не делится на строки
     * @return массив с высотами строк элемента
     */
    public int[] getLinesHeight();
    
    /**
     * ширина элемента
     * @return ширина элемента в пикселах
     */
    public int getVWidth();
    
    /**
     * 
     * цвет заполнения фона элемента
     * фон закрашивается автоматически перед вызовом drawItem
     * @return RGB-цвет заполнения фона элемента
     */
    public int getColorBGnd(); 
    
    /**
     * цвет чернил элемента
     * устанавливается перед вызовом drawItem
     * @return RGB-цвет чернил элемента
     */
    public int getColor(); 
    
    /**
     * 
     * отрисовка элемента. перед вызовом устанавливаются 
     * трансляция экранных координат <i>translate(x,y)</i> в позицию элемента
     * и обрезание <i>setClip(0,0,width,height)</i>. 
     * 
     * фон закрашивается автоматически перед вызовом drawItem
     * @param g контекст отрисовки элемента
     * @param ofs горизонтальное смещение скроллируемой части элемента
     * @param selected признак выбранного курсором элемента
     */
    public void drawItem(Graphics g, int ofs, boolean selected);

    /**
     * информация для всплывающего окна
     */
    public String getTipString();
    /**
     * Callback-вызов, осуществляемый при выполнении OK для выделенного курсором элемента
     */
    public void onSelect();
}
