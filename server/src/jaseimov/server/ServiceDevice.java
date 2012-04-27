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
package jaseimov.server;

import jaseimov.lib.devices.Device;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import jaseimov.lib.services.ServiceType;

/**
 * Interface of a remote service associated to a device.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public interface ServiceDevice
{
  /**
   * Starts this service for allow the remote use of the device.
   * @throws RemoteException
   * @throws AlreadyBoundException
   */
  void startService() throws RemoteException, AlreadyBoundException;

  /**
   * Stops this service and the device can't be accessed remotely.
   * @throws RemoteException
   * @throws NotBoundException
   */
  void stopService() throws RemoteException, NotBoundException;

  /**
   * Returns the type of the service.
   * @return ServiceType.
   */
  ServiceType getServiceType();

  /**
   * Returns the port of the service.
   * @return A port number.
   */
  int getPort();

  /**
   * Returns the Device associated to this service.
   * @return Device of this service.
   */
  Device getDevice();
}
