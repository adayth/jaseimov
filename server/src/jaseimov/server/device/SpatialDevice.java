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

import com.phidgets.PhidgetException;
import com.phidgets.SpatialPhidget;
import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DeviceType;
import jaseimov.lib.devices.Spatial;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 *
 * @author Aday Talavera <aday.talavera at gmail.com>
 */
public class SpatialDevice extends AbstractDevice implements Spatial
{
  public static final double COMPASS_ERROR_VALUE = 0;

  private int phidgetSerial;
  private SpatialPhidget spatial;

  public SpatialDevice(String name, int serial) throws DeviceException
  {
    this(name, serial, null);
  }

  public SpatialDevice(String name, int serial, String configFile) throws DeviceException
  {
    super(name, DeviceType.SPATIAL_SENSOR);

    phidgetSerial = serial;

    // Connect to phidget device
    try
    {
      spatial = new SpatialPhidget();
      spatial.open(phidgetSerial);
      spatial.waitForAttachment(DeviceConstants.PHIDGET_WAIT);
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }

    // Calibrate compass with configFile it it's provided
    if(configFile != null)
    {
      try
      {
        double values[] = calibrateWithFile(configFile);
        // Pass values to calibration function
        calibrateCompass(values);
      }
      catch(IOException ex)
      {
        throw new DeviceException(ex.getMessage());
      }
    }
  }

  /**
   * Reads a file with one line with 13 double values separated by commas
   * @param fileName
   * @return Array with 13 double values
   * @throws IOException
   * @throws PhidgetException
   */
  private double[] calibrateWithFile(String fileName) throws IOException
  {    
    File file = new File(fileName);
    BufferedReader in = new BufferedReader(new FileReader(file));          
    String line = in.readLine();
    String values[] = line.split(",");
    if(values.length >= 13)
    {
      double v[] = new double[13];
      for(int i=0; i<13; i++)
      {
        v[i] = Double.parseDouble(values[i]);
      }
      System.out.println("Compass calibration values readed from file " + fileName + " : " + Arrays.toString(v));
      return v;
    }
    else
    {
      throw new IOException("Compass calibration file must have 13 double values separated by commas");
    }
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
    try
    {
      double[] v = new double[3];
      v[0] = DeviceConstants.G_TO_CMS2 * spatial.getAcceleration(0);
      v[1] = DeviceConstants.G_TO_CMS2 * spatial.getAcceleration(1);
      v[2] = DeviceConstants.G_TO_CMS2 * spatial.getAcceleration(2);
      return v;
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public double[] getMagneticField() throws RemoteException, DeviceException
  {
    try
    {
      double[] v = new double[3];
      v[0] = spatial.getMagneticField(0);
      v[1] = spatial.getMagneticField(1);
      v[2] = spatial.getMagneticField(2);
      return v;
    }
    catch (PhidgetException ex)
    {
      if(ex.getErrorNumber() == PhidgetException.EPHIDGET_UNKNOWNVAL)            
        return new double[]{COMPASS_ERROR_VALUE, COMPASS_ERROR_VALUE, COMPASS_ERROR_VALUE};
      else
        throw new DeviceException(ex.getDescription());
    }
  }

  public double[] getAngularRate() throws RemoteException, DeviceException
  {
    try
    {
      double[] v = new double[3];
      v[0] = spatial.getAngularRate(0);
      v[1] = spatial.getAngularRate(1);
      v[2] = spatial.getAngularRate(2);
      return v;
    }
    catch (PhidgetException ex)
    {
      throw new DeviceException(ex.getDescription());
    }
  }

  public void calibrateCompass(double[] values) throws RemoteException, DeviceException
  {
    if(values != null && values.length >= 13)
    {
      try
      {
        spatial.setCompassCorrectionParameters(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9], values[10], values[11], values[12]);
      }
      catch (PhidgetException ex)
      {        
        throw new DeviceException(ex.getDescription());
      }
    }
    else
    {
      throw new DeviceException("Compass calibration must be 13 double values array");
    }
  }

  @Override
  public void closeDevice() throws DeviceException
  {
    try
    {
      spatial.close();
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
      super(SpatialDevice.this.deviceName + " accel. " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialDevice.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      try
      {
        return DeviceConstants.G_TO_CMS2 * spatial.getAcceleration(axis);
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
      super(SpatialDevice.this.deviceName + " compass " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialDevice.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      try
      {
        return spatial.getMagneticField(axis);
      }
      catch (PhidgetException ex)
      {
        if(ex.getErrorNumber() == PhidgetException.EPHIDGET_UNKNOWNVAL)        
          return COMPASS_ERROR_VALUE;
        else
          throw new DeviceException(ex.getDescription());
      }
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
      super(SpatialDevice.this.deviceName + " gyro. " + Axis.AXIS_NAMES[ax], DeviceType.AXIS_SENSOR);
      axis = ax;
      this.setDevicePosition(SpatialDevice.this.devicePosition);
    }

    public Object update() throws RemoteException, DeviceException
    {
      return getAxisValue();
    }

    public double getAxisValue() throws RemoteException, DeviceException
    {
      try
      {
        return spatial.getAngularRate(axis);
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
