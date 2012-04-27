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
 * RMI interface defining a superinterface for all sensor devices.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface SensorDevice extends Device
{
    /**
     * Returns the data measured by the sensor in an Object.
     * This can be used to access to a sensor using a common method.
     * @return The data measured by the sensor.
     * @throws RemoteException
     * @throws DeviceException
     */
    Object update() throws RemoteException, DeviceException;
}