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
package client.maingui.sensorvisors;

import client.ClientApp;
import client.devicelist.DeviceInfo;
import device.Camera;
import java.util.Observable;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * SensorVisor associated to a Camera device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CameraVisor extends SensorVisor
{
    JLabel imageLabel;    

    int width;
    int height;

    public CameraVisor(DeviceInfo info)
    {
        super(info);

        this.setFrameIcon(new ImageIcon(getClass().getResource("/client/images/webcam.png")));

        try
        {            
            Camera cam = (Camera)info.getDevice();
            width = cam.getImageWidth();
            height = cam.getImageHeigth();
        }
        //Catch ConnectException / RemoteException
        catch(Exception ex)
        {
            ex.printStackTrace();
        }        

        imageLabel = new JLabel();        
        imageLabel.setPreferredSize(new java.awt.Dimension(width,height));
        imageLabel.setDoubleBuffered(true);
        imageLabel.setToolTipText(ClientApp.getBundleString("RESOLUTION") + width + " x " + height + " pixels");
        super.sensorContainer.add(imageLabel);        

        this.pack();
    }

    
    
    public void update(Observable o, Object arg)
    {
        byte[] buffer = (byte[])arg;
        imageLabel.setIcon(new ImageIcon(buffer));        
    }  
}
