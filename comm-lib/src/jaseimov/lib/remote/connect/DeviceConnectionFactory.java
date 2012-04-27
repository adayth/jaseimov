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
package jaseimov.lib.remote.connect;

import jaseimov.lib.services.ServiceType;

/**
 * Factory to create {@link DeviceConnection} based in ServiceType provided to it.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class DeviceConnectionFactory
{
    final static String INVALID_SERVICE_TYPE = "Invalid ServiceType provided to ClientFactory";

    /**
     * Creates a {@link DeviceConnection} based in ServiceType provided to it.
     * @param serverIP IP of the service.
     * @param serverPort Port of the service.
     * @param serviceID ID of the service.
     * @param type Type of the service.
     * @return The DeviceConnection needed.
     */
    public static DeviceConnection getDeviceConnection(String serverIP, int serverPort, int serviceID, ServiceType type)
    {
        switch(type)
        {
            case RMI_SERVICE:
                return new DeviceConnectionRMI(serverIP,serverPort,serviceID);
            default:
                throw new IllegalArgumentException(INVALID_SERVICE_TYPE);
        }
    }
    
}
