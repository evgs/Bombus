/*
 * CameraImage.java
 *
 * Created on 25 Октябрь 2006 г., 22:35
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package images.camera;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

/**
 *
 * @author Evg_S
 */
public class CameraImage implements CommandListener{
    
    private Command cmdShot=new Command ("Shot", Command.OK, 1);
    private Command cmdCancel=new Command ("Cancel", Command.BACK, 99);
    
    private Display display;
    private Displayable parentView;
    
    private Player player;
    private VideoControl videoControl;
    
    Form f;
    CameraImageListener imgListener;

    /** Creates a new instance of CameraImage */
    public CameraImage(Display display, CameraImageListener imgListener) {
        this.display=display;
        parentView=display.getCurrent();
        this.imgListener=imgListener;

        int exp=0;
        try {
            String uri="capture://video";
            /*String contentTypes[]=Manager.getSupportedContentTypes(null);
            for (int i=0; i < contentTypes.length; i++) {
                System.out.println(contentTypes[i]);
                if (contentTypes[i].equals("image")) {
                    uri="capture://image"; //break;
                }
            }*/
            
            player = Manager.createPlayer(uri);
            player.realize();
            
            videoControl = (VideoControl)player.getControl("VideoControl");
            
            Form form = new Form("Camera");
            Item item = (Item)videoControl.initDisplayMode(
                    GUIControl.USE_GUI_PRIMITIVE, null);
            form.append(item);
            form.addCommand(cmdShot);
            form.addCommand(cmdCancel);
            form.setCommandListener(this);
            display.setCurrent(form);
            
            player.start();
        } catch (Exception e) { 
            display.setCurrent(
                    new Alert("Error", e.toString(), null, null), 
                    parentView);
            e.printStackTrace(); 
        }
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if (command==cmdShot) {
            try {
                byte photo[]=videoControl.getSnapshot(null);
                imgListener.cameraImageNotify(photo);
            } catch (Exception e) { e.printStackTrace(); }
        }
        // Shut down the player.
        player.close();
        player = null;
        videoControl = null;

        display.setCurrent(parentView);
    }
}
