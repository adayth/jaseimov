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
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.Spatial;
import java.rmi.RemoteException;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class SpatialTest extends AbstractDevice implements Spatial
{
  public SpatialTest()
  {
    super("spatial-test", DeviceType.SPATIAL_SENSOR);
  }

  public double[][] getSpatialValue() throws RemoteException, DeviceException
  {
    double[][] v = new double[3][3];
    v[0] = getAcceleration();
    v[1] = getMagneticField();
    v[2] = getAngularRate();
    return v;
  }

  public Object update() throws RemoteException, DeviceException
  {
    return getSpatialValue();
  }

  public double[] getAcceleration() throws RemoteException, DeviceException
  {
    double[] v = new double[3];
    v[0] = Math.random();
    v[1] = Math.random();
    v[2] = Math.random();
    return v;
  }

  public double[] getMagneticField() throws RemoteException, DeviceException
  {
    double[] v = new double[3];
    v[0] = 20 * Math.random();
    v[1] = 20 * Math.random();
    v[2] = 20 * Math.random();
    return v;
  }

  public double[] getAngularRate() throws RemoteException, DeviceException
  {
    double[] v = new double[3];
    v[0] = 10 * Math.random();
    v[1] = 10 * Math.random();
    v[2] = 10 * Math.random();
    return v;
  }

  @Override
  public void closeDevice() throws DeviceException
  {    
  }

  public void calibrateCompass(double[] values) throws RemoteException, DeviceException
  {
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
      super(SpatialTest.this.deviceName + " accel. " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialTest.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      return 30 * Math.random();
    }

    @Override
    public void closeDevice() throws DeviceException
    {
    }
  }
  // Only create an instance for each Axis
  private AccelAxisDevice xAccel = null;
  private AccelAxisDevice yAccel = null;
  private AccelAxisDevice zAccel = null;

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
        if (xAccel == null)
        {
          xAccel = new AccelAxisDevice(Axis.X_AXIS);
        }
        device = xAccel;
        break;
      case Axis.Y_AXIS:
        if (yAccel == null)
        {
          yAccel = new AccelAxisDevice(Axis.Y_AXIS);
        }
        device = yAccel;
        break;
      case Axis.Z_AXIS:
        if (zAccel == null)
        {
          zAccel = new AccelAxisDevice(Axis.Z_AXIS);
        }
        device = zAccel;
        break;
    }
    return device;
  }

  /////////////////////////////////////////////////
  /////////////////////////////////////////////////
  /**
   * Implements a Compass Axis based in this AccelerometerDevice.
   */
  public class CompassAxisDevice extends AbstractDevice implements Axis
  {
    private int axis;

    public CompassAxisDevice(int ax)
    {
      super(SpatialTest.this.deviceName + " compass " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialTest.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      return 30 * Math.random();
    }

    @Override
    public void closeDevice() throws DeviceException
    {
    }
  }
  // Only create an instance for each Axis
  private CompassAxisDevice xCompass = null;
  private CompassAxisDevice yCompass = null;
  private CompassAxisDevice zCompass = null;

  /**
   * Return an {@link AxisDevice} of the choosed axis.
   * @param axis One of the axis values located in Axis.
   * @return An AbstractDevice that is an AxisDevice.
   */
  public AbstractDevice getCompassAxisDevice(int axis)
  {
    CompassAxisDevice device = null;
    switch (axis)
    {
      case Axis.X_AXIS:
        if (xCompass == null)
        {
          xCompass = new CompassAxisDevice(Axis.X_AXIS);
        }
        device = xCompass;
        break;
      case Axis.Y_AXIS:
        if (yCompass == null)
        {
          yCompass = new CompassAxisDevice(Axis.Y_AXIS);
        }
        device = yCompass;
        break;
      case Axis.Z_AXIS:
        if (zCompass == null)
        {
          zCompass = new CompassAxisDevice(Axis.Z_AXIS);
        }
        device = zCompass;
        break;
    }
    return device;
  }

  /////////////////////////////////////////////////
  /////////////////////////////////////////////////
  /**
   * Implements a Gyroscope Axis based in this AccelerometerDevice.
   */
  public class GyroAxisDevice extends AbstractDevice implements Axis
  {
    private int axis;

    public GyroAxisDevice(int ax)
    {
      super(SpatialTest.this.deviceName + " gyro. " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialTest.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      return 30 * Math.random();
    }

    @Override
    public void closeDevice() throws DeviceException
    {
    }
  }
  // Only create an instance for each Axis
  private GyroAxisDevice xGyro = null;
  private GyroAxisDevice yGyro = null;
  private GyroAxisDevice zGyro = null;

  /**
   * Return an {@link AxisDevice} of the choosed axis.
   * @param axis One of the axis values located in Axis.
   * @return An AbstractDevice that is an AxisDevice.
   */
  public AbstractDevice getGyroAxisDevice(int axis)
  {
    GyroAxisDevice device = null;
    switch (axis)
    {
      case Axis.X_AXIS:
        if (xGyro == null)
        {
          xGyro = new GyroAxisDevice(Axis.X_AXIS);
        }
        device = xGyro;
        break;
      case Axis.Y_AXIS:
        if (yGyro == null)
        {
          yGyro = new GyroAxisDevice(Axis.Y_AXIS);
        }
        device = yGyro;
        break;
      case Axis.Z_AXIS:
        if (zGyro == null)
        {
          zGyro = new GyroAxisDevice(Axis.Z_AXIS);
        }
        device = zGyro;
        break;
    }
    return device;
  }
}
