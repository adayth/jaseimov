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
 * RMI interface for a Phidgets InterfaceKit board.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface InterfaceKit extends Device
{

  /**
   * Returns the boolean value of a digital output of the board.
   * @param index Index of the digital output.
   * @return If true, digital output is activataded. If false, digital output is desactivated
   * @throws RemoteException
   * @throws DeviceException
   */
  boolean getOutput(int index) throws RemoteException, DeviceException;

  /**
   * Set the value for a digital output of the board.
   * @param index Index of the digital output.
   * @param value New digital output value. True activated, false desactivated.
   * @throws RemoteException
   * @throws DeviceException
   */
  void setOutput(int index, boolean value) throws RemoteException, DeviceException;

  /**
   * Returns the sensor value of a analog input of the board.
   * @param index Index of the analog input.
   * @return Sensor value of the analog input.
   * This value is generic value of voltage, and should be converted to a real sensor value.
   * @throws RemoteException
   * @throws DeviceException
   */
  int getSensorValue(int index) throws RemoteException, DeviceException;
}
