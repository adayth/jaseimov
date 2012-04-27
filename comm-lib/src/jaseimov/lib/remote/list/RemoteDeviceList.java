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
package jaseimov.lib.remote.list;

import jaseimov.lib.remote.connect.ConnectException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.services.ServiceListing;
import jaseimov.lib.services.ServiceListing.ServiceInfo;
import jaseimov.lib.services.ServiceType;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A list to connect to remote devices in a server using information provided by
 * a ServiceListing RMI service.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class RemoteDeviceList
{
  private Map<Integer, RemoteDeviceInfo> deviceMap = new HashMap<Integer, RemoteDeviceInfo>();
  private String registryIP;
  private int registryPort;

  /**
   * Creates a RemoteDeviceList.
   * @param ip IP of the RMI registry of the server.
   * @param port Port of the RMI registry of the server.
   */
  public RemoteDeviceList(String ip, int port)
  {
    registryIP = ip;
    registryPort = port;
  }

  /**
   * Obtains information of remote devices using ServiceListing RMI service.
   * The information is stored in this list to acces to remote devices.
   * @throws ConnectException
   */
  public void connectToServer() throws ConnectException
  {
    try
    {
      Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
      ServiceListing list = (ServiceListing) registry.lookup(ServiceListing.serviceName);
      String[] serviceInfo = list.getServiceList();

      deviceMap.clear();
      
      if (serviceInfo.length > 0)
      {
        for (String s : serviceInfo)
        {
          ServiceInfo info = new ServiceInfo(s);
          int id = info.getID();
          int port = info.getPort();
          ServiceType type = info.getServiceType();

          if (!deviceMap.containsKey(id))
          {
            RemoteDeviceInfo node = new RemoteDeviceInfo(registryIP, port, id, type);
            deviceMap.put(id, node);
          }
        }
      }
      else
      {
        throw new RuntimeException("Emtpy service list in " + registryIP + " " + registryPort + " - " + ServiceListing.serviceName);
      }
    }
    catch (RemoteException ex)
    {
      throw new ConnectException(ex);
    }
    catch (NotBoundException ex)
    {
      throw new ConnectException(ex);
    }
  }

  /**
   * Returns a {@link RemoteDeviceInfo} with the device ID provided.
   * @param id Device ID of the device.
   * @return A RemoteDeviceInfo object or null if a device with that id isn't found.
   */
  public RemoteDeviceInfo getRemoteDeviceInfo(int id)
  {
    return deviceMap.get(id);
  }

  /**
   * Return an array of {@link RemoteDeviceInfo} with all devices contained in this list.
   * @return An array of {@link RemoteDeviceInfo}.
   */
  public RemoteDeviceInfo[] getRemoteDeviceInfoArray()
  {
    return deviceMap.values().toArray(new RemoteDeviceInfo[0]);
  }

  /**
   * Returns an array of {@link RemoteDeviceInfo} with all the devices of the DeviceType
   * provided contained in this list.
   * @param type DeviceType to filter.
   * @return An array of {@link RemoteDeviceInfo} or a length 0 array if there isn't devices
   *         avalible of that type.
   * @throws ConnectException
   */
  public RemoteDeviceInfo[] getRemoteDeviceInfoArray(DeviceType type) throws ConnectException
  {
    List<RemoteDeviceInfo> retList = new ArrayList();

    for (RemoteDeviceInfo value : deviceMap.values())
    {
      try
      {
        if (value.getDevice().getDeviceType() == type)
        {
          retList.add(value);
        }
      }
      catch (RemoteException ex)
      {
        ex.printStackTrace();
      }
    }

    return retList.toArray(new RemoteDeviceInfo[0]);
  }

  /**
   * Returns an array of {@link RemoteDeviceInfo} with all the devices contained in this list that
   * are instance of the Device Class provided.
   * @param deviceClass Device Class object to filter.
   * @return An array of {@link RemoteDeviceInfo} or a length 0 array if there isn't devices
   *         avalible of that type.
   * @throws ConnectException
   */
  public RemoteDeviceInfo[] getRemoteDeviceInfoArray(Class deviceClass) throws ConnectException
  {
    List<RemoteDeviceInfo> retList = new ArrayList();

    for (RemoteDeviceInfo value : deviceMap.values())
    {
      if (deviceClass.isInstance(value.getDevice()))
      {
        retList.add(value);
      }
    }

    return retList.toArray(new RemoteDeviceInfo[0]);
  }
}
