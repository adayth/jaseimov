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
package client.devicelist;

import client.servercomm.ClientDevice;
import client.servercomm.ClientFactory;
import client.servercomm.ConnectException;
import client.update.Updater;
import device.Device;
import device.SensorDevice;
import service.ServiceType;

/**
 * Node of {@link DeviceList} that uses a {@link ClientDevice} to connect to a remote device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class DeviceInfo
{
    ClientDevice client;
    Updater updater;

    /**
     * Creates a DeviceInfo node.
     * @param serverIP IP of the server.
     * @param serverPort Port of the server.
     * @param serviceID ID of the service.
     * @param type Type of the service.
     */
    public DeviceInfo(String serverIP, int serverPort, int serviceID, ServiceType type)
    {
        client = ClientFactory.getClient(serverIP,serverPort,serviceID,type);
    }

    /**
     * Returns the {@link ClientDevice} used by the class.
     * @return A {@link ClientDevice}.
     */
    public ClientDevice getClient()
    {
        return client;
    }

    /**
     * Uses the {@link ClientDevice} to connect to remote device and returns it.
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
     * an {@link Updater} associated to it.
     * @return An Updater object or null if the device isn't a SensorDevice.
     */
    public Updater getUpdater()
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
                updater = new Updater((SensorDevice)dev);
            }
        }
        return updater;
    }

}
