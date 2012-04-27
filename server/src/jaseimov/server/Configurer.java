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
package jaseimov.server;

import jaseimov.lib.devices.AbstractDevice;
import jaseimov.lib.devices.Axis;
import jaseimov.lib.devices.Device;
import jaseimov.lib.devices.DeviceException;
import jaseimov.lib.devices.DevicePosition;
import jaseimov.lib.devices.DeviceType;
import jaseimov.server.device.AccelerometerDevice;
import jaseimov.server.device.AdvancedServoControlDev;
import jaseimov.server.device.CameraDevice;
import jaseimov.server.device.IRDevice;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jaseimov.server.device.IRDevice.IRType;
import jaseimov.server.device.InterfaceKitDevice;
import jaseimov.server.device.MotorControlDevice;
import jaseimov.server.device.MouseEncoderDevice;
import jaseimov.server.device.MouseEncoderDevice.MouseAxis;
import jaseimov.server.device.PhidgetEncoderDevice;
import jaseimov.server.device.ServoControlDevice;
import jaseimov.server.device.SonarDevice;
import jaseimov.server.device.SpatialDevice;

/**
 * Configure {@link ServerApp} with a configuration file.
 * @author Aday Talavera Hierro <aday.talavera@gmail.com>
 */
public class Configurer
{
  /**
   * Configuration file separator between device type name and parameters of the device
   */
  public static final String NAME_SEPARATOR = "=";
  /**
   * Configuration file separator between parameters of the device
   */
  public static final String PARAMS_SEPARATOR = ",";
  /**
   * Configuration file character for comment a line
   */
  public static final String COMMENT_CHAR = "#";

  /**
   * Reads a configuration file and returns an array of AbstractDevice configured
   * with the information of the file.
   * @param fileName Configuration file.
   * @return Array of AbstractDevice configured.
   * @throws IOException
   */
  public static AbstractDevice[] readDeviceConfigFile(String fileName) throws IOException
  {
    Map<String, Device> devMap = new HashMap<String, Device>();

    File file = new File(fileName);
    BufferedReader in = new BufferedReader(new FileReader(file));
    if (in.ready())
    {
      String line;
      while ((line = in.readLine()) != null)
      {
        if (line.startsWith(COMMENT_CHAR))
        {
          continue;
        }
        String[] elements = line.split(NAME_SEPARATOR);
        if (elements.length == 2)
        {        
          String deviceID = elements[0];
          String[] params = elements[1].split(PARAMS_SEPARATOR);

          try
          {
            // Get device name to put in a map
            String name = null;
            AbstractDevice device = null;
            
            DeviceType type = DeviceType.valueOf(deviceID);
            switch (type)
            {
              case MOTOR_CONTROL:
                if (params.length >= 5)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  int index = Integer.parseInt(params[2]);
                  int lower = Integer.parseInt(params[3]);
                  int upper = Integer.parseInt(params[4]);

                  // Optional params
                  if(params.length >= 7)
                  {
                    String encoderName = params[5];
                    Device encoder = devMap.get(encoderName);
                    String accelName = params[6];
                    Device accel = devMap.get(accelName);

                    // Create motor assigning encoder & accelerometer
                    if(encoder != null && accel != null)
                    {
                      device = new MotorControlDevice(name, serial, index, lower, upper, encoder, accel);
                    }
                  }                  
                  else
                  {
                    // Create motor whitout encoder & accelerometer
                    device = new MotorControlDevice(name, serial, index, lower, upper);
                  }
                }
                break;
              case SERVO_CONTROL:
                if (params.length >= 6)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  int index = Integer.parseInt(params[2]);
                  int min = Integer.parseInt(params[3]);
                  int max = Integer.parseInt(params[4]);
                  int start = Integer.parseInt(params[5]);
                  device = new ServoControlDevice(name, serial, index, min, max, start);
                }
                break;
              case ADV_SERVO_CONTROL:
                if (params.length >= 6)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  int index = Integer.parseInt(params[2]);
                  int min = Integer.parseInt(params[3]);
                  int max = Integer.parseInt(params[4]);
                  int start = Integer.parseInt(params[5]);
                  device = new AdvancedServoControlDev(name, serial, index, min, max, start);
                }
                break;
              case ACCELEROMETER_SENSOR:
                if (params.length >= 3)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  device = new AccelerometerDevice(name, serial);
                  device.setDevicePosition(DevicePosition.valueOf(params[2]));

                  Device axisX, axisY, axisZ;
                  // Add accelerometer axis
                  axisX = ((AccelerometerDevice) device).getAccelAxisDevice(Axis.X_AXIS);
                  putIfAbsent(devMap, axisX.getName(), axisX);
                  axisY = ((AccelerometerDevice) device).getAccelAxisDevice(Axis.Y_AXIS);
                  putIfAbsent(devMap, axisY.getName(), axisY);
                  axisZ = ((AccelerometerDevice) device).getAccelAxisDevice(Axis.Z_AXIS);
                  putIfAbsent(devMap, axisZ.getName(), axisZ);
                }
                break;
              case SPATIAL_SENSOR:
                if (params.length >= 3)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  if(params.length == 3)
                  {
                    device = new SpatialDevice(name, serial);
                    device.setDevicePosition(DevicePosition.valueOf(params[2]));
                  }
                  else
                  {
                    String configFile = params[2];
                    device = new SpatialDevice(name, serial, configFile);
                    device.setDevicePosition(DevicePosition.valueOf(params[3]));
                  }

                  Device axisX, axisY, axisZ;
                  // Add accelerometer axis
                  axisX = ((SpatialDevice) device).getAccelAxisDevice(Axis.X_AXIS);
                  putIfAbsent(devMap, axisX.getName(), axisX);
                  axisY = ((SpatialDevice) device).getAccelAxisDevice(Axis.Y_AXIS);
                  putIfAbsent(devMap, axisY.getName(), axisY);
                  axisZ = ((SpatialDevice) device).getAccelAxisDevice(Axis.Z_AXIS);
                  putIfAbsent(devMap, axisZ.getName(), axisZ);

                  // Add compass axis
                  axisX = ((SpatialDevice) device).getCompassAxisDevice(Axis.X_AXIS);
                  putIfAbsent(devMap, axisX.getName(), axisX);
                  axisY = ((SpatialDevice) device).getCompassAxisDevice(Axis.Y_AXIS);
                  putIfAbsent(devMap, axisY.getName(), axisY);
                  axisZ = ((SpatialDevice) device).getCompassAxisDevice(Axis.Z_AXIS);
                  putIfAbsent(devMap, axisZ.getName(), axisZ);

                  // Add gyroscope axis
                  axisX = ((SpatialDevice) device).getGyroAxisDevice(Axis.X_AXIS);
                  putIfAbsent(devMap, axisX.getName(), axisX);
                  axisY = ((SpatialDevice) device).getGyroAxisDevice(Axis.Y_AXIS);
                  putIfAbsent(devMap, axisY.getName(), axisY);
                  axisZ = ((SpatialDevice) device).getGyroAxisDevice(Axis.Z_AXIS);
                  putIfAbsent(devMap, axisZ.getName(), axisZ);
                }
                break;
              case MOUSE_ENCODER_SENSOR:
                if (params.length >= 6)
                {
                  name = params[0];
                  String devFile = params[1];
                  double radius = Double.parseDouble(params[2]);
                  double tics = Double.parseDouble(params[3]);
                  MouseAxis axis = MouseAxis.valueOf(params[4]);
                  device = new MouseEncoderDevice(name, devFile, radius, tics, axis);
                  device.setDevicePosition(DevicePosition.valueOf(params[5]));
                }
                break;
              case PHIDGET_ENCODER_SENSOR:
                if (params.length >= 7)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  int index = Integer.parseInt(params[2]);
                  double radius = Double.parseDouble(params[3]);
                  double tics = Double.parseDouble(params[4]);
                  double sign = Double.parseDouble(params[5]);
                  device = new PhidgetEncoderDevice(name, serial ,index, radius, tics, sign);
                  device.setDevicePosition(DevicePosition.valueOf(params[6]));
                }
                break;
              case INTERFACE_KIT:
                if (params.length >= 2)
                {
                  name = params[0];
                  int serial = Integer.parseInt(params[1]);
                  device = new InterfaceKitDevice(name, serial);                  
                }
                break;
              case SONAR_SENSOR:
                if (params.length >= 5)
                {
                  String ikitName = params[1];
                  Device ikDevice = devMap.get(ikitName);
                  if (ikDevice != null)
                  {                    
                    int input = Integer.parseInt(params[2]);
                    int output = Integer.parseInt(params[3]);
                    device = new SonarDevice(name, ikDevice, input, output);
                    device.setDevicePosition(DevicePosition.valueOf(params[4]));
                  }
                  else
                  {
                    System.err.println("InterfaceKit " + name + " not found");
                  }
                }
                break;
              case IR_SENSOR:
                if (params.length >= 5)
                {
                  name = params[0];
                  String ikitName = params[1];
                  Device ikDevice = devMap.get(ikitName);
                  if (ikDevice != null)
                  {                    
                    int input = Integer.parseInt(params[2]);
                    IRType irType = IRType.valueOf(params[3]);
                    device = IRDevice.getIRDevice(name, ikDevice, input, irType);
                    device.setDevicePosition(DevicePosition.valueOf(params[4]));
                  }
                  else
                  {
                    System.err.println("InterfaceKit " + name + " not found");
                  }
                }
                break;
              case CAMERA_SENSOR:
                if (params.length >= 6)
                {
                  name = params[0];
                  String devFile = params[1];
                  int width = Integer.parseInt(params[2]);
                  int height = Integer.parseInt(params[3]);
                  int compression = Integer.parseInt(params[4]);
                  device = new CameraDevice(name, devFile, width, height, compression);
                  device.setDevicePosition(DevicePosition.valueOf(params[5]));
                }
                break;
              default:
                System.err.println("Invalid device indentifier " + deviceID);
                break;
            }

            // Add valid devices to devices map
            putIfAbsent(devMap, name, device);
          }
          catch (DeviceException ex)
          {
            ex.printStackTrace();
          }
        }
      }
      in.close();
    }

    return devMap.values().toArray(new AbstractDevice[0]);
  }

  // Utility method to put devices in map if they are not included yet
  private static void putIfAbsent(Map<String, Device> devMap, String name, Device device)
  {
    if (device != null && name != null)
    {
      if(name.length() > 0)
      {
        if(!devMap.containsKey(name))
        {
          devMap.put(name, device);
        }
        else
        {
          System.err.println("Duplicated device name for " + name);
        }
      }
      else
      {
        System.err.println("Device name cannot be null");
      }
    }
  }
}
