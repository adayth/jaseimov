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

import client.devicelist.DeviceInfo;
import client.servercomm.ConnectException;
import device.Device;
import java.rmi.RemoteException;

/**
 * Factory to create SensorVisor objects.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class VisorFactory
{
    /**
     * Factory method to create a SensorVisor based in the DeviceType of a {@link DeviceInfo}.
     * @param deviceInfo
     * @return A new SensorVisor.
     */
    public static SensorVisor getSensorVisor(DeviceInfo deviceInfo)
    {
        try
        {
            Device dev = deviceInfo.getDevice();
            SensorVisor visor;
            switch(dev.getDeviceType())
            {
                case ACCELEROMETER_SENSOR:
                    visor = new AccelerometerVisor(deviceInfo);
                    break;
                case ENCODER_SENSOR:
                    visor = new EncoderVisor(deviceInfo);
                    break;
                case SONAR_SENSOR:
                    visor = new SonarVisor(deviceInfo);
                    break;
                case IR_SENSOR:
                    visor = new IRVisor(deviceInfo);
                    break;
                case CAMERA_SENSOR:
                    visor = new CameraVisor(deviceInfo);
                    break;
                default:
                    throw new IllegalArgumentException();
            }                        
            return visor;
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
    }
}
