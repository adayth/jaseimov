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
package client.devicelist;

import client.ClientApp;
import client.servercomm.ConnectException;
import device.DeviceType;
import service.ServiceListing;
import service.ServiceListing.ServiceInfo;
import service.ServiceType;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * A list to connect to remote devices in a server using information provided by
 * a ServiceListing RMI service.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class DeviceList
{
    private Map<Integer,DeviceInfo> map;

    private String registryIP;
    private int registryPort;

    /**
     * Creates a DeviceList.
     * @param ip IP of the RMI registry of the server.
     * @param port Port of the RMI registry of the server.
     */
    public DeviceList(String ip, int port)
    {
        registryIP = ip;
        registryPort = port;
        map = new Hashtable();        
    }

    /**
     * Obtains information of remote devices using ServiceListing RMI service.
     * The information is stored in this list to acces to remote devices.
     * @throws ConnectException
     */
    public void getServiceInfo() throws ConnectException
    {        
        try
        {
            Registry registry = LocateRegistry.getRegistry(registryIP,registryPort);
            ServiceListing list = (ServiceListing) registry.lookup(ServiceListing.serviceName);
            String[] serviceInfo = list.getServiceList();

            if(serviceInfo.length > 0)
            {
                for(String s : serviceInfo)
                {
                    ServiceInfo info = new ServiceInfo(s);
                    int id = info.getID();
                    int port = info.getPort();
                    ServiceType type = info.getServiceType();

                    if(!map.containsKey(id))
                    {
                        DeviceInfo node = new DeviceInfo(registryIP,port,id,type);
                        map.put(id, node);
                    }
                }
            }
            else
                throw new RuntimeException(ClientApp.getBundleString("EMTPY SERVICE LIST EXCEPTION") + registryIP + " " + registryPort + " - " + ServiceListing.serviceName);

        }
        catch(RemoteException ex)
        {
            throw new ConnectException(ex);
        }
        catch(NotBoundException ex)
        {
            throw new ConnectException(ex);
        }        
    }

    /**
     * Returns a {@link DeviceInfo} with the device ID provided.
     * @param id Device ID of the device.
     * @return A DeviceInfo object or null if a device with taht id isn't found.
     */
    public DeviceInfo getDeviceInfo(int id)
    {        
        return map.get(id);
    }

    /**
     * Return an array of {@link DeviceInfo} with all devices contained in this list.
     * @return An array of {@link DeviceInfo}.
     */
    public DeviceInfo[] getDeviceInfoArray()
    {        
        return map.values().toArray(new DeviceInfo[0]);
    }

    /**
     * Returns an array of {@link DeviceInfo} with all the devices of the DeviceType
     * provided contained in this list.
     * @param type DeviceType to filter.
     * @return An array of {@link DeviceInfo} or a length 0 array if there isn't devices
     *         avalible of that type.
     * @throws ConnectException
     */
    public DeviceInfo[] getDeviceInfoArray(DeviceType type) throws ConnectException
    {
        List<DeviceInfo> retList = new ArrayList();

        for(DeviceInfo value : map.values())
        {
            try
            {
                if(value.getDevice().getDeviceType() == type)
                    retList.add(value);
            }
            catch(RemoteException ex)
            {
                ex.printStackTrace();
            }
        }
        
        return retList.toArray(new DeviceInfo[0]);
    }
}
