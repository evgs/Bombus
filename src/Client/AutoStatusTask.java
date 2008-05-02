/*
 * AutoStatusTask.java
 *
 * Created on 12.06.2007 г., 3:12
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

import com.alsutton.jabber.datablocks.Presence;

/**
 *
 * @author Evg_S
 */
public class AutoStatusTask implements Runnable {    
    private boolean stop;
    private long timeEvent;
    /** Creates a new instance of AutoStatusTask */
    public AutoStatusTask() {
        new Thread(this).start();
    }
    
    public void setTimeEvent(long delay){
        timeEvent=(delay==0)? 0:delay+System.currentTimeMillis();
    }

    boolean isTimerSet() { return (timeEvent!=0); }
    
    public void destroyTask(){
        stop=false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                stop=true;
            }
            
            if (timeEvent==0) continue;
            
            long timeRemained=System.currentTimeMillis()-timeEvent;
//#if DefaultConfiguration
            System.out.println(timeRemained);
//#endif
            if (timeRemained<0) continue;

            StaticData.getInstance().roster.setAutoStatus(Presence.PRESENCE_AWAY);
            timeEvent=0;

        }
    }

}
