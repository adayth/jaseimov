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
package jaseimov.client.camera;

import jaseimov.client.ClientApp;
import jaseimov.client.utils.Command;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.devices.DeviceType;

/**
 * Command that creates and shows a {@link CameraFrame}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ViewCamerasCommand extends Command
{
    private static final String name = "view-cameras";

    private CameraFrame cameraFrame;

    public ViewCamerasCommand()
    {
        super(name);
    }

    public void execute()
    {
        RemoteDeviceInfo[] cameras;
        try
        {
            cameras = ClientApp.getDeviceList().getRemoteDeviceInfoArray(DeviceType.CAMERA_SENSOR);
            if(cameras.length > 0)
            {
                if(cameraFrame == null || !cameraFrame.isVisible())
                {
                    cameraFrame = new CameraFrame(cameras);
                }
                ClientApp.registerFrame(cameraFrame);
                cameraFrame.setVisible(false);
                cameraFrame.setVisible(true);
            }
            else
                throw new RuntimeException(ClientApp.getBundleString("NO CAMERAS EXCEPTION"));
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
    }    

}
