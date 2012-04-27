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

import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.services.ServiceType;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * ServiceDevice implementation for a RMI service.
 * Publishes a device in the RMI registry.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ServiceRMIDevice implements ServiceDevice
{
  private AbstractDevice device;
  private int port;

  /**
   * Creates a new ServiceRMIDevice.
   * @param exportedDevice The device to be published in the RMI registry.
   * @param rmiPort The port used by the RMI registry.
   */
  public ServiceRMIDevice(AbstractDevice exportedDevice, int rmiPort)
  {
    device = exportedDevice;
    port = rmiPort;
  }

  /**
   * Publishes the device in the RMI registry.
   * @throws RemoteException
   * @throws AlreadyBoundException
   */
  public void startService() throws RemoteException, AlreadyBoundException
  {
    Device stub = (Device) UnicastRemoteObject.exportObject(device, 0);
    LocateRegistry.getRegistry(port).bind(String.valueOf(device.getID()), stub);
  }

  /**
   * Deletes the device from the RMI registry.
   * @throws RemoteException
   * @throws NotBoundException
   */
  public void stopService() throws RemoteException, NotBoundException
  {
    try
    {
      device.closeDevice();
    }
    catch (DeviceException ex)
    {
      ex.printStackTrace();
    }
    LocateRegistry.getRegistry(port).unbind(String.valueOf(device.getID()));
  }

  public ServiceType getServiceType()
  {
    return ServiceType.RMI_SERVICE;
  }

  public int getPort()
  {
    return port;
  }

  public Device getDevice()
  {
    return device;
  }
}
