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

package server;

import device.AbstractDevice;
import device.Accelerometer;
import device.DeviceException;
import device.DevicePosition;
import device.DeviceType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.device.*;
import server.device.IRDevice.IRType;
import server.device.MouseEncoderDevice.MouseAxis;

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
    public static AbstractDevice[] readConfigFile(String fileName) throws IOException
    {
        List<AbstractDevice> list = new ArrayList<AbstractDevice>();
        Map<String,InterfaceKitDevice> ikList = new HashMap<String,InterfaceKitDevice>();

        File file = new File(fileName);        
        BufferedReader in = new BufferedReader(new FileReader(file));
        if(in.ready())
        {
            String line;
            while( (line = in.readLine()) != null)
            {
                if(line.startsWith(COMMENT_CHAR))
                {
                    continue;
                }
                String[] elements = line.split(NAME_SEPARATOR);
                if(elements.length == 2)
                {
                    String deviceID = elements[0];
                    String[] params = elements[1].split(PARAMS_SEPARATOR);                    

                    try
                    {
                        AbstractDevice device = null;
                        DeviceType type = DeviceType.valueOf(deviceID);
                        switch(type)
                        {
                            case MOTOR_CONTROL:
                                if(params.length >= 3)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    int index = Integer.parseInt(params[2]);
                                    device = new MotorControlDevice(name,serial,index);
                                }
                                break;
                            case SERVO_CONTROL:
                                if(params.length >= 6)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    int index = Integer.parseInt(params[2]);
                                    int min = Integer.parseInt(params[3]);
                                    int max = Integer.parseInt(params[4]);
                                    int start = Integer.parseInt(params[5]);
                                    device = new ServoControlDevice(name,serial,index,min,max,start);
                                }
                                break;
                            case ACCELEROMETER_SENSOR:
                                if(params.length >= 3)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    device = new AccelerometerDevice(name,serial);
                                    device.setDevicePosition(DevicePosition.valueOf(params[2]));
                                    list.add(((AccelerometerDevice)device).getAxisDevice(Accelerometer.Axis.X_AXIS));
                                    list.add(((AccelerometerDevice)device).getAxisDevice(Accelerometer.Axis.Y_AXIS));
                                    list.add(((AccelerometerDevice)device).getAxisDevice(Accelerometer.Axis.Z_AXIS));
                                }
                                break;
                            case ENCODER_SENSOR:
                                if(params.length >= 6)
                                {
                                    String name = params[0];
                                    String devFile = params[1];
                                    double radius = Double.parseDouble(params[2]);
                                    int tics = Integer.parseInt(params[3]);
                                    MouseAxis axis = MouseAxis.valueOf(params[4]);
                                    device = new MouseEncoderDevice(name,devFile,radius,tics,axis);
                                    device.setDevicePosition(DevicePosition.valueOf(params[5]));
                                }
                                break;
                            case INTERFACE_KIT:
                                if(params.length >= 2)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    device = new InterfaceKitDevice(name,serial);
                                    ikList.put(String.valueOf(serial), (InterfaceKitDevice)device);
                                }
                                break;
                            case SONAR_SENSOR:
                                if(params.length >= 5)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    InterfaceKitDevice ikDevice = ikList.get(String.valueOf(serial));
                                    if(ikDevice != null)
                                    {
                                        int input = Integer.parseInt(params[2]);
                                        int output = Integer.parseInt(params[3]);
                                        device = new SonarDevice(name,ikDevice,input,output);
                                        device.setDevicePosition(DevicePosition.valueOf(params[4]));
                                    }
                                    else
                                    {
                                        System.err.println("InterfaceKit " + serial + " not found");
                                    }
                                }
                                break;
                            case IR_SENSOR:
                                if(params.length >= 5)
                                {
                                    String name = params[0];
                                    int serial = Integer.parseInt(params[1]);
                                    InterfaceKitDevice ikDevice = ikList.get(String.valueOf(serial));
                                    if(ikDevice != null)
                                    {
                                        int input = Integer.parseInt(params[2]);
                                        IRType irType = IRType.valueOf(params[3]);
                                        device = IRDevice.getIRDevice(name,ikDevice,input,irType);
                                        device.setDevicePosition(DevicePosition.valueOf(params[4]));
                                    }
                                    else
                                    {
                                        System.err.println("InterfaceKit " + serial + " not found");
                                    }
                                }
                                break;
                            case CAMERA_SENSOR:
                                if(params.length >= 6)
                                {
                                    String name = params[0];
                                    String devFile = params[1];
                                    int width = Integer.parseInt(params[2]);
                                    int height = Integer.parseInt(params[3]);
                                    int compression = Integer.parseInt(params[4]);
                                    device = new CameraDevice(name,devFile,width,height,compression);
                                    device.setDevicePosition(DevicePosition.valueOf(params[5]));
                                }
                                break;
                            default:
                                System.err.println("Invalid device indentifier " + deviceID);
                                break;
                        }

                        if(device != null)
                        {
                            list.add(device);
                        }
                    }
                    catch(DeviceException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            in.close();
        }

        return list.toArray(new AbstractDevice[0]);
    }
}
