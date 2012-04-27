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
package server.devicetest;

import device.AbstractDevice;
import device.Accelerometer;
import device.DeviceException;
import device.DeviceType;
import java.rmi.RemoteException;

/**
 * Virtual Accelerometer for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class AccelerometerTest extends AbstractDevice implements Accelerometer
{    
    public AccelerometerTest()
    {
        super("test-accelerometer",DeviceType.ACCELEROMETER_SENSOR);
    }

    public class AxisDevice extends AbstractDevice implements Accelerometer.Axis
    {
        private int axis;

        public AxisDevice(int ax)
        {
            super(AccelerometerTest.this.deviceName + " " + AXIS_NAMES[ax],DeviceType.ACCELEROMETER_AXIS);
            axis = ax;
            this.setDevicePosition(AccelerometerTest.this.devicePosition);
        }

        public Object update() throws RemoteException, DeviceException
        {
            return getAcceleration();
        }

        public double getAcceleration() throws RemoteException, DeviceException
        {
            return 30 * Math.random();
        }

        @Override
        public void closeDevice() throws DeviceException
        {            
        }
    }

    private AxisDevice xAxisDevice = null;
    private AxisDevice yAxisDevice = null;
    private AxisDevice zAxisDevice = null;

    public AbstractDevice getAxisDevice(int axis)
    {
        AxisDevice device = null;
        switch(axis)
        {
            case Accelerometer.Axis.X_AXIS:
                if(xAxisDevice == null)
                {
                    xAxisDevice = new AxisDevice(Accelerometer.Axis.X_AXIS);
                }
                device = xAxisDevice;
                break;
            case Accelerometer.Axis.Y_AXIS:
                if(yAxisDevice == null)
                {
                    yAxisDevice = new AxisDevice(Accelerometer.Axis.Y_AXIS);
                }
                device = yAxisDevice;
                break;
            case Accelerometer.Axis.Z_AXIS:
                if(zAxisDevice == null)
                {
                    zAxisDevice = new AxisDevice(Accelerometer.Axis.Z_AXIS);
                }
                device = zAxisDevice;
                break;            
        }
        return device;
    }

    public double[] getAcceleration() throws RemoteException, DeviceException
    {                
        double[] v = new double[3];
        v[0] = 30 * Math.random();
        v[1] = 30 * Math.random();
        v[2] = 30 * Math.random();
        return v;
    }

    public Object update() throws RemoteException, DeviceException
    {
        return getAcceleration();
    }

    @Override
    public void closeDevice() throws DeviceException
    {        
    }
}
