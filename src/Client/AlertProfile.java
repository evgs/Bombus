/*
 * AlertProfile.java
 *
 * Created on 28.03.2005, 0:05
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
 *
 */

package Client;

import images.RosterIcons;
import locale.SR;
import ui.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author Eugene Stahov
 */
public class AlertProfile extends VirtualList implements CommandListener {
    public final static int AUTO=0;
    public final static int ALL=1;
    public final static int VIBRA=2;
    public final static int SOUND=3;
    public final static int NONE=4;
    
    private final static String[] alertNames=
    { "Auto", "All signals", "Vibra", "Sound", "No signals"};
    
    private Profile profile=new Profile();
    int defp;
    Config cf;
    
    /** Creates a new instance of Profile */
    
    private Command cmdOk=new Command(SR.MS_SELECT,Command.OK,1);
    private Command cmdDef=new Command(SR.MS_SETDEFAULT,Command.OK,2);
    private Command cmdCancel=new Command(SR.MS_BACK,Command.BACK,99);
    /** Creates a new instance of SelectStatus */
    public AlertProfile(Display d) {
        super();
        
        cf=Config.getInstance();
        
        setTitleItem(new Title(SR.MS_ALERT_PROFILE));
        
        addCommand(cmdOk);
        addCommand(cmdDef);
        addCommand(cmdCancel);
        setCommandListener(this);
        
        int p=cf.profile;
        defp=cf.def_profile;
        
        moveCursorTo(p, true);
        attachDisplay(d);
    }
    
    int index;
    public VirtualElement getItemRef(int Index){ index=Index; return profile;}
    private class Profile extends IconTextElement {
        public Profile(){
            super(RosterIcons.getInstance());
        }
        //public void onSelect(){}
        public int getColor(){ return Colors.LIST_INK; }
        public int getImageIndex(){return index+RosterIcons.ICON_PROFILE_INDEX;}
        public String toString(){ 
            StringBuffer s=new StringBuffer(alertNames[index]);
            if (index==defp) s.append(" (default)");
            return s.toString();
        }
    }
    
    public void commandAction(Command c, Displayable d){
        if (c==cmdOk) eventOk(); 
        if (c==cmdDef) { 
            cf.def_profile=defp=cursor;
	    cf.saveToStorage();
            redraw();
        }
        if (c==cmdCancel) destroyView();
    }
    
    public void eventOk(){
        cf.profile=cursor;
        destroyView();
    }
    
    public int getItemCount(){   return alertNames.length; }
    
}
