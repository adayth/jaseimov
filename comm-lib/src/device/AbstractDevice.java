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

package device;

import java.rmi.RemoteException;

/**
 * Implements basic operations of Device interface. Final device implementations extend this class.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public abstract class AbstractDevice
        implements Device
{
    protected static int idCount = 0;

    protected String deviceName;
    protected int deviceID;
    protected DeviceType deviceType;
    protected DevicePosition devicePosition = DevicePosition.NOT_DEFINED;

    /**
     * Constructor for an AbstractDevice. The unique identifier (ID) of the device is calculated automatically.
     * The default position for a device is {@link DevicePosition#NOT_DEFINED}.
     * @param name Name of the device.
     * @param type Type of the device.
     */
    public AbstractDevice(String name,DeviceType type)
    {
        deviceName = name;
        deviceID = idCount++;
        deviceType = type;
    }

    public String getName() throws RemoteException
    {
        return deviceName;
    }

    public DeviceType getDeviceType() throws RemoteException
    {
        return deviceType;
    }

    public int getID() throws RemoteException
    {
        return deviceID;
    }

    public DevicePosition getDevicePosition() throws RemoteException
    {
        return devicePosition;
    }

    /**
     * Sets the position of this device.
     * @param newPosition New position for the device.
     */
    public void setDevicePosition(DevicePosition newPosition)
    {
        devicePosition = newPosition;
    }

    /**
     * Abstract method for closing a device.
     * @throws DeviceException
     */
    public abstract void closeDevice() throws DeviceException;        
}