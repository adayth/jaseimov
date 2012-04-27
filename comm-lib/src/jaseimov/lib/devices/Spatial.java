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
package jaseimov.lib.devices;

import java.rmi.RemoteException;

/**
 * RMI interface for a spatial sensor formed by an accelerometer, a compass and a gyroscope.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Spatial extends SensorDevice, Accelerometer, Compass, Gyroscope
{
  /**
   * Returns a matrix with accelerations, magnetic fields and angular momentums obtained from an accelerometer,
   * a compass and a gyroscope.
   * @return A matrix with this form:<br>
   * {<br>
   * {x acceleration, y acceleration, z acceleration},<br>
   * {x magnetic field, y magnetic field, z magnetic field},<br>
   * {x angular momentum, y angular momentum, z angular momentum}<br>
   * }
   * @throws RemoteException
   * @throws DeviceException
   */
  double[][] getSpatialValue()  throws RemoteException, DeviceException;
}
