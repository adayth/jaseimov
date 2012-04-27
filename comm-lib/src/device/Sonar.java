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
 * RMI interface for a Sonar.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Sonar extends SensorDevice
{
    /**
     * Returns if the sonar is enabled or not.
     * @return True if the sonar is enabled, false if it isn't.
     * @throws RemoteException
     * @throws DeviceException
     */
    boolean getEnabled() throws RemoteException, DeviceException;

    /**
     * Activates/desactivates the sonar.
     * @param value True to activate the sonar, false to desactivate the sonar.
     * @throws RemoteException
     * @throws DeviceException
     */
    void setEnabled(boolean value) throws RemoteException, DeviceException;

    /**
     * Returns the distance measured with the sonar.
     * If sonar is desativated, the returned distance will be wrong.
     * @return Distance in centimeters.
     * @throws RemoteException
     * @throws DeviceException
     */
    double getDistance() throws RemoteException, DeviceException;
}
