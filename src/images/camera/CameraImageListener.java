/*
 * CameraImageListener.java
 *
 * Created on 25 Октябрь 2006 г., 22:32
 *
 * Copyright (c) 2005-2006, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package images.camera;

/**
 *
 * @author Evg_S
 */
public interface CameraImageListener {
    
    /** Creates a new instance of CameraImageListener */
    public void cameraImageNotify(byte[] capturedPhoto);
    
}
