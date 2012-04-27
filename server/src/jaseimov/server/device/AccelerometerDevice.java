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
package jaseimov.server.device;

import com.phidgets.AccelerometerPhidget;
import com.phidgets.PhidgetException;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import java.rmi.RemoteException;

/**
 * Implements an Accelerometer of three axis device based in Phidgets three axis Accelerometer.
 * Uses phidgets java library.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class AccelerometerDevice extends AbstractDevice implements Accelerometer
{  
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
    super(name, DeviceType.ACCELEROMETER_SENSOR);

    phidgetSerial = serial;

    // Connect to phidget device
    try
    {
      accelerometer = new AccelerometerPhidget();
      accelerometer.open(phidgetSerial);
      accelerometer.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }  

  public double[] getAcceleration() throws RemoteException, DeviceException
  {
    try
    {
      double[] v = new double[3];
      v[0] = DeviceConstants.G_TO_CMS2 * accelerometer.getAcceleration(0);
      v[1] = DeviceConstants.G_TO_CMS2 * accelerometer.getAcceleration(1);
      v[2] = DeviceConstants.G_TO_CMS2 * accelerometer.getAcceleration(2);
      return v;
    }
    catch (PhidgetException ex)
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

  /////////////////////////////////////////////////
  /////////////////////////////////////////////////

  /**
   * Implements an Accelerometer Axis based in this AccelerometerDevice.
   */
  public class AccelAxisDevice extends AbstractDevice implements Axis
  {
    private int axis;

    public AccelAxisDevice(int ax)
    {
      super(AccelerometerDevice.this.deviceName + " " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(AccelerometerDevice.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      try
      {
        return DeviceConstants.G_TO_CMS2 * accelerometer.getAcceleration(axis);
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

  // Only create an instance for each Axis
  private AccelAxisDevice xAccelAxisDevice = null;
  private AccelAxisDevice yAccelAxisDevice = null;
  private AccelAxisDevice zAccelAxisDevice = null;

  /**
   * Return an {@link AxisDevice} of the choosed axis.
   * @param axis One of the axis values located in Axis.
   * @return An AbstractDevice that is an AxisDevice.
   */
  public AbstractDevice getAccelAxisDevice(int axis)
  {
    AccelAxisDevice device = null;
    switch (axis)
    {
      case Axis.X_AXIS:
        if (xAccelAxisDevice == null)
        {
          xAccelAxisDevice = new AccelAxisDevice(Axis.X_AXIS);
        }
        device = xAccelAxisDevice;
        break;
      case Axis.Y_AXIS:
        if (yAccelAxisDevice == null)
        {
          yAccelAxisDevice = new AccelAxisDevice(Axis.Y_AXIS);
        }
        device = yAccelAxisDevice;
        break;
      case Axis.Z_AXIS:
        if (zAccelAxisDevice == null)
        {
          zAccelAxisDevice = new AccelAxisDevice(Axis.Z_AXIS);
        }
        device = zAccelAxisDevice;
        break;
    }
    return device;
  }
}
