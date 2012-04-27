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
 * RMI interface for an accelerometer sensor of three axis.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Accelerometer extends SensorDevice
{
    /**
     * Returns an array of three double with the acceleration of the three axis in cm/s^2 units.
     * @return Array of accelerations: [0] will be x axis, [1] will be y axis and [2] will be z axis.
     * @throws RemoteException
     * @throws DeviceException
     */
    double[] getAcceleration() throws RemoteException, DeviceException;

    /**
     * RMI interface for an  accelerometer's axis.
     */
    public interface Axis extends SensorDevice
    {
        final public static int X_AXIS = 0;
        final public static int Y_AXIS = 1;
        final public static int Z_AXIS = 2;

        final public static String[] AXIS_NAMES = {"X axis", "Y axis", "Z axis"};

        /**
         * Returns the acceleration of this axis.
         * @return Acceleration.
         * @throws RemoteException
         * @throws DeviceException
         */
        double getAcceleration() throws RemoteException, DeviceException;
    }
}
