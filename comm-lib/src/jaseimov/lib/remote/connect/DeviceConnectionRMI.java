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
package jaseimov.lib.remote.connect;

import jaseimov.lib.devices.Device;
import jaseimov.lib.services.ServiceType;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * A DeviceConnection to connect to a RMI service.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class DeviceConnectionRMI implements DeviceConnection
{  
    private int id;
    private String ip;
    private int port;
    private boolean connected = false;
    private Device device = null;

    /**
     * Creates a DeviceConnectionRMI.
     * @param serverIP IP of the RMI registry.
     * @param serverPort Port of the RMI registry.
     * @param serviceID Name of the RMI service.
     */
    public DeviceConnectionRMI(String serverIP, int serverPort, int serviceID)
    {
        ip = serverIP;
        port = serverPort;
        id = serviceID;        
    }

    /**
     * Connects to RMI service.
     * @throws ConnectException If can't connect to the service.
     */
    public void connect() throws ConnectException
    {
        if(!connected)
        {
            try
            {
                Registry registry = LocateRegistry.getRegistry(ip, port);
                device = (Device) registry.lookup(String.valueOf(id));
                if(device.getID() != id)
                    throw new ConnectException(ConnectException.ID_DONT_MATCH);
            }
            catch(RemoteException ex)
            {
                throw new ConnectException(ex);
            }
            catch(NotBoundException ex)
            {                
                throw new ConnectException(ex);
            }
            connected = true;
        }
        else
            throw new ConnectException(ConnectException.ALREADY_CONNECTED);
    }

    /**
     * Disconnects from RMI service.
     * @throws ConnectException If can't disconnect from the service.
     */
    public void disconnect() throws ConnectException
    {
        if(connected)
        {
            connected = false;
            device = null;
        }
        else
            throw new ConnectException(ConnectException.NOT_CONNECTED);
    }

    public boolean isConnected()
    {
        return connected;
    }

    /**
     * Returns RMI service type.
     * @return RMI service type constant.
     */
    public ServiceType getServiceType()
    {
        return ServiceType.RMI_SERVICE;
    }

    /**
     * Get the remote reference to remote device.
     * @return Remote device.
     * @throws ConnectException If can't obtain remote reference.
     */
    public Device getDevice() throws ConnectException
    {
        if(connected)
            return device;
        else
            throw new ConnectException(ConnectException.NOT_CONNECTED);
    }
}