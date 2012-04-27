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

import com.phidgets.AccelerometerPhidget;
import com.phidgets.PhidgetException;
import device.AbstractDevice;
import device.Accelerometer;
import device.DeviceException;
import device.DeviceType;
import java.rmi.RemoteException;

/**
 * Implements an Accelerometer of three axis device based in Phidgets three axis Accelerometer.
 * Uses phidgets java library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class AccelerometerDevice 
        extends AbstractDevice
        implements Accelerometer
{
    /**
     * Conversion factor between gravity units to cm/s^2 units
     */
    final protected static double G_TO_CMS2 = 9.8;

    private int phidgetSerial;
    private AccelerometerPhidget accelerometer;

    /**
     * Creates a new AcceleromterDevice
     * @param name Name of the device.
     * @param serial Phidgets serial number of the Phidgets Acceleromter.
     * @throws DeviceException If the device is not found.
     */
    public AccelerometerDevice(String name, int serial) throws DeviceException
    {
        super(name,DeviceType.ACCELEROMETER_SENSOR);

        phidgetSerial = serial;

        // Connect to phidget device
        try
        {
            accelerometer = new AccelerometerPhidget();
            accelerometer.open(phidgetSerial);
            accelerometer.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
        }
        catch(PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
    }            

    /**
     * Implements an Accelerometer.Axis based in this AccelerometerDevice.
     */
    public class AxisDevice extends AbstractDevice implements Accelerometer.Axis
    {
        private int axis;

        public AxisDevice(int ax)
        {
            super(AccelerometerDevice.this.deviceName + " " + AXIS_NAMES[ax],DeviceType.ACCELEROMETER_AXIS);
            axis = ax;
            this.setDevicePosition(AccelerometerDevice.this.devicePosition);
        }

        public Object update() throws RemoteException, DeviceException
        {
            return getAcceleration();
        }

        public double getAcceleration() throws RemoteException, DeviceException
        {
            try
            {
                return G_TO_CMS2 * accelerometer.getAcceleration(axis);
            }
            catch (PhidgetException ex)
            {
                throw new DeviceException(ex.getDescription());
            }
        }

        @Override
        public void closeDevice() throws DeviceException
        {
        }
    }

    private AxisDevice xAxisDevice = null;
    private AxisDevice yAxisDevice = null;
    private AxisDevice zAxisDevice = null;

    /**
     * Return an {@link AxisDevice} of the choosed axis.
     * @param axis One of the axis values located in Accelerometer.Axis.
     * @return An AbstractDevice that is an AxisDevice.
     */
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
        try
        {
            double[] v = new double[3];
            v[0] = G_TO_CMS2 * accelerometer.getAcceleration(0);
            v[1] = G_TO_CMS2 * accelerometer.getAcceleration(1);
            v[2] = G_TO_CMS2 * accelerometer.getAcceleration(2);
            return v;
        }
        catch(PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }        
    }

    public Object update() throws RemoteException, DeviceException
    {
        return getAcceleration();
    }

    /**
     * Close the Phidget device.
     * @throws DeviceException
     */
    @Override
    public void closeDevice() throws DeviceException
    {
        try
        {
            accelerometer.close();
        }
        catch (PhidgetException ex)
        {
            throw new DeviceException(ex.getDescription());
        }
    }
}
