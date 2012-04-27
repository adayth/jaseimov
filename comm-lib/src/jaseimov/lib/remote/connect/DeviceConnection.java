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

/**
 * Defines an interface to connect to a remote device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface DeviceConnection
{
    /**
     * Connect to remote device.
     * @throws ConnectException If can't connect to remote device.
     */
    public void connect() throws ConnectException;

    /**
     * Disconnect from remote device.
     * @throws ConnectException If can't disconnect from remote device.
     */
    public void disconnect() throws ConnectException;

    /**
     * Returns if is connected to remote device or not.
     * @return True if is conected, false if it isn't.
     */
    public boolean isConnected();

    /**
     * Returns the ServiceType of the remote device.
     * @return ServiceType of the remote device.
     */
    public ServiceType getServiceType();

    /**
     * Returns the remote device.
     * @return Remote device.
     * @throws ConnectException If can't obtain remote device.
     */
    public Device getDevice() throws ConnectException;
}