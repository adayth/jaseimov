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
package jaseimov.server.devicetest;

import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Accelerometer;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import java.rmi.RemoteException;

/**
 * Virtual Accelerometer for debugging purposes.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class AccelerometerTest extends AbstractDevice implements Accelerometer
{
  public AccelerometerTest()
  {
    super("test-accelerometer", DeviceType.ACCELEROMETER_SENSOR);
  }

  public class AxisDeviceTest extends AbstractDevice implements Axis
  {
    private int axis;    

    public AxisDeviceTest(int ax)
    {
      super(AccelerometerTest.this.deviceName + " " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(AccelerometerTest.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      return Math.random();
    }

    @Override
    public void closeDevice() throws DeviceException
    {
    }
  }
  private AxisDeviceTest xAxisDevice = null;
  private AxisDeviceTest yAxisDevice = null;
  private AxisDeviceTest zAxisDevice = null;

  public AbstractDevice getAxisDevice(int axis)
  {
    AxisDeviceTest device = null;
    switch (axis)
    {
      case Axis.X_AXIS:
        if (xAxisDevice == null)
        {
          xAxisDevice = new AxisDeviceTest(Axis.X_AXIS);
        }
        device = xAxisDevice;
        break;
      case Axis.Y_AXIS:
        if (yAxisDevice == null)
        {
          yAxisDevice = new AxisDeviceTest(Axis.Y_AXIS);
        }
        device = yAxisDevice;
        break;
      case Axis.Z_AXIS:
        if (zAxisDevice == null)
        {
          zAxisDevice = new AxisDeviceTest(Axis.Z_AXIS);
        }
        device = zAxisDevice;
        break;
    }
    return device;
  }

  public double[] getAcceleration() throws RemoteException, DeviceException
  {
    double[] v = new double[3];
    v[0] = xAxisDevice.getAxisValue();
    v[1] = yAxisDevice.getAxisValue();
    v[2] = zAxisDevice.getAxisValue();
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
