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
package jaseimov.client.datacapture;

import jaseimov.client.ClientApp;
import jaseimov.client.utils.Command;
import jaseimov.lib.remote.list.RemoteDeviceInfo;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Camera;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.SensorDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * Command that creates and show a {@link CaptureFrame}.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class CaptureDataCommand extends Command
{
    private static final String name = "capture-data";

    private CaptureFrame captureFrame;

    public CaptureDataCommand()
    {
        super(name);
    }

    public void execute()
    {
        RemoteDeviceInfo[] devices = ClientApp.getDeviceList().getRemoteDeviceInfoArray();
        List<RemoteDeviceInfo> sensorList = new ArrayList();

        for(RemoteDeviceInfo info : devices)
        {
            try
            {
                Device dev = info.getDevice();
                if(dev instanceof SensorDevice && !(dev instanceof Camera) && !(dev instanceof Accelerometer))
                {                    
                    sensorList.add(info);
                }
            }
            catch(ConnectException ex)
            {
                ex.printStackTrace();
            }
        }

        if(sensorList.size() > 0)
        {            

            if(captureFrame == null || !captureFrame.isVisible())
            {
                captureFrame = new CaptureFrame(sensorList.toArray(new RemoteDeviceInfo[0]));
            }
            ClientApp.registerFrame(captureFrame);
            captureFrame.setVisible(false);
            captureFrame.setVisible(true);
        }
        else
        {
            throw new RuntimeException(ClientApp.getBundleString("NO SENSORS EXCEPTION"));
        }
    }

}
