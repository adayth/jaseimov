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
 * RMI interface for a remote device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface Device extends java.rmi.Remote
{

  /**
   * Returns the name of the device.
   * @return Name of the device.
   * @throws RemoteException
   */
  String getName() throws RemoteException;

  /**
   * Returns the type of the device defined by a {@link DeviceType}.
   * @return Type of the device.
   * @throws RemoteException
   */
  DeviceType getDeviceType() throws RemoteException;

  /**
   * Returns the unique identifier (ID) of this device in the system.
   * @return ID of the device.
   * @throws RemoteException
   */
  int getID() throws RemoteException;

  /**
   * Returns the position of this device in ASEIMOV car definend by a {@link DevicePosition}.
   * @return Position of the device.
   * @throws RemoteException
   */
  DevicePosition getDevicePosition() throws RemoteException;
}
