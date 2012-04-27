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

package service;

import java.rmi.RemoteException;

/**
 * RMI interface for get a String array of ServiceInfo.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */

public interface ServiceListing extends java.rmi.Remote
{
    /**
     * The name that should have the RMI service that implements this interface.
     */
    public static final String serviceName = "service-list";

    /**
     * Remote method.
     * @return An array of ServiceInfo.
     * @throws RemoteException
     */
    public String[] getServiceList() throws RemoteException;

    /**
     * Converts the info of a Service (id, port and ServiceType) to a String delimited by commas and backwards
     */
    public static class ServiceInfo
    {
        final static String DELIM = ",";
        
        private int id;
        private int port;
        private ServiceType type;

        /**
         * Take the Service information in a String delimited by commas.
         * @param info Service info delimited by commas.
         */
        public ServiceInfo(String info)
        {
            String[] parsedInfo = info.split(DELIM);
            
            id = Integer.parseInt(parsedInfo[0]);
            port = Integer.parseInt(parsedInfo[1]);
            type = ServiceType.valueOf(parsedInfo[2]);
        }

        /**
         * Take the Service info separately.
         * @param id Service ID.
         * @param port Service port.
         * @param type Service value.
         */
        public ServiceInfo(int id, int port, ServiceType type)
        {
            this.id = id;
            this.port = port;
            this.type = type;
        }

        /**
         * Returns the Service ID.
         * @return Service ID.
         */
        public int getID()
        {
            return id;
        }

        /**
         * Returns the Service Port.
         * @return Service port.
         */
        public int getPort()
        {
            return port;
        }

        /**
         * Returns the Service ServiceType.
         * @return Service ServiceType.
         */
        public ServiceType getServiceType()
        {
            return type;
        }

        /**
         * Convert the information stored in this class to a String delimited by commas.
         * @return ID,Port,ServiceType
         */
        @Override
        public String toString()
        {
            return id + DELIM + port + DELIM + type;
        }
    }
}