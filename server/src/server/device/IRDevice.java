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
package server.device;

import device.AbstractDevice;
import device.DeviceException;
import device.DeviceType;
import device.IR;
import device.InterfaceKit;
import java.rmi.RemoteException;

/**
 * Implements an IR device connected to a Phidgets InterfaceKit board.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public abstract class IRDevice extends AbstractDevice implements IR
{
    /**
     * Infrared types.
     */
    public enum IRType
    {
        /**
         * Short range  (10-80 cm) infrared.
         */
        IR_10_80,

        /**
         * Long range (20-150 cm) infrared.
         */
        IR_20_150;
    }    

    InterfaceKit ikit;
    protected int sensorPort;
    
    private IRDevice(String name, InterfaceKit ikit, int sensorPort)
    {
        super(name, DeviceType.IR_SENSOR);

        this.ikit = ikit;
        this.sensorPort = sensorPort;        
    }

    /**
     *  This device doesn't need to be closed.
     * @throws DeviceException
     */
    @Override
    public void closeDevice() throws DeviceException
    {       
    }

    public abstract double getDistance() throws RemoteException, DeviceException;    

    public Object update() throws RemoteException, DeviceException
    {
        return getDistance();
    }

    /**
     * Factory method for creates IR devices based in its IRType.
     * @param name The device name.
     * @param ikit InterfaceKit where sonar is connected.
     * @param sensorPort The InterfaceKit analogic input where IR is connected.
     * @param type The type of the IR to be created.
     * @return A new IRDevice.
     */
    public static IRDevice getIRDevice(String name, InterfaceKit ikit, int sensorPort, IRType type)
    {
        switch(type)
        {
            case IR_10_80:
                return new IR1080(name,ikit,sensorPort);
            case IR_20_150:
                return new IR20150(name,ikit,sensorPort);
            default:
                throw new IllegalArgumentException("Invalid IRType provided");                
        }
    }

    // Sharp Distance Sensor 2Y0A21 (10-80cm)
    private static class IR1080 extends IRDevice
    {
        final private static int MAX_RANGE = 1000;
        final private static int MIN_RANGE = 73;

        public IR1080(String name, InterfaceKit ikit, int sensorPort)
        {
            super(name,ikit,sensorPort);
        }

        @Override
        public double getDistance() throws RemoteException, DeviceException
        {
            int value = ikit.getSensorValue(sensorPort);
            if(value >= MIN_RANGE && value <= MAX_RANGE)
            {
                return 4800. / (value - 20.);
            }
            else
            {
                return -1;
            }
        }
        
        /*@Override
        public double getDistance() throws RemoteException, DeviceException
        {
            int value = ikit.getSensorValue(sensorPort);
            return 4800. / (value - 20.);
        }*/
    }

    // Sharp Distance Sensor 2Y0A02 (20-150cm)
    private static class IR20150 extends IRDevice
    {
        final private static int MAX_RANGE = 1000;
        final private static int MIN_RANGE = 76;

        public IR20150(String name, InterfaceKit ikit, int sensorPort)
        {
            super(name,ikit,sensorPort);
        }

        @Override
        public double getDistance() throws RemoteException, DeviceException
        {
            int value = ikit.getSensorValue(sensorPort);
            if(value >= MIN_RANGE && value <= MAX_RANGE)
            {
                return 9462. / (value - 16.92);
            }
            else
            {
                return -1;
            }
        }

        /*public double getDistance() throws RemoteException, DeviceException
        {
            int value = ikit.getSensorValue(sensorPort);            
            return 9462. / (value - 16.92);
        }*/
    }
}
