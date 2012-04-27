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
 * RMI interface for a gyroscope sensor of three axis. A gyroscope measures the angular momentum.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Gyroscope extends SensorDevice
{  
  /**
   * Returns an array of three double with the angular momentum of the three axis in degrees/s units.
   * @return Array of angular momentums: [0] will be x axis, [1] will be y axis and [2] will be z axis.
   * @throws RemoteException
   * @throws DeviceException
   */
  double[] getAngularRate() throws RemoteException, DeviceException;  
}
