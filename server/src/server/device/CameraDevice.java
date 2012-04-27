/*
 * Copyright (C) 2010 Aday Talavera Hierro <aday.talavera@gmail.com>
 *
 * This file is part of JASEIMOV.
 *
 * JASEIMOV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JASEIMOV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JASEIMOV.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.device;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;
import device.AbstractDevice;
import device.Camera;
import device.DeviceException;
import device.DeviceType;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;

/**
 * Implements a Camera device. Uses v4l4j library
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CameraDevice
        extends AbstractDevice
        implements Camera
{
    private String device;

    private VideoDevice vd;
    private FrameGrabber fg;

    private int width;
    private int height;
    private int compression;
    private boolean captureStarted = false;

    /**
     * Creatas a new CameraDevice.
     * @param name Name of the device.
     * @param device Device file of the camera, generally /dev/video#
     * @param width Width in pixels of resolution of the camera.
     * @param height Height un pixels of resolution of the camera.
     * @param compression Factor of compression to be used between 0 and 100.
     * @throws DeviceException
     */
    public CameraDevice(String name, String device, int width, int height, int compression) throws DeviceException
    {
        super(name, DeviceType.CAMERA_SENSOR);

        this.device = device;
        this.width = width;
        this.height = height;
        this.compression = compression;        

        try
        {
            vd = new VideoDevice(this.device);            
        }
        catch(V4L4JException ex)
        {
            throw new DeviceException(ex.getMessage());
        }
    }

    /**
     * Stops capturing camera and release all resources associated.
     * @throws DeviceException
     */
    @Override
    public void closeDevice() throws DeviceException
    {
        if(captureStarted)
        {
            fg.stopCapture();
        }
        if(vd != null)
        {
            vd.releaseFrameGrabber();
            vd.release();
        }
    }

    private void startCapture() throws DeviceException
    {
        if(!captureStarted)
        {
            try
            {
                fg = vd.getJPEGFrameGrabber(352, 288, 0, V4L4JConstants.STANDARD_WEBCAM, compression);
                width = fg.getWidth();
                height = fg.getHeight();
                fg.startCapture();
                captureStarted = true;
            }
            catch(V4L4JException ex)
            {
                throw new DeviceException(ex.getMessage());
            }
        }
    }

    private void stopCapture()
    {
        if(captureStarted)
        {
            fg.stopCapture();
            captureStarted = false;
        }
    }

    public void setCompression(int value) throws RemoteException, DeviceException
    {
        compression = value;
        if(captureStarted)
        {
            stopCapture();
            startCapture();
        }
    }

    public byte[] getImage() throws RemoteException, DeviceException
    {
        if(!captureStarted)
        {
            startCapture();
        }
        ByteBuffer byteBuffer;
        byte[] buffer;
        try
        {
            byteBuffer = fg.getFrame();
            buffer = new byte[byteBuffer.limit()];
            byteBuffer.get(buffer);
            return buffer;
        }
        catch(V4L4JException ex)
        {
            throw new DeviceException(ex.getMessage());
        }
    }

    public Object update() throws RemoteException, DeviceException
    {
        return getImage();
    }

    public int getImageWidth() throws RemoteException
    {
        return width;
    }

    public int getImageHeigth() throws RemoteException
    {
        return height;
    }    
}
