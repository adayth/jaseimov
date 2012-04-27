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
 * RMI interface for an encoder.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */

public interface Encoder extends SensorDevice
{
    /**
     * Returns the total number of tics readed by the encoder since last call to getTics().
     * Warning: first call to this function can return a very high number of tics.
     * @return Number of tics.
     * @throws RemoteException
     */
    int getTics() throws RemoteException;

    /**
     * Returns a coeficient that can be multiplied by a number of tics to
     * calculate the centimeters covered by a wheel with that encoder.
     * @return Tic/cm constant for this encoder.
     * @throws RemoteException
     */
    double getCmPerTic() throws RemoteException;
}
