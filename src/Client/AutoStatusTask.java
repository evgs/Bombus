/*
 * AutoStatusTask.java
 *
 * Created on 12 Июнь 2007 г., 3:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
        while (stop) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                stop=true;
            }
            
            if (timeEvent==0) continue;
            
            long timeRemained=System.currentTimeMillis()-timeEvent;
            if (timeRemained<0) continue;

            StaticData.getInstance().roster.setAutoStatus(Presence.PRESENCE_AWAY);

        }
    }

}
