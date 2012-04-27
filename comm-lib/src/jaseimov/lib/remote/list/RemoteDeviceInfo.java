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
package jaseimov.lib.remote.list;

import jaseimov.lib.remote.utils.SensorUpdater;
import jaseimov.lib.remote.connect.DeviceConnection;
import jaseimov.lib.remote.connect.DeviceConnectionFactory;
import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.SensorDevice;
import jaseimov.lib.services.ServiceType;

/**
 * Node of {@link DeviceList} that uses a {@link DeviceConnection} to connect to a remote device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class RemoteDeviceInfo
{
    private DeviceConnection client;
    private SensorUpdater updater;

    /**
     * Creates a RemoteDeviceInfo node.
     * @param serverIP IP of the server.
     * @param serverPort Port of the server.
     * @param serviceID ID of the service.
     * @param type Type of the service.
     */
    public RemoteDeviceInfo(String serverIP, int serverPort, int serviceID, ServiceType type)
    {
        client = DeviceConnectionFactory.getDeviceConnection(serverIP,serverPort,serviceID,type);
    }

    /**
     * Returns the {@link DeviceConnection} used by the class.
     * @return A {@link DeviceConnection}.
     */
    public DeviceConnection getDeviceConnection()
    {
        return client;
    }

    /**
     * Uses the {@link DeviceConnection} to connect to remote device and returns it.
     * @return Reference to a remote device.
     * @throws ConnectException
     */
    public Device getDevice() throws ConnectException
    {
        if(!client.isConnected())
            client.connect();
        return client.getDevice();
    }

    /**
     * If the remote device is a SensorDevice this creates and stores in this class
     * an {@link SensorUpdater} object associated to it.
     * @return An SensorUpdater object or null if the device isn't a SensorDevice.
     */
    public SensorUpdater getSensorUpdater()
    {
        if(updater == null)
        {
            Device dev;
            try
            {
                dev = getDevice();
            }
            catch (ConnectException ex)
            {
                ex.printStackTrace();
                return null;
            }
            if(dev instanceof SensorDevice)
            {
                updater = new SensorUpdater((SensorDevice)dev);
            }
        }
        return updater;
    }

}
