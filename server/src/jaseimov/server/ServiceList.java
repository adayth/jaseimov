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
import jaseimov.lib.services.ServiceListing;
import jaseimov.lib.services.ServiceListing.ServiceInfo;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A serviceList of ServiceDevice that allows a server to start/stop all ServiceDevice that contains.
 * Additionally can publish the services using the ServiceListing RMI interface.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class ServiceList implements ServiceListing
{
  private List<ServiceDevice> serviceList = new ArrayList();
  private int port;

  /**
   *
   * @param rmiPort The RMI port used by the RMI registry.
   */
  public ServiceList(int rmiPort)
  {
    port = rmiPort;
  }

  /**
   * Allocates this serviceList in RMI registry using ServiceListing RMI interface.
   * @throws RemoteException
   */
  public void bindList() throws RemoteException
  {
    ServiceListing stub = (ServiceListing) UnicastRemoteObject.exportObject(this, 0);
    LocateRegistry.getRegistry(port).rebind(ServiceListing.serviceName, stub);
  }

  /**
   * Unallocates this serviceList in RMI registry.
   * @throws RemoteException
   * @throws NotBoundException
   */
  public void unbindList() throws RemoteException, NotBoundException
  {
    LocateRegistry.getRegistry(port).unbind(ServiceListing.serviceName);
  }

  /**
   * Add a new ServiceDevice to the serviceList.
   * @param d A new ServiceDevice.
   */
  public void addService(ServiceDevice d)
  {
    serviceList.add(d);
  }

  /**
   * Starts all ServiceDevice contained in this serviceList using {@link ServiceDevice#startService()}.
   * @throws RemoteException
   * @throws AlreadyBoundException
   */
  public void startServices() throws RemoteException, AlreadyBoundException
  {
    for (Object d : serviceList.toArray())
    {
      ((ServiceDevice) d).startService();
    }
  }

  /**
   * Stops all ServiceDevice contained in this serviceList using {@link ServiceDevice#stopService()}.
   * @throws RemoteException
   * @throws NotBoundException
   */
  public void stopServices() throws RemoteException, NotBoundException
  {
    for (Object d : serviceList.toArray())
    {
      ((ServiceDevice) d).stopService();
    }
  }

  public String[] getServiceList() throws RemoteException
  {
    List<String> ret = new ArrayList();
    for (ServiceDevice svc : serviceList.toArray(new ServiceDevice[0]))
    {
      Device dev = svc.getDevice();
      String definition = new ServiceInfo(dev.getID(), svc.getPort(), svc.getServiceType()).toString();
      ret.add(definition);
    }
    return ret.toArray(new String[0]);
  }

  /**
   * Returns an array that contains all ServiceDevice stored in this serviceList.
   * @return Array of ServiceDevice.
   */
  public ServiceDevice[] getServices()
  {
    return serviceList.toArray(new ServiceDevice[0]);
  }
}
